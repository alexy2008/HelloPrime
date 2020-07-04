param ([string]$lang)

Write-Host "开始编译 $lang" -ForegroundColor Green

switch ($lang) {
    "c#"    {dotnet.exe build Hi.csproj -o csbin -c Release}
    "vb"    {dotnet.exe build VbHi.vbproj -o vbbin -c Release}
    "java"  {javac.exe JHiPrime.java -d jbin}
    "c++"   {cmake.exe --build cmake-build-release --target CppHiPrime -- -j 4}
    "c"     {cmake.exe --build cmake-build-release --target CHiPrime -- -j 4}
    "go"    {go.exe build -o .\out\GohiPrime.exe GoHiPrime.go}
    Default {
        Write-Host "unknow language!" -ForegroundColor Red
        return -1
    }  
}

Write-Host "$lang 编辑完成" -ForegroundColor Yellow