@echo off
REM Detect 64-bit vs 32-bit JVM automatically
java -d64 -version >nul 2>&1
IF %ERRORLEVEL%==0 (
    echo 64-bit JVM detected
) ELSE (
    echo 32-bit JVM detected
)

REM Run the program with native access enabled
java --enable-native-access=ALL-UNNAMED -cp "lib/*;." acm.program.BreakoutMain
pause
