# Check command line arguments
if ($args.Count -ne 1 -and $args.Count -ne 3) {
    Write-Host -ForegroundColor Red "Usage: $PSCommandPath <directory> [-d <container_name>]"
    exit 1
}

# Get the first command line argument
$subdir = $args[0]

if ($args.Count -eq 3) {
    if ($args[1] -ne "-d") {
        Write-Host -ForegroundColor Red "Usage: $PSCommandPath <directory> [-d <container_name>]"
        exit 1
    }
    $d = $args[2]
    
    $commad = "docker run -it --name hello_prime --rm -v .:/helloprime ${d} bash -c 'grep PRETTY_NAME /etc/os-release && cd /helloprime && ./build ${subdir} '"
    Write-Host -ForegroundColor Cyan "Executing docker: $commad"
    Invoke-Expression $commad
    exit 0
}

# Check if the current directory contains the specified subdirectory
if (!(Test-Path -Path $subdir -PathType Container)) {
    Write-Host -ForegroundColor Red "Error: The current directory does not contain the '$subdir' subdirectory"
    exit 1
}

# Check if the specified subdirectory contains the 'bin' subdirectory
$bin_dir = Join-Path -Path $subdir -ChildPath "bin"
if (!(Test-Path -Path $bin_dir -PathType Container)) {
    Write-Host -ForegroundColor Yellow "Warning: The '$subdir/bin' directory does not exist, creating it..."
    New-Item -Path $bin_dir -ItemType Directory
}

# Check if the specified subdirectory contains the 'command.toml' file
$command_toml = Join-Path -Path $subdir -ChildPath "command.toml"
if (!(Test-Path -Path $command_toml -PathType Leaf)) {
    Write-Host -ForegroundColor Red "Error: The '$subdir' subdirectory does not contain the 'command.toml' file"
    exit 1
}

# Read the 'command.toml' file and parse the values of the 'ver' and 'build' configuration items
$ver = (Get-Content -Path $command_toml | Where-Object { $_ -match '^ver\s*=' } | ForEach-Object { $_ -replace '^ver\s*=', '' } | Select-Object -First 1).Trim(' ','"')
$build = (Get-Content -Path $command_toml | Where-Object { $_ -match '^build\s*=' } | ForEach-Object { $_ -replace '^build\s*=', '' } | Select-Object -First 1).Trim(' ','"')

# Set the working directory to the specified subdirectory
Write-Host -ForegroundColor Cyan "Working directory: '$subdir'"
Push-Location -Path $subdir

# Execute the system commands stRed in the 'ver' and 'build' variables
if ($ver) {
    Write-Host -ForegroundColor Green "Executing ver command: $ver"
    Invoke-Expression $ver
} else {
    Write-Host -ForegroundColor Red "Error: The ver command is not configRed"
}

if ($build) {
    Write-Host -ForegroundColor Green "Executing build command: $build"
    Invoke-Expression $build
} else {
    Write-Host -ForegroundColor Red "Error: The build command is not configRed"
}

Pop-Location

Write-Host -ForegroundColor Cyan "Operation completed"