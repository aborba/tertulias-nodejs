REM @ECHO OFF
SETLOCAL EnableDelayedExpansion
SET "arg1=%1"
IF NOT "!arg1!"=="" GOTO continuation
SET arg1="wip save"
:continuation
CLS
git add --all .
git commit -m %arg1% .
git push --all
