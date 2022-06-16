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

cd %libDir%

echo Downloading libraries...

if not exist "spigot-1.12.2.jar" (
    if exist "%librariesDir%\spigot-1.12.2.jar" (
        copy "%librariesDir%\spigot-1.12.2.jar" . 1>nul
    ) else (
        java -jar ..\utils\FileDownloader.jar %tempDir%\spigot-1.12.2.jar https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/spigotmc/spigot-api/1.12.2-R0.1-SNAPSHOT/spigot-api-1.12.2-R0.1-20180712.012057-156.jar
        if not "!errorlevel!"=="0" (
            echo An error occurred while downloading spigot-1.12.2.jar 1>&2
            pause
            exit 1
        ) else (
            mkdir "%librariesDir%" 1>nul 2>nul
            move "%tempDir%\spigot-1.12.2.jar" "%librariesDir%\spigot-1.12.2.jar" 1>nul
            copy "%librariesDir%\spigot-1.12.2.jar" . 1>nul
        )
    )
)
if not exist "spigot-1.8.8-R0.1-SNAPSHOT-latest.jar" (
    if exist "%librariesDir%\spigot-1.8.8-R0.1-SNAPSHOT-latest.jar" (
        copy "%librariesDir%\spigot-1.8.8-R0.1-SNAPSHOT-latest.jar" . 1>nul
    ) else (
        java -jar ..\utils\FileDownloader.jar %tempDir%\spigot-1.8.8-R0.1-SNAPSHOT-latest.jar https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar
        if not "!errorlevel!"=="0" (
            echo An error occurred while downloading spigot-1.8.8-R0.1-SNAPSHOT-latest.jar 1>&2
            pause
            exit 1
        ) else (
            mkdir "%librariesDir%" 1>nul 2>nul
            move "%tempDir%\spigot-1.8.8-R0.1-SNAPSHOT-latest.jar" "%librariesDir%\spigot-1.8.8-R0.1-SNAPSHOT-latest.jar" 1>nul
            copy "%librariesDir%\spigot-1.8.8-R0.1-SNAPSHOT-latest.jar" . 1>nul
        )
    )
)
if not exist "worldedit-bukkit-7.2.2.jar" (
    if exist "%librariesDir%\worldedit-bukkit-7.2.2.jar" (
        copy "%librariesDir%\worldedit-bukkit-7.2.2.jar" . 1>nul
    ) else (
        java -jar ..\utils\FileDownloader.jar %tempDir%\worldedit-bukkit-7.2.2.jar https://dev.bukkit.org/projects/worldedit/files/3172946/download
        if not "!errorlevel!"=="0" (
            echo An error occurred while downloading worldedit-bukkit-7.2.2.jar 1>&2
            pause
            exit 1
        ) else (
            mkdir "%librariesDir%" 1>nul 2>nul
            move "%tempDir%\worldedit-bukkit-7.2.2.jar" "%librariesDir%\worldedit-bukkit-7.2.2.jar" 1>nul
            copy "%librariesDir%\worldedit-bukkit-7.2.2.jar" . 1>nul
        )
    )
)
if not exist "worldedit-bukkit-6.1.9.jar" (
    if exist "%librariesDir%\worldedit-bukkit-6.1.9.jar" (
        copy "%librariesDir%\worldedit-bukkit-6.1.9.jar" . 1>nul
    ) else (
        java -jar ..\utils\FileDownloader.jar %tempDir%\worldedit-bukkit-6.1.9.jar https://dev.bukkit.org/projects/worldedit/files/2597538/download
        if not "!errorlevel!"=="0" (
            echo An error occurred while downloading worldedit-bukkit-6.1.9.jar 1>&2
            pause
            exit 1
        ) else (
            mkdir "%librariesDir%" 1>nul 2>nul
            move "%tempDir%\worldedit-bukkit-6.1.9.jar" "%librariesDir%\worldedit-bukkit-6.1.9.jar" 1>nul
            copy "%librariesDir%\worldedit-bukkit-6.1.9.jar" . 1>nul
        )
    )
)

cd ..

where ant 1>nul 2>nul
if "!errorlevel!"=="0" (
    set ant=ant
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

echo Compiling project...
call %ant%
pause
