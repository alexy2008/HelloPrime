# 检查命令行参数
if ($args.Count -ne 1) {
    Write-Host "Usage: $MyInvocation.MyCommand <directory>"
    Exit 1
}

# 获取第一个命令行参数
$subdir = $args[0]

# 检查当前目录下是否存在子目录 $1
if (-Not (Test-Path -Path $subdir -PathType Container)) {
    Write-Host "错误：当前目录下不存在 '$subdir' 子目录"
    Exit 1
}

# 进一步检查 $1 下面是否存在 'bin' 子目录
$bin_dir = Join-Path -Path $subdir -ChildPath "bin"
if (-Not (Test-Path -Path $bin_dir -PathType Container)) {
    New-Item -ItemType Directory -Path $bin_dir
}

# 检查 $subdir 下面是否存在 command.toml 文件
$command_toml = Join-Path -Path $subdir -ChildPath "command.toml"
if (-Not (Test-Path -Path $command_toml -PathType Leaf)) {
    Write-Host "错误：'$subdir' 下面不存在 'command.toml' 文件"
    Exit 1
}

# 读取 command.toml 文件并解析出 ver 和 build 配置项的值
$ver = (Get-Content $command_toml | Where-Object { $_ -match '^ver\s*=' } | Select-Object -First 1 | ForEach-Object { $_.Split('=')[1].Trim() })
$build = (Get-Content $command_toml | Where-Object { $_ -match '^build\s*=' } | Select-Object -First 1 | ForEach-Object { $_.Split('=')[1].Trim() })

# 设置工作目录为 subdir
Write-Host "工作目录: '$subdir'"
Set-Location $subdir -ErrorAction Stop

# 执行 ver 和 build 变量里存储的系统指令
if ($ver) {
    Write-Host "执行 ver 指令: $ver"
    Invoke-Expression $ver
} else {
    Write-Host "错误：未配置 ver 指令"
}

if ($build) {
    Write-Host "执行 build 指令: $build"
    Invoke-Expression $build
} else {
    Write-Host "错误：未配置 build 指令"
}

Write-Host "操作完成"