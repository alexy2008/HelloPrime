param ([string]$lang)

Write-Host "开始编译 $lang" -ForegroundColor Green

switch ($lang) {
    "c#co"  {dotnet build CscoHiPrime.csproj -o bin -c Release}
	"c#" 	{csc /out:./bin/CsHiPrime.exe /debug- /optimize+ CsHiPrime.cs}
    "vbco"  {dotnet build VbcoHiPrime.vbproj -o bin -c Release}
	"vb"	{vbc /out:./bin/VbHiPrime.exe /debug- /optimize+ VbHiPrime.vb /nologo}
    "java"  {javac JHiPrime.java -d bin}
    "java-g" {D:\graalvm11\bin\javac JHiPrime.java -d bin}
    "kt"    {kotlinc KtHiPrime.kt -d bin}
    "ktjar" {kotlinc KtHiPrime.kt -include-runtime -d bin/KtHiPrime.jar}
#    "c++"   {cmake.exe --build cmake-build-release --target CppHiPrime -- -j 4}
    "c++"   { g++ CppHiPrime.cpp --static -O3 -o ./bin/CppHiPrime}
#    "c"     {cmake.exe --build cmake-build-release --target CHiPrime -- -j 4}
    "c"     {gcc CHiPrime.c -O3 -o ./bin/CHiPrime}
    "go"    {go build -o .\bin\GohiPrime.exe GoHiPrime.go}
    "rust"  {rustc RsHiPrime.rs  --out-dir bin -C opt-level=3 -C debuginfo=0}
    "dart"  {dart compile exe DHiPrime.dart -o bin\DHiPrime.exe}
	"js"  	{tsc JsHiPrime.ts}

    Default {
        Write-Host "unknow language!" -ForegroundColor Red
        return -1
    }  
}

Write-Host "$lang 编译完成" -ForegroundColor Yellow