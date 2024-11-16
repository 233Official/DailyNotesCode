from fastapi import FastAPI, File, UploadFile, Request
from fastapi.responses import FileResponse, HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from pathlib import Path
import uvicorn

app = FastAPI()

# 设置一个文件夹来存储上传的文件
BASE_DIR = Path(__file__).resolve().parent
UPLOAD_DIRECTORY = BASE_DIR / "uploads"
UPLOAD_DIRECTORY.mkdir(exist_ok=True)
STATIC_DIRECTORY = BASE_DIR / "static"
STATIC_DIRECTORY.mkdir(exist_ok=True)
TEMPLATES_DIRECTORY = BASE_DIR / "templates"
TEMPLATES_DIRECTORY.mkdir(exist_ok=True)
UVICORN_LOG_CONFIG_JSON_PATH = BASE_DIR / "uvicorn_logger_config.json"

# 静态文件和模板
app.mount("/static", StaticFiles(directory=STATIC_DIRECTORY), name="static")
templates = Jinja2Templates(directory=TEMPLATES_DIRECTORY)


@app.get("/", response_class=HTMLResponse)
async def main(request: Request):
    files = [
        str(file.relative_to(UPLOAD_DIRECTORY))
        for file in UPLOAD_DIRECTORY.rglob("*")
        if file.is_file()
    ]
    return templates.TemplateResponse(
        "index.html", {"request": request, "files": files}
    )


@app.post("/uploadfile/")
async def create_upload_file(file: UploadFile = File(...)):
    file_location = UPLOAD_DIRECTORY / file.filename
    with open(file_location, "wb+") as file_object:
        file_object.write(file.file.read())
    return {"info": f"file '{file.filename}' saved at '{file_location}'"}


@app.get("/download/{file_path:path}")
async def download_file(file_path: str):
    file_location = UPLOAD_DIRECTORY / Path(file_path)
    if file_location.exists():
        return FileResponse(str(file_location))
    return {"error": "File not found"}


if __name__ == "__main__":

    uvicorn.run(
        app, host="0.0.0.0", port=8000, log_config=str(UVICORN_LOG_CONFIG_JSON_PATH)
    )

#
