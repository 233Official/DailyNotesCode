from fastapi import FastAPI, Request
import uvicorn
import logging
from pathlib import Path

app = FastAPI()

@app.middleware("http")
async def return_200_ok(request: Request, call_next):
    response = await call_next(request)
    response.status_code = 200
    return response

@app.api_route("/{path_name:path}", methods=["GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"])
async def catch_all(request: Request, path_name: str):
    logging.info(f"Received request: {request.method} {request.url}")
    body = await request.body()
    logging.info(f"Request body: {body}")
    return {"message": "OK"}

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    
    # HTTPS配置
    CURRENT_DIR = Path(__file__).resolve().parent
    KEY_FILEPATH = CURRENT_DIR / "server.key"
    CERT_FILEPATH = CURRENT_DIR / "server.crt"

    uvicorn.run(
        app, 
        host="0.0.0.0", 
        port=443,
        ssl_keyfile=str(KEY_FILEPATH),
        ssl_certfile=str(CERT_FILEPATH)
    )