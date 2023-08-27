if (!(Test-Path -Path ./bin)) { New-Item -ItemType Directory -Path ./bin }
dotnet --version
dotnet build CsHelloPrime.csproj -o bin -c Release