@SET file=200\250\250

start java -jar dist\constellation.jar --parameters:data\%file%_em.txt --command:data\%file%_cmd.txt --output:data\%file%0_out  --method:0
start java -jar dist\constellation.jar --parameters:data\%file%_em.txt --command:data\%file%_cmd.txt --output:data\%file%1_out  --method:1
start java -jar dist\constellation.jar --parameters:data\%file%_em.txt --command:data\%file%_cmd.txt --output:data\%file%2_out  --method:2
start java -jar dist\constellation.jar --parameters:data\%file%_em.txt --command:data\%file%_cmd.txt --output:data\%file%3_out  --method:3

@timeout 60