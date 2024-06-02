# 例句了一些知名的恶意cmdlets字符串, 实际测试可以加载一下实际的cmdlets执行
$malcmdlets = "Add-Persistence", "Find-AVSignature", "Get-GPPAutologon", "Get-GPPPassword", "Get-HttpStatus", "Get-Keystrokes", "Get-SecurityPackages", "Get-TimedScreenshot", "Get-VaultCredential", "Get-VolumeShadowCopy", "Install-SSP", "Invoke-CredentialInjection", "Invoke-DllInjection", "Invoke-Mimikatz", "Invoke-NinjaCopy", "Invoke-Portscan", "Invoke-ReflectivePEInjection", "Invoke-ReverseDnsLookup", "Invoke-Shellcode", "Invoke-TokenManipulation", "Invoke-WmiCommand", "Mount-VolumeShadowCopy", "New-ElevatedPersistenceOption", "New-UserPersistenceOption", "New-VolumeShadowCopy", "Out-CompressedDll", "Out-EncodedCommand", "Out-EncryptedScript", "Out-Minidump", "PowerUp", "PowerView", "Remove-Comments", "Remove-VolumeShadowCopy", "Set-CriticalProcess", "Set-MasterBootRecord"
foreach ($cmdlets in $malcmdlets) {
    "function $cmdlets { Write-Host Pretending to invoke $cmdlets }"
}
foreach ($cmdlets in $malcmdlets) {
    $cmdlets
}