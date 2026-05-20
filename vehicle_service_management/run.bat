@echo off
echo.
echo VehicleFlow - Running...
echo =======================

if not exist bin\Main.class (
    echo Main.class not found. Compiling first...
    call compile.bat
)

java -cp "bin;lib\*" Main

if %errorlevel% neq 0 (
    echo.
    echo Application exited with an error.
    pause
    exit /b %errorlevel%
)
