@echo off

REM 设置当前工作目录的 .prefect 目录为 PREFECT_HOME
set PREFECT_HOME=%CD%\.prefect

REM 确保目录存在
if not exist "%PREFECT_HOME%" mkdir "%PREFECT_HOME%"

echo 已设置 PREFECT_HOME=%PREFECT_HOME%

REM 检查激活脚本是否存在
set ACTIVATE_FILE=.venv\Scripts\activate.bat
if exist "%ACTIVATE_FILE%" (
    findstr /c:"REM 自动设置 PREFECT_HOME" "%ACTIVATE_FILE%" >nul
    if errorlevel 1 (
        echo 修改 %ACTIVATE_FILE% 以在激活环境时自动设置 PREFECT_HOME...
        
        echo. >> "%ACTIVATE_FILE%"
        echo REM 自动设置 PREFECT_HOME >> "%ACTIVATE_FILE%"
        echo set PREFECT_HOME=%%~dp0..\.prefect >> "%ACTIVATE_FILE%"
        echo if not exist "%%PREFECT_HOME%%" mkdir "%%PREFECT_HOME%%" >> "%ACTIVATE_FILE%"
        echo echo 已自动设置 PREFECT_HOME=%%PREFECT_HOME%% >> "%ACTIVATE_FILE%"
        
        echo 配置已添加到激活脚本中。今后每次激活环境时将自动设置 PREFECT_HOME。
    ) else (
        echo 激活脚本已包含 PREFECT_HOME 设置，无需修改。
    )
) else (
    echo 警告：找不到虚拟环境激活脚本 (%ACTIVATE_FILE%)。请确保已创建 Poetry 虚拟环境。
)