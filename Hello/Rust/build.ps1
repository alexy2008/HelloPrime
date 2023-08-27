if (!(Test-Path -Path ./bin)) { New-Item -ItemType Directory -Path ./bin }
rustc --version
rustc RsHelloPrime.rs  --out-dir bin -C opt-level=3 -C debuginfo=0