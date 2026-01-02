@ECHO OFF

xas99.py -i -M -q -R -D cart=0 -L roadhunter.lst -E symbols.txt Source/road-hunter.a99 -o bin/ROADHUNT

IF %ERRORLEVEL% NEQ 0 GOTO END

xdm99.py roadhunter.dsk -X dssd -n ROADHUNTER
xdm99.py roadhunter.dsk -a bin/ROADHUNT bin/ROADHUNU bin/ROADHUNV
xdm99.py roadhunter.dsk -a Map/rdmap1.bin -n RDMAP1 -f INT/FIX128
xdm99.py roadhunter.dsk -a Map/rdmap2.bin -n RDMAP2 -f INT/FIX128
xdm99.py roadhunter.dsk -a Map/rdmap3.bin -n RDMAP3 -f INT/FIX128
xdm99.py roadhunter.dsk -a TI-files/LOAD -t

REM xas99.py -i -M -q -R -D cart=1 -L roadhunter.lst -E symbols.txt Source/road-hunter.a99 -o bin/ROADHUNT
REM java -jar tools/ea5tocart.jar bin\ROADHUNT "ROAD HUNTER" > make.log
REM copy /b bin\ROADHUNT8.bin + ^
REM     Cart\empty-4k.bin + Map\rdmap1.bin + ^
REM     Cart\empty-4k.bin + Map\rdmap2.bin + ^
REM     Cart\empty-4k.bin + Map\rdmap3.bin + ^
REM     roadhunter8.bin
REM java -jar tools/CopyHeader.jar roadhunter8.bin 60
REM jar -cvf roadhunter.rpk roadhunter8.bin layout.xml > make.log

:END
