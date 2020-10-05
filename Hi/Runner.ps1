param ([string]$lang,[string]$limit,[string]$page="M",[int]$loop=1,[bool]$build)

switch ($page) {
    "H" {$page = "10000000"}
    "L" {$page = "1000000"}
    "M" {$page = "100000"}
    "S" {$page = "10000"} 
    "T" {$page = "1000"}         
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
            .\csbin\hi $limit $page
        }
      }
    "vb" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\vbbin\VbHi $limit $page
        }
    }
    "java" {
        for ($i = 0; $i -lt $loop; $i++) {
            java -cp .\bin JHiPrime $limit $page
        }
    }
    "c++" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\bin\CppHiPrime $limit $page
        }
    }
    "c" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\bin\CHiPrime $limit $page
        }
    }
    "go" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\bin\GoHiPrime $limit $page
        }
    }
    "rust" {
        for ($i = 0; $i -lt $loop; $i++) {
            .\bin\RsHiPrime $limit $page
        }
    }

    Default {
        Write-Host "unknow language!" -ForegroundColor Red
        return -1
    } 
}

Write-Host "$lang $ssum 以内。计算完毕" -ForegroundColor Green
