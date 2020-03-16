param ([string]$lang,[string]$limit,[string]$page="M",[int]$loop=1,[bool]$build)


switch ($page) {
    "H" {
        $page = "10000000"
        break
        }
    "L" {
        $page = "1000000"
        break
        }
    "M" {
        $page = "100000"
        break
        }
    "S" {
        $page = "10000"
        break
        } 
    "T" {
        $page = "1000"
        break
        }         
    Default {}
}

$page = $page -replace "K","000" -replace "M","000000"
$limit = $limit -replace "K","000" -replace "M","000000" -replace "G","000000000" -replace "T","000000000000"

# [long]$sum = [long]::Parse($page) * [long]::Parse($repeat)
$ssum = $limit.ToString().Replace("000000000000","0000亿").Replace("00000000000","000亿").
            Replace("0000000000","00亿").Replace("000000000","0亿").Replace("00000000","亿").
            Replace("0000000","000万").Replace("000000","00万").Replace("00000","0万").Replace("0000","万")

if ($build) {.\builder.ps1 $lang}

Write-Host "$lang 开始计算 $ssum 以内" -ForegroundColor Green 
switch ($lang) {
    "c#" {        
        for ($i = 0; $i -lt $loop; $i++) {
            dotnet csbin\hi.dll $page $repeat    
        }        
        break
      }
      "java" {
        for ($i = 0; $i -lt $loop; $i++) {
            java -cp .\jbin JHiPrime $limit $page   
        }        
        break
      }
      "c++" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\cmake-build-release\CppHiPrime.exe $page $repeat 
        }        
        break
      }
      "c" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\cmake-build-release\CHiPrime.exe $page $repeat   
        }        
        break
      }
      "go" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\out\go_hello_prime.exe $limit $page  
        }        
        break
      }

    Default {
        Write-Host "unknow language!" -ForegroundColor Red
        return -1
    } 
}

Write-Host "$lang $ssum 以内。计算完毕" -ForegroundColor Yellow
