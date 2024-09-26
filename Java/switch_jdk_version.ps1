# 切换到 JDK 8
$env:JAVA_HOME = "C:\Program Files\Java\jdk1.8.0_281"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:CLASSPATH = ".;$env:JAVA_HOME\lib"
Write-Output "Switched to JDK 8"

# 切换到 JDK 11
# $env:JAVA_HOME = "C:\Program Files\Java\jdk-11.0.10"
# $env:Path = "$env:JAVA_HOME\bin;$env:Path"
# $env:CLASSPATH = ".;$env:JAVA_HOME\lib"
# Write-Output "Switched to JDK 11"

# C:\Program Files\Microsoft\jdk-21.0.4.7-hotspot
# C:\Program Files\Java\jdk\openjdk-21.0.2
# C:\ProgramData\Oracle\Java\javapath