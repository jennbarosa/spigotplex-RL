import asyncio
import websockets

async def connect_to_server():
    uri = "ws://localhost:8755"  # Replace YOUR_PORT with the actual port number
    async with websockets.connect(uri, close_timeout=60, ping_interval=4) as websocket:
        greeting = await websocket.recv()
        print(f'C++ server opened with: {greeting}')
        while True:
            msg = input("Enter message: ")
            await websocket.send(msg)

            response = await websocket.recv()
            print(f"Received from server: {response}")

async def main():
    try:
        await connect_to_server()
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    asyncio.run(main())