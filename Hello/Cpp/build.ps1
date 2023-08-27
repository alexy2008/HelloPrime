if (!(Test-Path -Path ./bin)) { New-Item -ItemType Directory -Path ./bin }
g++ --version
g++ CppHelloPrime.cpp -lpthread -lm -O3 -o ./bin/CppHelloPrime