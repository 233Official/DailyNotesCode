from summer_modules.web_request_utils import getUserAgent
from summer_modules.utils import write_dict_to_json_file
from pathlib import Path
from summer_modules.ai.deepseek import translate_text
from summer_modules.logger import init_and_get_logger

CURRENT_DIR = Path(__file__).resolve().parent
logger = init_and_get_logger(CURRENT_DIR, "test_logger")

def test_logger():
    logger.debug("debug")
    logger.info("info")
    logger.warning("warning")
    logger.error("error")
    logger.critical("critical")

def test_write_dict_to_json_file():
    data = {"a": 1, "b": 2}
    filepath = CURRENT_DIR / "test.json"
    oneline_filepath = CURRENT_DIR / "test_oneline.json"
    write_dict_to_json_file(data, filepath)
    # with open(filepath, "r") as f:
    #     assert json.load(f) == data
    # filepath.unlink()
    write_dict_to_json_file(data, oneline_filepath, one_line=True)
    # with open(oneline_filepath, "r") as f:
    #     assert json.load(f) == data
    # oneline_filepath.unlink()

def test_translate_text():
    english_text = "Hello, how are you? I am learning Python programming."
    translate_text(english_text)

def main():
    # test_logger()
    # test_write_dict_to_json_file()
    test_translate_text()

if __name__ == "__main__":
    main()