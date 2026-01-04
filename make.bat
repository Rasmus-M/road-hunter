@ECHO OFF

REM Disk

xas99.py -i -M -q -R -D cart=0 -L roadhunter.lst -E symbols.txt Source/road-hunter.a99 -o bin/ROADHUNT

IF %ERRORLEVEL% NEQ 0 GOTO END

xas99.py -b -q Map/rdmap1.a99 -o Map/rdmap1.bin
xas99.py -b -q Map/rdmap2.a99 -o Map/rdmap2.bin
xas99.py -b -q Map/rdmap3.a99 -o Map/rdmap3.bin

xdm99.py roadhunter.dsk -X dssd -n ROADHUNTER
xdm99.py roadhunter.dsk -a bin/ROADHUNT bin/ROADHUNU bin/ROADHUNV
xdm99.py roadhunter.dsk -a Map/rdmap1.bin -n RDMAP1 -f INT/FIX128
xdm99.py roadhunter.dsk -a Map/rdmap2.bin -n RDMAP2 -f INT/FIX128
xdm99.py roadhunter.dsk -a Map/rdmap3.bin -n RDMAP3 -f INT/FIX128
xdm99.py roadhunter.dsk -a TI-files/LOAD -t

REM Cartridge

xas99.py -i -M -q -R -D cart=1 -L roadhunter-cart.lst -E symbols.txt Source/road-hunter.a99 -o bin/ROADHUNT
java -jar tools/ea5tocart.jar bin\ROADHUNT "ROAD HUNTER" > make.log

tools\sfk split 8064 -yes Map/rdmap1.bin bin/rdmap1
tools\sfk split 8064 -yes Map/rdmap2.bin bin/rdmap2
tools\sfk split 8064 -yes Map/rdmap3.bin bin/rdmap3

copy /b bin\ROADHUNT8.bin + ^
    Map\empty128.bin + bin\rdmap1.part1 + ^
    Map\empty128.bin + bin\rdmap1.part2 + Map\empty2304.bin + ^
    Map\empty128.bin + bin\rdmap2.part1 + ^
    Map\empty128.bin + bin\rdmap2.part2 + Map\empty2304.bin + ^
    Map\empty128.bin + bin\rdmap3.part1 + ^
    Map\empty128.bin + bin\rdmap3.part2 + Map\empty2304.bin + ^
    Map\empty.bin + ^
    Map\empty.bin + ^
    Map\empty.bin + ^
    Map\empty.bin + ^
    Map\empty.bin + ^
    Map\empty.bin ^
    roadhunter8.bin

java -jar tools/CopyHeader.jar roadhunter8.bin 60
jar -cvf roadhunter.rpk roadhunter8.bin layout.xml > make.log

:END
