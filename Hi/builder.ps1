param ([string]$lang)

Write-Host "开始编译 $lang" -ForegroundColor Green

switch ($lang) {
    "c#"    {dotnet.exe build Hi.csproj -o csbin -c Release}
    "vb"    {dotnet.exe build VbHi.vbproj -o vbbin -c Release}
    "java"  {javac JHiPrime.java -d jbin}
#    "c++"   {cmake.exe --build cmake-build-release --target CppHiPrime -- -j 4}
    "c++"   {g++ CHiPrime.cpp -o .\out\CppHiPrime}
#    "c"     {cmake.exe --build cmake-build-release --target CHiPrime -- -j 4}
    "c"     {g++ CHiPrime.c -o .\out\CHiPrime}
    "go"    {go.exe build -o .\out\GohiPrime.exe GoHiPrime.go}
    "rust"  {rustc RsHelloPrime.rs  --out-dir bin -C opt-level=3 -C debuginfo=0}
    Default {
        Write-Host "unknow language!" -ForegroundColor Red
        return -1
    }  
}

Write-Host "$lang 编译完成" -ForegroundColor Yellow