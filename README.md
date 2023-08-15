# **Hello Prime !**
Explore the table of prime Numbers within 1 trillion using more than 20 programming languages.

使用20种语言，探索一万亿以内的素数表

# **最新更新**
Hi目录下是单线程实现，总共完成了 C，C++，Rust，Java，C#，Go，Python，Ruby，PHP，VB.net，Kotlin，Swift，Groovy，Dart，JavaScript，TypeScript，Scala，Lua共18种语言。计划再搞个Julia和R，凑20种。

Hello目录下是多线程实现，总共完成了C++,Java,C#,Go,Rust 5种语言。

tools目录下用Python写了一个cli的shell，写的很烂，打算推倒重来。

这个项目的定位，几次折腾，最早是想做语言特性的展现，后来一度追求极简，多一行代码都要压缩，再后来又追求性能，越快越好。现在想想，还是回归初心，做语言特性的展示吧，性能测试方面，其实我也很感兴趣，但是打算开个新的开源项目DevBench——面向开发者的基准测试工具。

## **背景**
一直很羡慕那些通晓好几国语言的大神。作为一个码农，虽然没有能力做到英语，法语，西班牙语随时换台，谈笑风生。但不妨试一下Java，Python，CSharp随心切换，四面开花。

也许你在网上也看到过不少《用100种语言编写HelloWorld》之类的帖子，这玩意大抵相当于《如何用100种语言说 I love U》。从学会说一句‘ti amo,ti vogliobene’到能在佛罗伦萨的小酒馆里撩到妹子，中间可能还隔了3.5个罗永浩。我们至少得把难度提升到大一新生的100字自我介绍水平上来。

所有，Hello World太简单了点，让我们一起来试试Hello Prime吧！

## **目标**

我们试图通过使用不同的程序设计语言来解决同一个问题，达到展现该语言基本特性和并测试其性能的目的。我们希望这段程序具备以下特质：

- 展现该语言的基础语法结构，选择、循环的使用
- 展现基本的数据类型，数组，类型转换，数值处理、字符串处理、格式化输出等特征

- 展现该语言针对面向对象的设计，类，方法的使用

- 如果可能，展现该语言不同其他语言的特色之处

- 在不同语言中的实现应保持算法逻辑的一致，以便于对比
- 单一文件，不引用第三方库和框架
- 跨平台，Windows，Linux，MacOS都能跑

- 具备一定的性能测试能力，主要测试语言本身的能力，避免硬盘IO，减少内存占用

- 这段程序不能太长，最好不要超过100行，核心代码50行为宜

- 最后一点，这程序得有点意思，不要搞什么十万个随机数求和之类的

## **算法**

综上所述，我最后选择了计算素数这个题目。这个题目有两问：

**1. 给出M计算M以内素数的个数。**

**2. 给出N计算从2开始第N的素数。**

打开度娘，这样的代码铺天盖地，尤其是第一问，基本上各个语言的版本都有。那我们为什么要把这大二的题目再做一遍呢？

因为我们目标是：**一万亿**，对就是1000000000000，别数了12个零。先剧透一个：1E12以内共有素数：**三百七十六亿零七百九十一万两千零一十八个**

关于素数的算法，较简单的有这样几种：

最简单的就是试除法，或者叫定义法，就是按照素数定义去一一试除，在Java或者其他支持Lambda的语言中，这个其实只需要一行代码,例如：
```
IntStream.range(2,1000)
.filter(x->IntStream.range(2,(int)Math.sqrt(x)+1)
.filter(t->x%t==0).count()==0)
.forEach(x->System.out.print(x+","));
```
这种算法写法简单，效率极低，百万以上就基本上跑不动。

更常用的就是筛选法，埃拉托色尼筛和欧拉筛，基本原理就是下面这张动图：

![image](https://github.com/alexy2008/HelloPrime/blob/master/img/prime.gif)

但是，筛选法有个问题，就是典型的空间换时间，速度快，但内存占用太大，跑个10亿以上基本上就把内存吃完了。

所以，我们最后的选择是将两种筛选结合起来，改造改进，分区分片进行筛选。目前的算法在我的笔记本上，已经可以跑到1万亿了。

## **开发**

花了三天的时间，实现了10种语言，先发布出来。说是10种，多少也有凑数之嫌。时间主要花在安装和搭建环境上，。



Java和他的朋友们——Kotlin，Groovy。首先写了Java版，自动转kt，换成Groovy也基本不改啥

C#和他的伙计——VB.net。用Java版改C#版，比想象的容易多了，两个语言太像了，然后用工具转的VB.net

C++和他的大哥——C 。C++版遇到了传说中的内存泄露问题，windows下环境搭建也复杂，再改C语言版就容易了。令人意外的是C++版竟然还没有Java版快

Python独立写的，动态语言用起来就是爽啊，尤其是刚写完C++程序，就是运行速度感人

Go这个是全新学的。数据类型转换让人头疼。

Ruby这个基本上也是全新学的。比较容易，就是容易跟Python混

之所以选择这10中，因为刚好装了JetBrains全家桶，这几个都是原生支持。不得不说JetBrains全家桶，那是真香。什么VS code,什么宇宙第一IDE都是浮云

![image](https://github.com/alexy2008/HelloPrime/blob/master/img/JetBrains.png)


## **计划**

接下来计划实现：Julia，R，Clojure, Elixir, F#, ……

你有什么感兴趣的语言，欢迎加入我的项目




