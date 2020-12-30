# coding=utf-8
import platform
import click
import subprocess
import os
from pathlib import Path
import glob

command = {'java': {'ver': 'java -version',
                    'build': 'javac -version && javac *.java -d bin -encoding UTF-8',
                    'run': 'java -cp ./bin JHelloPrime %s %s %s %s'},
           'c': {'ver': 'gcc --version',
                 'build': 'gcc CHelloPrime.c -lm -O3 -o ./bin/CHelloPrime',
                 'run': './bin/CHelloPrime %s %s %s %s'}}

is_windows = True
osname = 'windows'
tag = 'Hello'
cur_path = '.'
launch = ''
launch_cmd = 'for /l %%i in (1,1,%s) do @%s'
launch_sh = 'for i in $(seq %s);do %s;done'


def check_lang(lang):
    global tag, cur_path
    if Path(lang).is_dir():
        cur_path = './%s' % lang

    if len(glob.glob(r'%s/*Hello*' % cur_path)) > 0:
        tag = 'Hello'
    elif len(glob.glob(r'%s/*Hi*' % cur_path)) > 0:
        tag = 'Hi'
        command[lang]['build'] = command[lang]['build'].replace('Hello', 'Hi')
        command[lang]['run'] = command[lang]['run'].replace('Hello', 'Hi')
    else:
        print('没有找到程序文件，请在正确目录下运行此脚本')
        return -1

    print('定位工作目录：', os.path.abspath(cur_path))
    return 0


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

    click.secho('欢迎HiPrime CLI for %s' % osname, fg='blue', bg='black')
    click.secho('https://www.deepinjava.com', fg='yellow', underline=True)
    pass


@cli.command(help='编译源代码')
@click.argument('lang')
def build(lang):
    if check_lang(lang) < 0: return -1
    click.echo('开始编译%s' % lang)
    p = subprocess.Popen(command[lang]['build'], shell=True, stdout=subprocess.PIPE, universal_newlines=True, cwd=cur_path)
    while p.poll() is None:
        out = p.stdout.readline()
        print(out, end="")
    click.echo('%s编译完成' % lang)


@cli.command(help='运行程序')
@click.argument('langs',nargs=-1)
@click.option('--limit', '-l', default='1000', help='计算范围')
@click.option('--page', '-p', default='100', help='页面大小')
@click.option('--mode', '-m', default=0, help='运行模式')
@click.option('--thread', '-t', default=1, help='线程数')
@click.option('--repeat', '-r', default=1, help='执行次数')
@click.option('--docker', '-d', help='使用docker运行')
def run(langs, limit, page, mode, thread, repeat, docker):
    lang = langs[0]
    if check_lang(lang) < 0: return -1
    click.secho('limit to: %s' % limit, fg='red', bg='black')
    c = command[lang]['run'] % (limit, page, mode, thread)
    if is_windows:
        c = c.replace('/', '\\')

    c = launch % (repeat, c)
    c = command[lang]['ver'] + ' && ' + c
    print(c)
    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, universal_newlines=True, cwd=cur_path, bufsize=4096)
    while p.poll() is None:
        out = p.stdout.readline()
        print(out, end="")


if __name__ == '__main__':
    cli()
