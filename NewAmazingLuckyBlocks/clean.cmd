@echo off
setlocal enabledelayedexpansion
rem File generated with BuildXMLCreator

set libDir=lib
mkdir %libDir% 1>nul 2>nul

set homeDir=%userprofile%
set baseDir=%userprofile%\.ant
set librariesDir=%baseDir%\cached_libraries
set antDirName=apache-ant
set antDir=%baseDir%\%antDirName%
set antFile=%antDir%\bin\ant
set tempDir=%baseDir%\temp

where ant 1>nul 2>nul
if "!errorlevel!"=="0" (
    for /f "delims=" %%i in ('where ant') do set ant=%%i
) else (
    if exist "%antFile%" (
        set ant="%antFile%"
    ) else (
        del /s /f /q "%antDir%" 1>nul 2>nul
        del /s /f /q "%tempDir%\%antDirName%" 1>nul 2>nul
        java -jar utils\FileDownloader.jar -u "%tempDir%\%antDirName%" http://archive.apache.org/dist/ant/binaries/apache-ant-1.10.12-bin.zip
        if not "!errorlevel!"=="0" (
            echo An error occurred while downloading ant 1>&2
            pause
            exit 2
        ) else (
            move "%tempDir%\%antDirName%\apache-ant-1.10.12" "%antDir%" 1>nul
            rd "%tempDir%\%antDirName%"
            set ant="%antFile%"
        )
    )
)

echo Cleaning project...
call %ant% clean
pause
