from summer_modules.web_request_utils import getUserAgent
from summer_modules.utils import write_dict_to_json_file
from pathlib import Path
from summer_modules.ai.deepseek import translate_text
from summer_modules.logger import init_and_get_logger
from summer_modules.vulnerability.cve.info import (
    get_cve_description,
    get_cve_info,
    test_get_cve_info_from_cve,
    test_get_cve_description,
)
from summer_modules.vulnerability.cve.poc import test_get_poc
from summer_modules.vulnerability.github_repo.nuclei import test_get_nuclei_cve_dict
from summer_modules.vulnerability.attck.attck_analyze import test_analyze_attck_info

# CNNVD
from summer_modules.vulnerability.cnnvd.info import (
    test_search_vul_on_cnnvd_by_cve_id_online,
    test_search_vul_on_cnnvd_by_cve_id_local,
    test_search_vul_on_cnnvd_by_cve_id,
    test_get_vul_info_from_cnnvd_by_cve_id_online,
    test_get_vul_info_from_cnnvd_by_cve_id_local,
    test_get_vul_info_from_cnnvd_by_cve_id
)

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
    # test_translate_text()

    # CVE
    # test_get_cve_info_from_cve()
    # test_get_cve_description()
    # test_get_poc()

    # Nuclei
    # test_get_nuclei_cve_dict()

    # ATT&CK
    # test_analyze_attck_info()

    # CNNVD
    # test_search_vul_on_cnnvd_by_cve_id_online()
    test_search_vul_on_cnnvd_by_cve_id_local()
    test_search_vul_on_cnnvd_by_cve_id()
    # test_get_vul_info_from_cnnvd_by_cve_id_online()
    test_get_vul_info_from_cnnvd_by_cve_id_local()
    test_get_vul_info_from_cnnvd_by_cve_id()


if __name__ == "__main__":
    main()
