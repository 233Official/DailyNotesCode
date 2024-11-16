from fastapi import FastAPI, File, UploadFile, Form, HTTPException, Request
from fastapi.responses import FileResponse, HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from pathlib import Path
import uvicorn
import hashlib
import json
from pydantic import BaseModel

app = FastAPI()


class StartUploadRequest(BaseModel):
    filename: str
    filesize: int
    chunk_size: int


# 设置文件夹
BASE_DIR = Path(__file__).resolve().parent
UPLOAD_DIRECTORY = BASE_DIR / "uploads"
UPLOAD_DIRECTORY.mkdir(exist_ok=True)
TEMP_DIRECTORY = BASE_DIR / "temp"
TEMP_DIRECTORY.mkdir(exist_ok=True)
TEMPLATES_DIRECTORY = BASE_DIR / "templates"
TEMPLATES_DIRECTORY.mkdir(exist_ok=True)
STATIC_DIRECTORY = BASE_DIR / "static"
STATIC_DIRECTORY.mkdir(exist_ok=True)

# 静态文件和模板
app.mount("/static", StaticFiles(directory=STATIC_DIRECTORY), name="static")
templates = Jinja2Templates(directory=TEMPLATES_DIRECTORY)

# 记录上传会话
uploads_in_progress = {}


@app.get("/", response_class=HTMLResponse)
async def main(request: Request):
    files = [
        str(file.relative_to(UPLOAD_DIRECTORY))
        for file in UPLOAD_DIRECTORY.rglob("*")
        if file.is_file()
    ]
    return templates.TemplateResponse(
        "chunked_file_server.html", {"request": request, "files": files}
    )


@app.get("/get_max_chunk_size/")
async def get_max_chunk_size():
    # 假设服务器支持的最大分块大小为 5MB
    max_chunk_size = 5 * 1024 * 1024
    return {"max_chunk_size": max_chunk_size}


@app.post("/start_upload/")
async def start_upload(request: StartUploadRequest):
    filename = request.filename
    filesize = request.filesize
    chunk_size = request.chunk_size

    # 创建上传会话ID
    upload_id = hashlib.md5(f"{filename}-{filesize}".encode()).hexdigest()
    temp_dir = TEMP_DIRECTORY / upload_id
    temp_dir.mkdir(parents=True, exist_ok=True)

    # 加载已接收的分块信息
    meta_file = temp_dir / "meta.json"
    if meta_file.exists():
        with open(meta_file, "r") as f:
            upload_info = json.load(f)
    else:
        upload_info = {
            "filename": filename,
            "filesize": filesize,
            "chunk_size": chunk_size,
            "received_chunks": {},
        }
        # 保存元数据
        with open(meta_file, "w") as f:
            json.dump(upload_info, f)
    uploads_in_progress[upload_id] = upload_info

    # 返回已接收且完整的分块列表
    received_chunks = [
        int(chunk_index)
        for chunk_index, valid in upload_info["received_chunks"].items()
        if valid
    ]

    return {"upload_id": upload_id, "received_chunks": received_chunks}


@app.post("/upload_chunk/")
async def upload_chunk(
    upload_id: str = Form(...),
    chunk_index: int = Form(...),
    chunk_hash: str = Form(...),
    chunk: UploadFile = File(...),
):
    if upload_id not in uploads_in_progress:
        raise HTTPException(status_code=404, detail="上传会话不存在")
    upload_info = uploads_in_progress[upload_id]
    temp_dir = TEMP_DIRECTORY / upload_id
    meta_file = temp_dir / "meta.json"
    chunk_filename = temp_dir / f"{chunk_index}.part"

    # 检查文件块是否已经存在且完整
    if (
        str(chunk_index) in upload_info["received_chunks"]
        and upload_info["received_chunks"][str(chunk_index)] == True
        and chunk_filename.exists()
    ):
        # 文件块已存在且完整，直接返回成功响应
        return {"status": f"分块 {chunk_index} 已存在且完整，跳过"}

    # 接收并保存文件块
    data = await chunk.read()
    with open(chunk_filename, "wb") as chunk_file:
        chunk_file.write(data)

    # 计算文件块的 SHA-256 哈希值
    sha256_hash = hashlib.sha256()
    sha256_hash.update(data)
    calculated_hash = sha256_hash.hexdigest()

    # 验证哈希值
    if calculated_hash != chunk_hash:
        # 删除损坏的文件块
        chunk_filename.unlink()
        return {"status": f"分块 {chunk_index} 的哈希校验失败，已删除，请重新上传"}

    # 更新已接收的分块信息为有效
    upload_info["received_chunks"][str(chunk_index)] = True

    # 保存元数据
    with open(meta_file, "w") as f:
        json.dump(upload_info, f)

    # 检查是否所有分块都已接收且有效
    total_chunks = (
        upload_info["filesize"] + upload_info["chunk_size"] - 1
    ) // upload_info["chunk_size"]
    if len(upload_info["received_chunks"]) == total_chunks and all(
        upload_info["received_chunks"].values()
    ):
        assemble_file(upload_id)
        del uploads_in_progress[upload_id]
    return {"status": f"分块 {chunk_index} 已接收并验证"}


def assemble_file(upload_id):
    upload_info = uploads_in_progress[upload_id]
    filename = upload_info["filename"]
    temp_dir = TEMP_DIRECTORY / upload_id
    file_location = UPLOAD_DIRECTORY / filename
    total_chunks = (
        upload_info["filesize"] + upload_info["chunk_size"] - 1
    ) // upload_info["chunk_size"]
    with open(file_location, "wb") as assembled_file:
        for chunk_index in range(total_chunks):
            chunk_filename = temp_dir / f"{chunk_index}.part"
            with open(chunk_filename, "rb") as chunk_file:
                assembled_file.write(chunk_file.read())
            chunk_filename.unlink()
    # 删除元数据文件和临时目录
    (temp_dir / "meta.json").unlink()
    temp_dir.rmdir()


# ...（其他路由和代码）

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
