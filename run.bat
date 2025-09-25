@echo off
REM Run Breakout from jar using the bundled JRE

"%~dp0jre\bin\java.exe" -cp "%~dp0acm.jar;%~dp0Breakout.jar" Breakout

pause
