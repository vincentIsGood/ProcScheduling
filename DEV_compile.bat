@echo off

set first=src/com/vincentcodes/proc/*.java
:: .java files are in encoding UTF-8
javac --release 16 -encoding UTF-8 -d classes -cp ./lib/*;./src/ %first%

pause