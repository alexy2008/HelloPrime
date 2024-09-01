if (!(Test-Path -Path ./bin)) { New-Item -ItemType Directory -Path ./bin }
javac -version
$command = "javac *.java -d bin -encoding UTF-8"
Write-Output ("Run the command : " + $command)
Invoke-Expression $command 