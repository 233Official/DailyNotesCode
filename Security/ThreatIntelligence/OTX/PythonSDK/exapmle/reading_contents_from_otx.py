from OTXv2 import OTXv2
from OTXv2 import IndicatorTypes
import toml
from pathlib import Path

CURRENT_DIR = Path(__file__).parent.resolve()
CONFIG_TOML_FILEPATH = CURRENT_DIR / "../config.toml"
CONFIG_TOML = toml.load("config.toml")
OTX_API_KEY = CONFIG_TOML["otx_api_key"]

otx = OTXv2(OTX_API_KEY)
# Get all the indicators associated with a pulse
# indicators = otx.get_pulse_indicators("pulse_id")
indicators = otx.get_pulse_indicators("681d9408ff6991f0c7882db9")
for indicator in indicators:
    print(f"{indicator["indicator"]} + {indicator["type"]}")
# Get everything OTX knows about google.com
otx.get_indicator_details_full(IndicatorTypes.DOMAIN, "google.com")
