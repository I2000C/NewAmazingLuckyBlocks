@echo off
setlocal enabledelayedexpansion
call .\gradlew.bat --refresh-dependencies jar
pause
