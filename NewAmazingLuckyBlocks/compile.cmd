@echo off
setlocal enabledelayedexpansion
rem File generated with CompilerHelper

call .\gradlew.bat downloadLibraries && call .\gradlew.bat jar
pause
