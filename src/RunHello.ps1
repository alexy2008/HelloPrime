param ([string]$lang, [string]$limit, [string]$page = "M", [int]$mode = 1, [int]$thread = 1, [int]$loop = 1, [bool]$build)

if ($lang.Equals("cs") -or $lang.Equals("c#")) {$lang = "Csharp"}
if ($lang.Equals("vb")) {$lang = "VisualBasic.Net"}
if ($lang.Equals("c++")) {$lang = "Cpp"}

switch ($page) {
    "H" {$page = "5000000"}
    "L" {$page = "1000000"}
    "M" {$page = "100000"}
    "S" {$page = "10000"}
    "T" {$page = "1000"}
    Default {}
}

$page = $page -replace "K", "000" -replace "M", "000000"
$limit = $limit -replace "K", "000" -replace "M", "000000" -replace "G", "000000000" -replace "T", "000000000000"


$ssum = $limit.ToString().Replace("000000000000","0000亿").Replace("00000000000","000亿").
            Replace("0000000000","00亿").Replace("000000000","0亿").Replace("00000000","亿").
            Replace("0000000","000万").Replace("000000","00万").Replace("00000","0万").Replace("0000","万")


Set-Location $lang

if ($build) {
    Write-Host "$lang 编译" -ForegroundColor Blue
    &.\build.bat}

Write-Host "$lang 开始计算 $ssum 以内素数" -ForegroundColor Green

for ($i = 0; $i -lt $loop; $i++) {
    &.\run.bat $limit $page $mode $thread
}

Set-Location ..

Write-Host "$lang $ssum 以内计算完毕" -ForegroundColor Yellow
