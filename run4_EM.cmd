rem call :launch 200 280
rem call :launch 200 281
rem call :launch 200 290
call :launch 300 310

@timeout 60

:: end doing things!
goto:eof

:launch basePath,test,out)
    set "basePath=%~1"
    set "test=%~2"
    set "realPath=\%basePath%\%test%
    start java -jar dist\constellation.jar --parameters:data\%realPath%\%test%_em.txt --command:data\%realPath%\%test%_cmd.txt --output:data\%realPath%\%test%_0_out  --method:0
    start java -jar dist\constellation.jar --parameters:data\%realPath%\%test%_em.txt --command:data\%realPath%\%test%_cmd.txt --output:data\%realPath%\%test%_1_out  --method:1
    start java -jar dist\constellation.jar --parameters:data\%realPath%\%test%_em.txt --command:data\%realPath%\%test%_cmd.txt --output:data\%realPath%\%test%_2_out  --method:2
    start java -jar dist\constellation.jar --parameters:data\%realPath%\%test%_em.txt --command:data\%realPath%\%test%_cmd.txt --output:data\%realPath%\%test%_3_out  --method:3
    goto:eof