if (!(Test-Path -Path ./bin)) { New-Item -ItemType Directory -Path ./bin }
go version
go build -o ./bin/ GoHelloPrime.go