# coding=utf-8
import platform
import click
import subprocess
import os
import glob

command = {'java': {'ver': 'java -version',
                    'build': 'javac -version && javac JHiPrime.java -d bin',
                    'run': 'java -cp ./bin JHiPrime %s %s'},
           'c': {'ver': 'gcc --version',
                 'build': 'gcc CHiPrime.c -lm -O3 -o ./bin/CHiPrime',
                 'run': './bin/CHiPrime %s %s'}}

is_windows = True
osname = 'windows'
launch = ''
launch_cmd = 'for /l %%i in (1,1,%s) do @%s'
launch_sh = 'for i in $(seq %s);do %s;done'

def check(lang):
    curpath = '.'
    if len(glob.glob(r'%s' % lang))>0 :
        curpath = './%s' % lang

    if len(glob.glob(r'%s/*Hello*' % lang))>0 :
        print(glob.glob(r'%s/*Hello*' % lang))




@click.group()
def cli():
    global is_windows, osname, launch
    osname = platform.system()
    is_windows = (osname == 'Windows')

    if is_windows:
        launch = launch_cmd
    else:
        launch = launch_sh

    g = glob.glob(r'*Hi*')
    print(len(g))

    print(os.getcwd())  # 获取当前工作目录路径
    print(os.path.abspath('.'))  # 获取当前工作目录路径
    print(os.path.abspath('src/bin'))  # 获取当前目录文件下的工作目录路径
    print(os.path.abspath('..'))  # 获取当前工作的父目录 ！注意是父目录路径
    print(os.path.abspath(os.curdir))  # 获取当前工作目录路径

    click.secho('欢迎HiPrime CLI for %s' % osname, fg='blue', bg='black')
    click.secho('https://www.deepinjava.com', fg='yellow', underline=True)
    pass


@cli.command(help='编译源代码')
@click.argument('lang')
def build(lang):
    click.echo('开始编译%s' % lang)
    p = subprocess.Popen(command[lang]['build'], shell=True, stdout=subprocess.PIPE, universal_newlines=True)
    while p.poll() is None:
        out = p.stdout.readline()
        print(out, end="")
    click.echo('%s编译完成' % lang)


@cli.command(help='运行程序')
@click.argument('lang')
@click.option('--limit', '-l', default='1000', help='计算范围')
@click.option('--page', '-p', default='100', help='页面大小')
@click.option('--mode', '-m', default=0, help='运行模式')
@click.option('--thread', '-t', default=1, help='线程数')
@click.option('--repeat', '-r', default=1, help='执行次数')
@click.option('--docker', '-d', help='使用docker运行')
def run(lang, limit, page, mode, thread, repeat, docker):
    check(lang)
    click.secho('limit to: %s' % limit, fg='red', bg='black')
    c = command[lang]['run'] % (limit, page)
    if is_windows:
        c = c.replace('/', '\\')

    c = launch % (repeat, c)
    c = command[lang]['ver'] + ' && ' + c
    print(c)
    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, universal_newlines=True)
    while p.poll() is None:
        out = p.stdout.readline()
        print(out, end="")


if __name__ == '__main__':
    cli()
