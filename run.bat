@echo off
REM Compile Breakout.java including ACM jar
javac -cp ".;lib\acm.jar" Breakout.java
REM Run the program
java -cp ".;lib\acm.jar" Breakout
pause
