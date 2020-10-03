#!/bin/bash
lang=$1

echo -e "\033[32m开始编译$lang \033[0m"

case $lang in
    "java")
        javac JHiPrime.java -d out
        ;;
    "c")
        gcc CHiPrime.c -o .\out\CHiPrime
        ;;
    "c++")
        g++ CppHiPrime.cpp -o .\out\CppHiPrime
        ;;
    *)
        echo -e "\033[31m不支持的语言 \033[0m"
esac

echo -e "\033[33m$lang编译完成 \033[0m"