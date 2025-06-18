@echo off
REM Package mock applications as Windows native applications
echo Packaging mock applications as Windows native applications...

REM Create output directory for executables
mkdir target\executables 2>nul

echo.
echo Packaging AS400 Terminal Mock...
jpackage ^
    --input target\mock-apps ^
    --name AS400TerminalMock ^
    --main-jar AS400TerminalMock.jar ^
    --main-class com.automation.mock.AS400TerminalMock ^
    --dest target\executables ^
    --type app-image ^
    --app-version 1.0 ^
    --description "AS400 Terminal Mock Application for Automation Testing" ^
    --vendor "Automation Framework"

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to package AS400TerminalMock
    exit /b 1
)

echo.
echo Packaging SAP GUI Mock...
jpackage ^
    --input target\mock-apps ^
    --name SAPGUIMock ^
    --main-jar SAPGUIMock.jar ^
    --main-class com.automation.mock.SAPGUIMock ^
    --dest target\executables ^
    --type app-image ^
    --app-version 1.0 ^
    --description "SAP GUI Mock Application for Automation Testing" ^
    --vendor "Automation Framework"

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to package SAPGUIMock
    exit /b 1
)

echo.
echo ================================
echo Packaging completed successfully!
echo ================================
echo Native applications created in target\executables\:
dir target\executables\ /AD /B
echo.
echo You can now run:
echo - target\executables\AS400TerminalMock\AS400TerminalMock.exe
echo - target\executables\SAPGUIMock\SAPGUIMock.exe
