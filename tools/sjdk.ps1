switch ($args[0]) {
    "16" {$jhome = "D:\jdk16"}
    "15" {$jhome = "D:\jdk15"}
    "14" {$jhome = "D:\jdk14"}
    "11" {$jhome = "C:\Program Files\Java\jdk-11.0.2"}
}

$env:JAVA_HOME=$jhome
$env:Path=$jhome+"\bin" + ";" + $env:Path

java -version