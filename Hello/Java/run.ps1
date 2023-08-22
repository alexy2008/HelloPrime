param(
    [string]$arg1 = 10000,
    [string]$arg2 = 1000,
    [int]$m = 0,
    [int]$t = 1,
    [int]$r = 1
)

$command = "java -cp ./bin JHelloPrime"

if ($arg1.StartsWith("e")) { $arg1 = "1" + $arg1 }
if ($arg2.StartsWith("e")) { $arg2 = "1" + $arg2 }

$limit = [double]$arg1
$page = [double]$arg2

$command = $command  + " " + $limit + " " + $page + " " + $m  + " " + $t

Write-Output ("Run the command : " + $command)

for ($i = 0; $i -lt $r; $i++) { Invoke-Expression $command } 