param ([string]$lang)

Write-Host "��ʼ���� $lang" -ForegroundColor Green

switch ($lang) {
    "c#"    {dotnet.exe build Hi.csproj -o csbin -c Release}
    "vb"    {dotnet.exe build VbHi.vbproj -o vbbin -c Release}
    "java"  {javac JHiPrime.java -d bin}
#    "c++"   {cmake.exe --build cmake-build-release --target CppHiPrime -- -j 4}
    "c++"   { g++ CppHiPrime.cpp --static -O3 -o .\bin\CppHiPrime}
#    "c"     {cmake.exe --build cmake-build-release --target CHiPrime -- -j 4}
    "c"     {gcc CHiPrime.c -O3 -o .\bin\CHiPrime}
    "go"    {go.exe build -o .\bin\GohiPrime.exe GoHiPrime.go}
    "rust"  {rustc RsHiPrime.rs  --out-dir bin -C opt-level=3 -C debuginfo=0}

    Default {
        Write-Host "unknow language!" -ForegroundColor Red
        return -1
    }  
}

Write-Host "$lang �������" -ForegroundColor Yellow