@echo off
REM Compilation Script for Vehicle Service Management App with REST API
REM This script compiles all Java files

echo Compiling Vehicle Service Management App...

cd src

REM Compile all Java files with API support
javac -cp .;../lib/mysql-connector-j-8.0.33.jar;../lib/json-20231013.jar gui/*.java model/*.java dao/*.java service/*.java api/*.java Main.java

if %errorlevel% equ 0 (
    echo.
    echo Compilation successful!
    echo.
    echo To run the application, execute: run.bat
) else (
    echo.
    echo Compilation failed! Check the errors above.
    echo.
    echo Make sure you have:
    echo   - lib/mysql-connector-j-8.0.33.jar
    echo   - lib/json-20231013.jar
    pause
)

pause
