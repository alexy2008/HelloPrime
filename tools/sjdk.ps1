switch ($args[0]) {
    "g17" {$jhome = "D:\Program\graalvm17"} 
    "20.0.2" {$jhome = "D:\Program\Java\jdk-20.0.2"}
    "20" {$jhome = "D:\Program\Java\jdk-20"}
    "17" {$jhome = "D:\jdk17"}
    "17.0.8" {$jhome = "D:\Program\Java\jdk-17"}
    "17.0.1" {$jhome = "D:\Program\Java\jdk-17.0.1"}
    "16" {$jhome = "D:\jdk16"}
    "15" {$jhome = "D:\jdk15"}
    "14" {$jhome = "D:\jdk14"}
    "11" {$jhome = "C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot"}
}

$env:JAVA_HOME=$jhome
$env:Path=$jhome+"\bin" + ";" + $env:Path

java -version