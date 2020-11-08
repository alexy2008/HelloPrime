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


$ssum = $limit.ToString().Replace("000000000000","0000��").Replace("00000000000","000��").
            Replace("0000000000","00��").Replace("000000000","0��").Replace("00000000","��").
            Replace("0000000","000��").Replace("000000","00��").Replace("00000","0��").Replace("0000","��")


Set-Location $lang

if ($build) {
    Write-Host "$lang ����" -ForegroundColor Blue
    &.\build.bat}

Write-Host "$lang ��ʼ���� $ssum ��������" -ForegroundColor Green

for ($i = 0; $i -lt $loop; $i++) {
    &.\run.bat $limit $page $mode $thread
}

Set-Location ..

Write-Host "$lang $ssum ���ڼ������" -ForegroundColor Yellow
