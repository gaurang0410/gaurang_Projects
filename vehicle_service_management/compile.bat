@echo off
echo.
echo VehicleFlow - Compiling...
echo ============================

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin -cp "lib\*" src\Main.java src\api\*.java src\dao\*.java src\gui\*.java src\model\*.java src\service\*.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)

echo.
echo Compilation successful!
pause
