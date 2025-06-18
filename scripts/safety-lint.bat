@echo off
setlocal enabledelayedexpansion

REM 🛡️ Universal Desktop Automation Framework - Safety Linting Script (Windows)
REM This script validates feature files for dangerous global operations
REM Usage: scripts\safety-lint.bat [feature-file-path]

echo 🛡️  UNIVERSAL DESKTOP AUTOMATION FRAMEWORK - SAFETY LINTER
echo ================================================================
echo.

set "FEATURE_DIR=%~1"
if "%FEATURE_DIR%"=="" set "FEATURE_DIR=src\test\resources\features"

set VIOLATIONS_FOUND=0
set TOTAL_FILES_CHECKED=0
set EXIT_CODE=0

echo 📁 Scanning: %FEATURE_DIR%
echo 🎯 Target: Feature files (*.feature)
echo.

REM Check if path exists
if not exist "%FEATURE_DIR%" (
    echo ❌ Path not found: %FEATURE_DIR%
    exit /b 1
)

REM Function to check a single feature file
:check_feature_file
set "file=%~1"
echo 📄 Checking: %file%

REM Check for dangerous global key combinations
findstr /n /i "I press.*key combination.*ALT+F4" "%file%" | findstr /v "managed application" >nul
if %errorlevel% equ 0 (
    echo 🚨 CRITICAL: Global ALT+F4 detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I press.*key combination.*CTRL+ALT+DEL" "%file%" >nul
if %errorlevel% equ 0 (
    echo 🚨 CRITICAL: CTRL+ALT+DEL detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I press.*key combination.*WIN+R" "%file%" | findstr /v "managed application" >nul
if %errorlevel% equ 0 (
    echo 🚨 CRITICAL: Global WIN+R detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I press.*key combination.*ALT+TAB" "%file%" | findstr /v "managed application" >nul
if %errorlevel% equ 0 (
    echo ⚠️  HIGH: Global ALT+TAB detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I press.*key combination" "%file%" | findstr /v "managed application" >nul
if %errorlevel% equ 0 (
    echo ⚠️  HIGH: Unscoped key combination detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

REM Check for dangerous global typing
findstr /n /i "I type.*shutdown" "%file%" >nul
if %errorlevel% equ 0 (
    echo 🚨 CRITICAL: Shutdown command detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I type.*format.*C:" "%file%" >nul
if %errorlevel% equ 0 (
    echo 🚨 CRITICAL: Format command detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I type.*taskkill.*explorer" "%file%" >nul
if %errorlevel% equ 0 (
    echo 🚨 CRITICAL: Explorer kill command detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I type" "%file%" | findstr /v "managed application" | findstr /v "in the active field" >nul
if %errorlevel% equ 0 (
    echo ⚠️  HIGH: Global typing without context detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

REM Check for global key presses
findstr /n /i "I press \"ENTER\" key$" "%file%" | findstr /v "managed application" >nul
if %errorlevel% equ 0 (
    echo ⚡ MEDIUM: Global ENTER key press detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

findstr /n /i "I press \"WIN\" key" "%file%" | findstr /v "managed application" >nul
if %errorlevel% equ 0 (
    echo ⚠️  HIGH: Global Windows key press detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

REM Check for unscoped text waiting
findstr /n /i "I wait for text.*to appear" "%file%" | findstr /v "managed application" | findstr /v "in region" >nul
if %errorlevel% equ 0 (
    echo ⚡ MEDIUM: Unscoped text waiting detected in %file%
    set /a VIOLATIONS_FOUND+=1
)

set /a TOTAL_FILES_CHECKED+=1
goto :eof

REM Main execution
if exist "%FEATURE_DIR%\*.feature" (
    for %%f in ("%FEATURE_DIR%\*.feature") do (
        call :check_feature_file "%%f"
    )
) else if "%FEATURE_DIR:~-8%"==".feature" (
    call :check_feature_file "%FEATURE_DIR%"
) else (
    REM Search subdirectories
    for /r "%FEATURE_DIR%" %%f in (*.feature) do (
        call :check_feature_file "%%f"
    )
)

echo.
echo ================================================================
echo 📊 SAFETY LINT REPORT
echo ================================================================
echo Files Checked: %TOTAL_FILES_CHECKED%
echo Violations Found: %VIOLATIONS_FOUND%

if %VIOLATIONS_FOUND% equ 0 (
    echo ✅ ALL CLEAR - No safety violations detected!
    echo 🛡️  Your feature files are SAFE for execution.
    exit /b 0
) else (
    echo 🚨 SAFETY VIOLATIONS DETECTED!
    echo ⚠️  Review and fix violations before running tests.
    echo.
    echo 💡 Quick Fixes:
    echo    • Add 'in managed application "app_name"' to scoped operations
    echo    • Replace global key combinations with managed application context
    echo    • Ensure applications are registered before operations
    echo    • Use 'terminate managed application' instead of ALT+F4
    echo.
    echo 📖 For detailed guidance, see: SAFETY.md
    exit /b 1
)
