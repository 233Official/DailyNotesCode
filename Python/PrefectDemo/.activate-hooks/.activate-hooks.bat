@echo off

REM ���õ�ǰ����Ŀ¼�� .prefect Ŀ¼Ϊ PREFECT_HOME
set PREFECT_HOME=%CD%\.prefect

REM ȷ��Ŀ¼����
if not exist "%PREFECT_HOME%" mkdir "%PREFECT_HOME%"

echo ������ PREFECT_HOME=%PREFECT_HOME%

REM ��鼤��ű��Ƿ����
set ACTIVATE_FILE=.venv\Scripts\activate.bat
if exist "%ACTIVATE_FILE%" (
    findstr /c:"REM �Զ����� PREFECT_HOME" "%ACTIVATE_FILE%" >nul
    if errorlevel 1 (
        echo �޸� %ACTIVATE_FILE% ���ڼ����ʱ�Զ����� PREFECT_HOME...
        
        echo. >> "%ACTIVATE_FILE%"
        echo REM �Զ����� PREFECT_HOME >> "%ACTIVATE_FILE%"
        echo set PREFECT_HOME=%%~dp0..\.prefect >> "%ACTIVATE_FILE%"
        echo if not exist "%%PREFECT_HOME%%" mkdir "%%PREFECT_HOME%%" >> "%ACTIVATE_FILE%"
        echo echo ���Զ����� PREFECT_HOME=%%PREFECT_HOME%% >> "%ACTIVATE_FILE%"
        
        echo ��������ӵ�����ű��С����ÿ�μ����ʱ���Զ����� PREFECT_HOME��
    ) else (
        echo ����ű��Ѱ��� PREFECT_HOME ���ã������޸ġ�
    )
) else (
    echo ���棺�Ҳ������⻷������ű� (%ACTIVATE_FILE%)����ȷ���Ѵ��� Poetry ���⻷����
)