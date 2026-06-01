@echo off
title Push Group 2 Quiz App to GitHub
echo ========================================================
echo   PROQUIZ ASSESSMENT SUITE - GROUP 2 GIT PUSH HELPER
echo ========================================================
echo.
echo This script will securely push your code to:
echo https://github.com/Ef-kays/quiz-application-group.git
echo.
echo If prompted, please click "Sign in with your browser"
echo or enter your personal access token to authorize the upload.
echo.
echo ========================================================
echo.

git push -u origin main

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Push failed. Please check your GitHub connection or credentials.
) else (
    echo.
    echo [SUCCESS] Your code and Javadocs have been successfully pushed to GitHub!
)

echo.
pause
