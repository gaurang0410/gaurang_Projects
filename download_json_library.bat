@echo off
REM Download JSON Library Helper Script
REM This script downloads the required JSON library for the API

echo.
echo ===================================
echo   JSON Library Downloader
echo ===================================
echo.
echo This script will download org.json library
echo from Maven Central Repository
echo.

REM Check if curl is available
curl --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: curl command not found!
    echo Please download manually from:
    echo https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar
    echo.
    echo Save it to: lib\json-20231013.jar
    pause
    exit /b 1
)

REM Check if lib directory exists
if not exist "lib" (
    echo Creating lib directory...
    mkdir lib
)

REM Download the JSON library
echo Downloading json-20231013.jar...
echo.

curl -L "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar" -o "lib\json-20231013.jar"

if %errorlevel% equ 0 (
    echo.
    echo ===================================
    echo   Download Successful!
    echo ===================================
    echo.
    echo File saved to: lib\json-20231013.jar
    echo.
    echo You can now compile and run your project:
    echo   1. Run compile.bat
    echo   2. Run run.bat
    echo.
) else (
    echo.
    echo ===================================
    echo   Download Failed!
    echo ===================================
    echo.
    echo Error: Could not download the file
    echo.
    echo Manual download:
    echo   1. Visit: https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar
    echo   2. Save to: c:\Users\Gaurang Khanolkar\OneDrive\Desktop\oopj\project\lib\json-20231013.jar
    echo.
)

pause
