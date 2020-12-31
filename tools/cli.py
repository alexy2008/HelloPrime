# coding=utf-8
import platform
import click
import subprocess
import os
from pathlib import Path
import glob
from rich.console import Console
from rich.table import Table

# try:
#     import wmi
# except:


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


def e2n(s):
    if 'e' not in s : return int(s)
    if s.startswith('e'): s = '1' + s
    s1, _, s2 = s.partition('e')
    num = int(s1) * pow(10, int(s2))

    return num


def n2s(num):
    s = str(num)
    if num % 1_0000_0000_0000 == 0:
        s = s[:-12] + "万亿"
    elif num % 1_0000_0000 == 0:
        s = s[:-8] + "亿"
    elif num % 1_0000 == 0:
        s = s[:-4] + "万"

    return s


@click.group()
def cli():
    global is_windows, osname, launch
    osname = platform.system()

    print(platform.uname())



    is_windows = (osname == 'Windows')

    if is_windows:
        import wmi
        launch = launch_cmd
        print('【操作系统】：', platform.system(), platform.win32_ver())
        # cpu = wmi.WMI().Win32_Processor()[0]
        # print('【CPU信息】：%s %s核%s线程' % (cpu.Name, cpu.NumberOfCores, cpu.ThreadCount))
    else:
        launch = launch_sh
        if osname == 'Linux':
            print('【操作系统】：', platform.system(), platform.linux_distribution())
        elif osname == 'MacOS':
            print()

    click.secho('欢迎HiPrime CLI for %s' % osname, fg='blue', bg='black')
    click.secho('https://www.deepinjava.com', fg='yellow', underline=True)
    pass


@cli.command(help='编译源代码')
@click.argument('lang')
def build(lang):
    if check_lang(lang) < 0: return -1
    click.echo('开始编译%s' % lang)
    p = subprocess.Popen(command[lang]['build'], shell=True, stdout=subprocess.PIPE, universal_newlines=True,
                         cwd=cur_path)
    while p.poll() is None:
        out = p.stdout.readline()
        print(out, end="")
    click.echo('%s编译完成' % lang)


@cli.command(help='运行程序')
@click.argument('langs', nargs=-1)
@click.option('--limit', '-l', default='10000', help='计算范围')
@click.option('--page', '-p', default='1000', help='页面大小')
@click.option('--mode', '-m', default=0, help='运行模式')
@click.option('--thread', '-t', default=1, help='线程数')
@click.option('--repeat', '-r', default=1, help='执行次数')
@click.option('--docker', '-d', help='使用docker运行')
def run(langs, limit, page, mode, thread, repeat, docker):
    lang = langs[0]
    if check_lang(lang) < 0: return -1
    if limit.startswith('e'): limit = '1'+limit
    if page.startswith('e'): page = '1'+page

    nlimit = e2n(limit)
    npage = e2n(page)

    click.secho('【计算范围】: %s（%s）；【页面大小】：%s（%s）' % (limit, n2s(nlimit), page, n2s(npage)), fg='red', bg='black')
    c = command[lang]['run'] % (nlimit, npage, mode, thread)
    if is_windows:
        c = c.replace('/', '\\')

    c = launch % (repeat, c)
    c = command[lang]['ver'] + ' && ' + c
    print(c)
    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, universal_newlines=True, cwd=cur_path, bufsize=4096)
    while p.poll() is None:
        out = p.stdout.readline()
        print(out, end="")

    table = Table(title="运行结果", show_lines=True)

    table.add_column("Released", justify="cen", style="cyan")
    table.add_column("Title", style="magenta")
    table.add_column("Box Office", justify="right", style="green")

    table.add_row("Dec 20, 2019", "Star Wars: The Rise of Skywalker", "$952,110,690")
    table.add_row("May 25, 2018", "星球大战: A Star Wars Story", "$393,151,347")
    table.add_row("Dec 15, 2017", "Star Wars Ep. V111: The Last Jedi", "$1,332,539,889")
    table.add_row("Dec 16, 2016", "Rogue One: A Star Wars Story", "$1,332,439,889")

    console = Console()
    console.print(table)

    table = Table.grid()
    # table = Table(show_header=True, header_style="bold magenta")
    table.add_column("Date", style="dim", width=20)
    table.add_column("Title")
    table.add_column("Production Budget", justify="right")
    table.add_column("Box Office", justify="right")
    table.add_row(
        "Dev 20, 2019", "Star Wars: The Rise of Skywalker", "$275,000,000", "$375,126,118"
    )
    table.add_row(
        "May 25, 2018",
        "[red]Solo[/red]: A Star Wars Story",
        "$275,000,000",
        "$393,151,347",
    )
    table.add_row(
        "Dec 15, 2017",
        "Star Wars Ep. VIII: The Last Jedi",
        "$262,000,000",
        "[bold]$1,332,539,889[/bold]",
    )

    console.print(table)

    from rich import print
    grid = Table.grid(expand=True)
    grid.add_column()
    grid.add_column(justify="right")
    grid.add_row("Raising shields", "[bold magenta]COMPLETED [green]:heavy_check_mark:")

    print(grid)


if __name__ == '__main__':
    cli()
