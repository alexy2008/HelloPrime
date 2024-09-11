param(
    [string]$arg2 = 100000,
    [string]$arg3 = 1000,
    [int]$m = 0,
    [int]$t = 1,
    [int]$r = 1
)

$subdir = $args[0]

# 检查当前目录下是否存在子目录 $1
if (-Not (Test-Path -Path $subdir -PathType Container)) {
    Write-Host "错误：当前目录下不存在 '$subdir' 子目录"
    Exit 1
}

# 检查 $subdir 下面是否存在 command.toml 文件
$command_toml = Join-Path -Path $subdir -ChildPath "command.toml"
if (-Not (Test-Path -Path $command_toml -PathType Leaf)) {
    Write-Host "错误：'$subdir' 下面不存在 'command.toml' 文件"
    Exit 1
}

# 读取 command.toml 文件并解析出 ver 和 run 配置项的值
$ver = (Get-Content $command_toml | Where-Object { $_ -match '^ver\s*=' } | Select-Object -First 1 | ForEach-Object { $_.Split('=')[1].Trim() })
$run = (Get-Content $command_toml | Where-Object { $_ -match '^run\s*=' } | Select-Object -First 1 | ForEach-Object { $_.Split('=')[1].Trim() })

# 获取其他命令行参数
$limit = $args[1] ?? 100000
$page = $args[2] ?? 1000
$m = 0
$t = 1
$r = 1

# 处理命令行选项
$optstring = $args[3]
$index = 4
while ($index -lt $args.Count) {
    $opt = $args[$index - 1]
    $optarg = $args[$index]
    switch ($opt) {
        "-t" { $t = $optarg }
        "-m" { $m = $optarg }
        "-r" { $r = $optarg }
    }
    $index += 2
}

Write-Host "m,t,r: $m,$t,$r"

# 处理特殊格式
if ($limit.StartsWith("e")) { $limit = "1$limit" }
if ($page.StartsWith("e")) { $page = "1$page" }

$limit = [Math]::Round([double]$limit)
$page = [Math]::Round([double]$page)
$run = "$run $limit $page $m $t"

# 设置工作目录为 subdir
Write-Host "工作目录: '$subdir'"
Set-Location $subdir -ErrorAction Stop

# 执行 ver 和 run 变量里存储的系统指令
if ($ver) {
    Write-Host "执行 ver 指令: $ver"
    Invoke-Expression $ver
} else {
    Write-Host "错误：未配置 ver 指令"
}

if ($run) {
    Write-Host "执行 run 指令: $run"
    for ($i = 0; $i -lt $r; $i++) {
        Invoke-Expression $run
    }
} else {
    Write-Host "错误：未配置 run 指令"
}

Write-Host "操作完成"