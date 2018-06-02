rem call :launch 200 280
rem call :launch 200 281
rem call :launch 200 290
call :launch 320 320 1

@timeout 60

:: end doing things!
goto:eof

:launch (constell,test, method)
    set constell=%1
    set test=%2
    set method=%3
    set "prog=start java -jar ..\..\..\dist\constellation.jar"
    %prog% --parameters:%constell%_em.txt --command:%test%_cmd.txt --output:%test%_0_out  --method:%method%
    goto:eof
