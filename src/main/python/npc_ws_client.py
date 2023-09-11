import asyncio
import websockets

class WebSocketClient:
    def __init__(self, server_url):
        self.server_url = server_url
        self.response_data = asyncio.Queue()
        self.connected = False

    async def connect(self):
        if not self.connected:
            self.websocket = await websockets.connect(self.server_url, close_timeout=60, ping_interval=4)
            self.connected = True
            asyncio.create_task(self.handle_messages())

    async def handle_messages(self):
        while self.connected:
            try:
                message = await self.websocket.recv()
                await self.response_data.put(message)
            except websockets.ConnectionClosedError:
                self.connected = False
                await self.connect()  # Reconnect when connection is closed

    async def send_message(self, message) -> str:
        if not self.connected:
            await self.connect()

        await self.websocket.send(message)

        response = await self.response_data.get()
        return response