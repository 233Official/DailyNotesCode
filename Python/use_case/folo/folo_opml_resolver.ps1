#!/usr/bin/env pwsh
<#
  利用 PowerShell 的 XML 能力重写 Folo 导出的 OPML, 无需 Python 环境。
#>

[CmdletBinding()]
param(
    [Parameter(Mandatory = $false, HelpMessage = "显示脚本帮助")]
    [switch] $Help,

    [Parameter(Mandatory = $true, HelpMessage = "自部署 RSSHub 的基础地址")]
    [Alias("url", "rsshub_url")]
    [string] $RsshubUrl,

    [Parameter(Mandatory = $true, HelpMessage = "RSSHub 配置的 ACCESS_KEY")]
    [Alias("key", "access_key")]
    [string] $AccessKey,

    [Parameter(Mandatory = $true, HelpMessage = "需要处理的 OPML 文件路径")]
    [Alias("file", "opml_filepath")]
    [string] $OpmlFile,

    [Parameter(Mandatory = $false, HelpMessage = "输出 OPML 文件路径, 默认在原文件名后追加 _resolved")]
    [Alias("out", "output_filepath")]
    [string] $OutputFile
)

function Show-Usage {
    Write-Host @"
用法示例:
  pwsh ./folo_opml_resolver.ps1 -url http://rsshub.self.top:8080 `
    -key dascwe `
    -file D:\Downloads\follow.opml `
    -out D:\Downloads\follow_custom.opml
"@
}

if ($Help) {
    Show-Usage
    return
}

if (-not (Test-Path -LiteralPath $OpmlFile -PathType Leaf)) {
    throw "找不到 OPML 文件: $OpmlFile"
}

try {
    $baseUri = [Uri]::new($RsshubUrl, [UriKind]::Absolute)
} catch {
    throw "RSSHub 地址无效: $RsshubUrl"
}

if ($baseUri.Scheme -notin @("http", "https")) {
    throw "RSSHub 地址必须以 http:// 或 https:// 开头"
}
if ($baseUri.Query -or $baseUri.Fragment) {
    throw "RSSHub 基础地址不应包含查询参数或片段"
}

$normalizedPath = $baseUri.AbsolutePath.TrimEnd("/")
if (-not $normalizedPath) {
    $normalizedPath = ""
}
$normalizedBase = "{0}://{1}{2}" -f $baseUri.Scheme, $baseUri.Authority, $normalizedPath

if (-not $OutputFile) {
    $directory = Split-Path -LiteralPath $OpmlFile -Parent
    $nameWithoutExt = [IO.Path]::GetFileNameWithoutExtension($OpmlFile)
    $ext = [IO.Path]::GetExtension($OpmlFile)
    if (-not $ext) {
        $ext = ".opml"
    }
    $OutputFile = Join-Path -Path $directory -ChildPath ("{0}_resolved{1}" -f $nameWithoutExt, $ext)
}

$parentDir = Split-Path -LiteralPath $OutputFile -Parent
if ($parentDir -and -not (Test-Path -LiteralPath $parentDir -PathType Container)) {
    throw "输出目录不存在: $parentDir"
}

Add-Type -AssemblyName System.Web

[xml] $document = Get-Content -LiteralPath $OpmlFile -Encoding UTF8 -Raw
$outlineNodes = $document.SelectNodes("//outline[@xmlUrl]")

$replacements = 0

foreach ($node in $outlineNodes) {
    $url = $node.GetAttribute("xmlUrl")
    if ([string]::IsNullOrWhiteSpace($url)) {
        continue
    }
    if (-not $url.StartsWith("https://rsshub.app")) {
        continue
    }

    $suffix = $url.Substring("https://rsshub.app".Length)
    $rebuilt = "$normalizedBase$suffix"

    $builder = [UriBuilder]::new($rebuilt)
    $query = [System.Web.HttpUtility]::ParseQueryString($builder.Query)
    $query.Remove("key")
    $query["key"] = $AccessKey
    $builder.Query = $query.ToString()

    $node.SetAttribute("xmlUrl", $builder.Uri.AbsoluteUri)
    $replacements++
}

if ($replacements -eq 0) {
    Write-Host "未发现以 https://rsshub.app 开头的订阅, 原文件保持不变。"
    return
}

$settings = [System.Xml.XmlWriterSettings]::new()
$settings.Encoding = [System.Text.UTF8Encoding]::new($false)
$settings.Indent = $false

$writer = [System.Xml.XmlWriter]::Create($OutputFile, $settings)
try {
    $document.WriteTo($writer)
} finally {
    $writer.Dispose()
}

Write-Host "已在 $OutputFile 写入 $replacements 条替换后的订阅。"
