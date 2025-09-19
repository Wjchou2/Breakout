#!/bin/bash
# Compile Breakout.java including ACM jar
javac -cp ".:lib/acm.jar" Breakout.java
# Run the program
java -cp ".:lib/acm.jar" Breakout
