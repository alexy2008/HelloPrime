if (!(Test-Path -Path ./bin)) { New-Item -ItemType Directory -Path ./bin }
javac -version
javac *.java -d bin -encoding UTF-8