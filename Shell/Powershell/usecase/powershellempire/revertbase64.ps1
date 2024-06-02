$revert_enc_cmd = "AUGA4BQZA4CAkBQYAAHAlBAdA8GAuBAIAQHAzBQaAwEA0BgbAUGAtBQdAcGAyBQQA0CAgAQZAQHAhBQZAIHAjBAIAUGAtBQYA4EAtAAIAMHAzBQZAMGAvBgcAAHAfBgMAMDAuBQaAcHAgAAaAQHAhBAUA0CAgAAZA8GAoBAdAUGANBQaA0GAXBQLAUGArBwbAYHAuBQS"
$charArray = $revert_enc_cmd.ToCharArray()
[Array]::Reverse($charArray)
$reverted = -join $charArray
Write-Host "enc_cmd: $reverted"
# 使用 powershell -enc 命令执行
powershell -enc $reverted
