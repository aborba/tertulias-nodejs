@ECHO OFF
SET /p message="Enter Git commit message: "
echo %message%
sync.bat "%message%"