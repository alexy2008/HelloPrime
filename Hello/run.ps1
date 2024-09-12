param(
    [string]$subdir,
    [string]$limit = 100000,
    [string]$page = 1000,
    [int]$m = 0,
    [int]$t = 1,
    [int]$r = 1
)

# 检查当前目录是否有子目录 $subdir
if (-Not (Test-Path -Path $subdir -PathType Container)) {
    Write-Host -ForegroundColor Red "Error: Subdirectory '$subdir' does not exist in the current directory"
    Exit 1
}

# 检查 $subdir 是否有 command.toml 文件
$command_toml = Join-Path -Path $subdir -ChildPath "command.toml"
if (-Not (Test-Path -Path $command_toml -PathType Leaf)) {
    Write-Host -ForegroundColor Red "Error: 'command.toml' file does not exist in '$subdir'"
    Exit 1
}

# 读取 command.toml 文件并解析 ver 和 run 配置值
$ver = (Get-Content $command_toml | Where-Object { $_ -match '^ver\s*=' } | Select-Object -First 1 | ForEach-Object { $_.Split('=')[1].Trim().Trim('"') })
$run = (Get-Content $command_toml | Where-Object { $_ -match '^run\s*=' } | Select-Object -First 1 | ForEach-Object { $_.Split('=')[1].Trim().Trim('"') })

# 处理特殊格式
if ($limit.StartsWith("e")) { $limit = "1$limit" }
if ($page.StartsWith("e")) { $page = "1$page" }

$limit = [Math]::Round([double]$limit)
$page = [Math]::Round([double]$page)
$run = "$run $limit $page $m $t"

# 设置工作目录为 $subdir
Write-Host -ForegroundColor Cyan "Working directory: '$subdir'"
Push-Location -Path $subdir

# 执行存储在变量中的 ver 和 run 命令
if ($ver) {
    Write-Host -ForegroundColor Green "Executing ver command: $ver"
    Invoke-Expression $ver
} else {
    Write-Host -ForegroundColor Red "Error: Ver command not configured"
}

if ($run) {
    Write-Host -ForegroundColor Green "Executing run command: $run"
    for ($i = 0; $i -lt $r; $i++) {
        Invoke-Expression $run
    }
} else {
    Write-Host -ForegroundColor Red "Error: Run command not configured"
}

Pop-Location

Write-Host -ForegroundColor Cyan "Operation completed"