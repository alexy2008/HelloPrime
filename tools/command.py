command = {
    'java': {
        'ver': 'java -version', 'pre': 'J',
        'build': 'javac -version && javac *.java -d bin -encoding UTF-8',
        'run': 'java -XX:AutoBoxCacheMax=20000 -Xms2048m -Xmx16000m -cp ./bin JHelloPrime %s %s %s %s'},
    'sc': {
        'ver': 'scala -version', 'name': 'Scala',
        'build': 'scalac ScHiPrime.scala -d bin',
        'run': 'scala -cp ./bin ScHiPrime %s %s %s %s'},

    'kt': {
        'ver': 'kotlin -version', 'name': 'Kotlin',
        'build': 'kotlinc KtHelloPrime.kt -d bin',
        'run': 'kotlin -cp ./bin KtHiPrime %s %s %s %s'},
    'c': {
        'ver': 'gcc --version',
        'build': 'gcc CHelloPrime.c -lm -O3 -o ./bin/CHelloPrime',
        'run': './bin/CHelloPrime %s %s %s %s'},
    'cpp': {
        'ver': 'g++ --version', 'name': 'C++', 'dir': 'Cpp',
        'build': 'g++ CppHelloPrime.cpp -lpthread -lm -O3 -o ./bin/CppHelloPrime',
        'run': './bin/CppHelloPrime %s %s %s %s'},
    'rust': {
        'ver': 'rustc --version', 'name': 'Rust',
        'build': 'rustc RsHelloPrime.rs  --out-dir bin -C opt-level=3 -C debuginfo=0',
        'run': './bin/RsHelloPrime %s %s %s %s'},
    'cs': {
        'ver': 'dotnet --version', 'name': 'C#', 'dir': 'CSharp',
        'build': 'dotnet build CsHelloPrime.csproj -o bin -c Release',
        'run': './bin/CsHelloPrime %s %s %s %s'},
    'vb': {
        'ver': 'dotnet --version', 'name': 'VisualBasic',
        'build': 'dotnet build VbHelloPrime.vbproj -o bin -c Release',
        'run': './bin/VbHelloPrime %s %s %s %s'},
    'go': {
        'ver': 'go version',
        'build': 'go build -o ./bin/ GoHelloPrime.go',
        'run': './bin/GoHelloPrime %s %s %s %s'},
    'swift': {
        'ver': 'swiftc --version', 'pre': 'Sw',
        'build': 'swiftc -O SwHelloPrime.swift -o bin/SwHelloPrime.exe',
        'run': './bin/SwHelloPrime %s %s %s %s'},
    'dart': {
        'ver': 'dart --version', 'pre': 'Da',
        'build': 'dart compile exe DaHelloPrime.dart -o bin/DaHelloPrime.exe',
        'run': './bin/DaHelloPrime %s %s %s %s'},
    'py': {
        'ver': 'python --version', 'name': 'Python',
        'run': 'python PyHelloPrime.py %s %s %s %s'},
    'rb': {
        'ver': 'ruby --version', 'name': 'Ruby',
        'run': 'ruby RbHelloPrime.rb %s %s %s %s'},
    'php': {
        'ver': 'php --version',
        'run': 'php PhpHelloPrime.php %s %s %s %s'},
    'groovy': {
        'ver': 'groovy --version', 'pre': 'Gv',
        'run': 'groovy GvHelloPrime %s %s %s %s'},
    'js': {
        'ver': 'node --version', 'name': 'JavaScript',
        'run': 'node JsHelloPrime.js %s %s %s %s'},
    'lua': {
        'ver': 'lua -v',
        'run': 'lua LHelloPrime.lua %s %s %s %s'},    
    'ts': {
        'ver': 'deno --version', 'name': 'TypeScript',
        'run': 'deno run TsHelloPrime.ts %s %s %s %s'},
    'julia': {
        'ver': 'julia -v',
        'run': 'julia -O3 --compile=all  JLHelloPrime.jl  %s %s %s %s'},
}