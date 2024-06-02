# 要写入 Debug 流必须使用 Write-Debug + -Debug 参数
Write-Debug "This is a test debug" -Debug
# 要写入 Verbose 流必须使用 Write-Verbose + -Verbose 参数
Write-Verbose "This is a test verbose" -Verbose
Write-Warning "This is a test warning" 
# Write-Host 与 Write-Information 都会写入 Information 流, 不过 Write-Host 会直接输出到控制台, 而 Write-Information 只会在 -InformationAction 参数设置为 Continue 时才会输出到控制台
Write-Host "This is a test host" 
Write-Information "This is a test information"
Write-Information "This is a test information" -InformationAction Continue
Write-Output "This is a test output"
Write-Error "This is a test error"
