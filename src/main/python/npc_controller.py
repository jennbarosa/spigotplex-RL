from enum import Enum
import json
import asyncio
import requests
from npc_ws_client import WebSocketClient
import atexit

class NpcActionSpace(Enum):
    FORWARD = 0
    BACKWARD = 1
    LEFT = 2
    RIGHT = 3
    JUMP = 4
    START_SPRINTING = 5
    STOP_SPRINTING = 6
    YAW_UP = 7
    YAW_DOWN = 8
    PITCH_UP = 9
    PITCH_DOWN = 10


class HttpMethod(Enum):
    GET = 0
    POST = 1


class NpcEnvControllerApi():
    def __init__(self, host):
        self.host = host
    
    def request(self, endpoint: str, method: HttpMethod, payload="") -> requests.Response:
        url = f"{self.host}{endpoint}"
        return requests.get(url) if method == HttpMethod.GET else requests.post(url, data=payload)
    

class NpcEnvControllerSocket():
    def __init__(self, host, endpoint):
        self.client = WebSocketClient(f"{host}{endpoint}")


class NpcEnvController():
    def __init__(self, port: int):
        self.port = port
        self.host = f"localhost:{port}"
        self.api = NpcEnvControllerApi(f"http://{self.host}")
        self.socket = NpcEnvControllerSocket(f"ws://{self.host}", endpoint="/npc_rl")
        self.loop = asyncio.get_event_loop()
        self._ws_connect()
        
        
    def apply_action(self, action: int):
        return self.loop.run_until_complete(self._ws_apply_action(action))

    def reset_env(self):
        return self.loop.run_until_complete(self._ws_reset_env())

    def _http_apply_action(self, action: int):
        response: requests.Response = self.api.request(f"/step?action={action}", HttpMethod.GET)
        if response.status_code != 200:
            print(f"Failed to apply Npc action '{action}'")
            return None
        return response.json()
    
    def _http_reset_env(self):
        response: requests.Response = self.api.request("/reset", HttpMethod.GET)
        if response.status_code != 200:
            print(f"Failed to reset environment ({response.status_code}): {response.content}")
            return None
        return response.json()
    
    async def _ws_connect(self):
        await self.socket.client.connect()

    async def _ws_apply_action(self, action: int):
        response_raw = await self.socket.client.send_message(f"step:{action}")
        return json.loads(response_raw)

    async def _ws_reset_env(self):
        response_raw = await self.socket.client.send_message("reset")
        return json.loads(response_raw)