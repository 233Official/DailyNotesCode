# 替换为您要计算 MD5 的文件路径
$filePath = "D:\Repo\Github\Self\DailyNotesCode\Powershell\Hash\calc_hash.ps1"

# 使用 Get-FileHash 计算 MD5 哈希
$md5Hash = Get-FileHash -Path $filePath -Algorithm MD5

# 输出 MD5 哈希值
$md5Hash.Hash
