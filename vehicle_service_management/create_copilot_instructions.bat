@echo off
REM Create .github directory and copilot-instructions.md file

if not exist ".github" mkdir ".github"

echo # Vehicle Service Management System - Developer Guide> ".github\copilot-instructions.md"
echo.>> ".github\copilot-instructions.md"
echo ## Build ^& Run Commands>> ".github\copilot-instructions.md"
echo.>> ".github\copilot-instructions.md"
echo ### Prerequisites>> ".github\copilot-instructions.md"
echo - Java JDK 8+>> ".github\copilot-instructions.md"
echo - MySQL Server 5.7+>> ".github\copilot-instructions.md"
echo - MySQL JDBC Driver (mysql-connector-j-8.0.33.jar) in `lib/`>> ".github\copilot-instructions.md"
echo - JSON Library (json-20231013.jar) in `lib/`>> ".github\copilot-instructions.md"
echo.>> ".github\copilot-instructions.md"
echo Setup complete! Check .github\copilot-instructions.md
echo.
echo Note: The file has been initialized. Please see the full content I'll create separately.
pause
