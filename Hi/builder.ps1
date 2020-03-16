param ([string]$lang)

Write-Host "ø™ º±‡“Î $lang" -ForegroundColor Green

switch ($lang) {
    "c#" {
        dotnet build Hi.csproj -o csbin -c Release
        break
      }
      "java" {
        javac JHiPrime.java -d jbin
        break
      }
      "c++" {
        cmake.exe --build cmake-build-release --target CppHiPrime -- -j 4
        break
      }
      "c" {
        cmake.exe --build cmake-build-release --target CHiPrime -- -j 4
        break
      }
      "go" {
        go.exe build -o .\out\go_hello_prime.exe GoHiPrime.go
        break
      }
      
    Default {
        Write-Host "unknow language!" -ForegroundColor Red
        return -1
    }  
}

Write-Host "$lang ±‡“ÎÕÍ±œ" -ForegroundColor Yellow