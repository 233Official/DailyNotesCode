import logging
import colorlog

# 配置日志记录
log_colors = {
    "DEBUG": "cyan",
    "INFO": "green",
    "WARNING": "yellow",
    "ERROR": "red",
    "CRITICAL": "bold_red",
}

formatter = colorlog.ColoredFormatter(
    "%(log_color)s%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    log_colors=log_colors,
)

handler = logging.StreamHandler()
handler.setFormatter(formatter)

file_handler = logging.FileHandler("app.log")
file_handler.setFormatter(
    logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
)

logging.basicConfig(level=logging.DEBUG, handlers=[file_handler, handler])
# logging.basicConfig(level=logging.INFO, handlers=[file_handler, handler])


logger = logging.getLogger(__name__)
