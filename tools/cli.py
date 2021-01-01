# coding=utf-8
import platform
import click
import subprocess
import os
from pathlib import Path
import glob
from rich.console import Console
from rich.table import Table

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

info = {}


def check_lang(lang):
    global tag, cur_path, info
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

    info['tag'] = tag + 'Prime'

    print('定位工作目录：', os.path.abspath(cur_path))
    return 0


def e2n(s):
    if 'e' not in s: return int(s)
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
    global is_windows, osname, launch, info
    osname = platform.system()
    print(platform.uname())

    is_windows = (osname == 'Windows')

    if is_windows:
        # import wmi
        launch = launch_cmd
        print('【操作系统】：', platform.system(), platform.win32_ver())
        info['os'] = platform.system() + ' ' + platform.release()
        print(info['os'])
        # cpu = wmi.WMI().Win32_Processor()[0]
        # print('【CPU信息】：%s %s核%s线程' % (cpu.Name.strip(), cpu.NumberOfCores, cpu.ThreadCount))
    else:
        launch = launch_sh
        if osname == 'Linux':
            import distro
            print('【操作系统】：', platform.system(), distro.linux_distribution())
            info['os'] = distro.name() + ' ' + distro.version()
            print(info['os'])
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
    global info
    info['thread'] = thread
    info['repeat'] = repeat
    info['mode'] = mode
    info['thread'] = thread
    info['costs'] = []

    lang = langs[0]
    info['lang'] = lang.capitalize().replace('pp', '++').replace('sharp', '#')
    if check_lang(lang) < 0: return -1
    if limit.startswith('e'): limit = '1' + limit
    if page.startswith('e'): page = '1' + page

    nlimit = e2n(limit)
    info['limit'] = nlimit
    npage = e2n(page)
    info['page'] = npage

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
        proc_out(out)

    print_result()


def proc_out(line):
    global info
    i1 = line.find('the')
    i2 = line.find('th prime')
    i3 = line.find('time cost:')


    if i1 >= 0 and i2 >= 0 and i3 >= 0:
        info['maxind'] = line[i1 + 4: i2]
        info['maxprime'] = line[i2 + 12: i3 - 2]
        info['costs'].append(int(line[i3 + 11: -4]))


def print_result():
    global info

    console = Console()
    console.print(info)

    # table = Table(title="运行结果", show_lines=True, show_header=False)
    table = Table.grid(expand=True)
    for i in range(3):
        table.add_column('id%s' % i, style="cyan")
        table.add_column('ct%s' % i, style="yellow")

    info['mincost'] = fmtime(min(info['costs']))
    info['avgcost'] = fmtime(sum(info['costs']) / info['repeat'])

    table.add_row('【语言】', info['lang'], '【操作系统】', info['os'], '【运行程序】', info['tag'])
    table.add_row('【页面大小】', n2s(info['page']), '【运行模式】', str(info['mode']), '【线程数】', str(info['thread']))
    table.add_row('【计算范围】', n2s(info['limit']), '【素数数量】', info['maxind'], '【最大素数】', str(info['maxprime']))
    table.add_row('【计算次数】', str(info['repeat']), '【最好成绩】', '[bold magenta][red]' + info['mincost'], '【平均成绩】',
                  info['avgcost'])

    console.print(table)


def fmtime(l):
    temp = l
    hper = 60 * 60 * 1000
    mper = 60 * 1000
    sper = 1000
    s = ''

    print(l / 1000)

    if l < sper: return str(int(l)) + '毫秒'
    if l < mper: return '%.2f秒' % (l / 1000)

    if int(temp/hper) > 0 :
        s = s + str(int(temp/hper)) + '时'

    temp = temp % hper
    if int(temp/mper) > 0 :
        s = s + str(int(temp / mper)) + '分'

    temp = temp % mper
    if int(temp / sper) > 0 :
        s= s + str(int(temp / sper)) + '秒'

    return s

if __name__ == '__main__':
    cli()
