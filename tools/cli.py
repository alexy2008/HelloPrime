# coding=utf-8
import glob
import platform

import click
import os
import re
import subprocess
import time
from pathlib import Path
from rich.console import Console
from rich.table import Table
from rich.traceback import install

# from rich.syntax import Syntax

install()

command = {
    'java': {
        'ver': 'java -version', 'pre': 'J',
        'build': 'javac -version && javac *.java -d bin -encoding UTF-8',
        'run': 'java -cp ./bin JHelloPrime %s %s %s %s'},
    'java15': {
        'ver': 'java -version', 'pre': 'J', 'ext': 'java',
        'build': 'javac -version && javac --enable-preview --release 15 *.java -d bin -encoding UTF-8',
        'run': 'java --enable-preview -cp ./bin JHelloPrime %s %s %s %s'},
    'kt': {
        'ver': 'kotlin -version', 'name': 'Kotlin',
        'build': 'kotlinc KtHelloPrime.kt -d bin',
        'run': 'kotlin -cp ./bin KtHiPrime %s %s %s %s'},
    'c': {
        'ver': 'gcc --version',
        'build': 'gcc CHelloPrime.c -lm -O3 -o ./bin/CHelloPrime',
        'run': './bin/CHelloPrime %s %s %s %s'},
    'cpp': {
        'ver': 'g++ --version', 'name': 'C++', 'dir': 'Cpp',
        'build': 'g++ CppHelloPrime.cpp -lm -O3 -o ./bin/CppHelloPrime',
        'run': './bin/CppHelloPrime %s %s %s %s'},
    'rs': {
        'ver': 'rustc --version', 'name': 'Rust',
        'build': 'rustc RsHelloPrime.rs  --out-dir bin -C opt-level=3 -C debuginfo=0',
        'run': './bin/RsHelloPrime %s %s %s %s'},
    'cs': {
        'ver': 'dotnet --version', 'name': 'C#', 'dir': 'CSharp',
        'build': 'dotnet build CsHelloPrime.csproj -o bin -c Release',
        'run': './bin/CsHelloPrime %s %s %s %s'},
    'vb': {
        'ver': 'dotnet --version', 'name': 'VisualBasic',
        'build': 'dotnet build VbHelloPrime.vbproj -o bin -c Release',
        'run': './bin/VbHelloPrime %s %s %s %s'},
    'go': {
        'ver': 'go version',
        'build': 'go build -o ./bin/GoHelloPrime.exe GoHelloPrime.go',
        'run': './bin/GoHelloPrime %s %s %s %s'},
    'swift': {
        'ver': 'swiftc --version', 'pre': 'Sw',
        'build': 'swiftc -O SwHelloPrime.swift -o bin/SwHelloPrime',
        'run': './bin/SwHelloPrime %s %s %s %s'},
    'dart': {
        'ver': 'dart --version', 'pre': 'Da',
        'build': 'dart compile exe DaHelloPrime.dart -o bin/DaHelloPrime.exe',
        'run': './bin/DaHelloPrime %s %s %s %s'},
    'py': {
        'ver': 'python --version', 'name': 'Python',
        'run': 'python PyHelloPrime.py %s %s %s %s'},
    'rb': {
        'ver': 'ruby --version', 'name': 'Ruby',
        'run': 'ruby RbHelloPrime.rb %s %s %s %s'},
    'php': {
        'ver': 'php --version',
        'run': 'php PhpHelloPrime.php %s %s %s %s'},
    'groovy': {
        'ver': 'groovy --version', 'pre': 'Gv',
        'run': 'groovy GvHelloPrime %s %s %s %s'},
    'js': {
        'ver': 'node --version', 'name': 'JavaScript',
        'run': 'node JsHelloPrime.js %s %s %s %s'},
    'ts': {
        'ver': 'deno --version', 'name': 'TypeScript',
        'run': 'deno run TsHelloPrime.ts %s %s %s %s'},
}

is_windows = True
osname = 'windows'
tag = 'Hello'
cur_path = '.'
launch = ''
launch_cmd = 'for /l %%i in (1,1,%s) do @%s'
launch_sh = 'for i in $(seq %s);do %s;done'
info = {}
console = Console()


def check_lang(lang):
    global tag, cur_path, info

    if command[lang].get('name') is None:
        command[lang]['name'] = lang.capitalize()

    info['lang'] = command[lang]['name']

    dr = command[lang].get('dir')
    if dr is None:
        dr = command[lang]['name']

    if Path(dr).is_dir():
        cur_path = './%s' % dr

    if len(glob.glob(r'%s/*Hello*' % cur_path)) > 0:
        tag = 'Hello'
    elif len(glob.glob(r'%s/*Hi*' % cur_path)) > 0:
        tag = 'Hi'
        command[lang]['run'] = command[lang]['run'].replace('Hello', 'Hi')
    else:
        print('没有找到程序文件，请在正确目录下运行此脚本')
        return -1

    info['tag'] = tag + 'Prime'

    print('定位工作目录：', os.path.abspath(cur_path))
    # syntax = Syntax.from_path("syntax.py", line_numbers=True)

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

    info['machine'] = platform.machine()
    is_windows = (osname == 'Windows')

    if is_windows:
        launch = launch_cmd
        info['os'] = platform.system() + platform.release()
        p = subprocess.Popen('wmic cpu get /value', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
        lns = p.stdout.readlines()
        for ln in lns:
            if ln.startswith('Name='):
                info['cpu'] = ln.split('=')[1].strip()
            if ln.startswith('NumberOfCores='):
                info['core'] = ln.split('=')[1].strip()
            if ln.startswith('NumberOfLogicalProcessors='):
                info['lcore'] = ln.split('=')[1].strip()
            if ln.startswith('MaxClockSpeed='):
                info['clock'] = ln.split('=')[1].strip()

    else:
        launch = launch_sh
        if osname == 'Linux':
            import distro
            # print('【操作系统】：', platform.system(), distro.linux_distribution())
            info['os'] = distro.name() + ' ' + distro.version()
            # print(info['os'])
            p = subprocess.Popen('cat /proc/cpuinfo', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
            lns = p.stdout.readlines()
            for ln in lns:
                if ln.startswith('model name'):
                    info['cpu'] = ln.split(':')[1].strip()
                if ln.startswith('cpu cores'):
                    info['core'] = ln.split(':')[1].strip()
                if ln.startswith('siblings'):
                    info['lcore'] = ln.split(':')[1].strip()
                if ln.startswith('cpu MHz'):
                    info['clock'] = ln.split(':')[1].strip()
        elif osname == 'MacOS':
            print()

    console.print('[green]欢迎使用[red]HelloPrime[/red] CLI for %s [green]' % osname)
    console.print('项目地址：[link=https://www.deepinjava.com]https://www.deepinjava.com[/link]')
    pass


@cli.command(help='编译源代码')
@click.argument('lang')
@click.option('--docker', '-d', help='使用docker运行')
def build(lang, docker):
    if check_lang(lang) < 0: return -1
    command[lang]['build'] = command[lang]['build'].replace('Hello', 'Hi')
    if not is_windows: command[lang]['build'] = command[lang]['build'].replace('.exe', '')
    c = command[lang]['build']
    if docker is not None:
        c = 'docker run -v %s:/usr/helloprime -w /usr/helloprime -it --rm %s sh -c \'%s\'' % (
            os.path.abspath(cur_path), docker, c)
        if is_windows: c = c.replace('\'', '\"')
    print(c)
    click.echo('开始编译%s' % lang)
    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, universal_newlines=True,
                         cwd=cur_path)
    while p.poll() is None:
        out = p.stdout.readline()
        print(out.replace('\n', '').replace('\r', ''))
    click.echo('%s编译完成' % lang)


@cli.command(help='运行程序')
@click.argument('langs', nargs=-1)
@click.option('--limit', '-l', default='1000000', help='计算范围')
@click.option('--page', '-p', default='10000', help='页面大小')
@click.option('--mode', '-m', default=0, help='运行模式')
@click.option('--thread', '-t', default=1, help='线程数')
@click.option('--repeat', '-r', default=1, help='执行次数')
@click.option('--docker', '-d', help='使用docker运行')
def run(langs, limit, page, mode, thread, repeat, docker):
    global info, launch
    info['thread'] = thread
    info['repeat'] = repeat
    info['mode'] = mode
    info['thread'] = thread
    info['docker'] = docker
    info['costs'] = []

    lang = langs[0]
    if len(langs) > 1: limit = langs[1]
    if len(langs) > 2: page = langs[2]

    if docker is not None:
        c = 'docker run -it --rm %s  cat /etc/os-release' % docker
        p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
        lns = p.stdout.readlines()
        if mode > 1: console.print(lns)
        for ln in lns:
            if ln.startswith('NAME='):
                info['os'] = ln[5:-1]
            if ln.startswith('VERSION_ID='):
                info['os'] = info['os'] + " " + ln[11:-1]
        info['os'] = info['os'].replace('"', '')

    if check_lang(lang) < 0: return -1
    if limit.startswith('e'): limit = '1' + limit
    if page.startswith('e'): page = '1' + page

    nlimit = e2n(limit)
    info['limit'] = nlimit
    npage = e2n(page)
    info['page'] = npage

    c = command[lang]['run'] % (nlimit, npage, mode, thread)
    if is_windows and docker is None:
        c = c.replace('/', '\\')

    if docker is not None: launch = launch_sh
    c = launch % (repeat, c)
    c = command[lang]['ver'] + ' && ' + c
    if docker is not None:
        c = 'docker run -v %s:/usr/helloprime -w /usr/helloprime -it --rm %s sh -c \'%s\'' % (
            os.path.abspath(cur_path), docker, c)
        if is_windows: c = c.replace('\'', '\"')
    if mode > 1: print(c)

    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True,
                         cwd=cur_path)
    out = p.stdout.readline().replace('\n', '').replace('\r', '')
    if mode > 1: print(out.replace('\n', '').replace('\r', ''), end='\r\n')
    pn = r'[0-9][0-9\.]+'
    v = re.findall(pn, out)
    if len(v) > 0:
        print(v)
        for vi in v:
            if '.' in vi:
                info['version'] = vi
                break
        if info.get('version') is None:
            info['version'] = v[0]
        console.print(out, end='\r\n')
    else:
        info['version'] = 'N/A'

    while p.poll() is None or out:
        try:
            out = p.stdout.readline().replace('\n', '').replace('\r', '')
            # if p.poll() is not None and not out : break
            k = proc_out(out)
            if mode > 1 or (k == 0 and mode > 0): console.print(out, end='\r\n')

        except Exception as ex:
            print('异常：' + str(ex))

    print_result()


def proc_out(line):
    global info

    pn = re.compile(r'(?<=the )[\d ]+?(?=th)|(?<=prime is )\d+|(?<=time cost: )\d+')
    r = re.findall(pn, str(line))

    if len(r) > 0:
        # console.print(r)
        info['maxind'] = r[0]
        info['maxprime'] = r[1]
        info['costs'].append(int(r[2]))
        console.print('%.0e([yellow]%s[/yellow])以内共有[yellow]%s[/yellow]个素数，最大素数为[yellow]%s[/yellow]，'
                      '[bright_red]%s[/bright_red]耗时[red]%d[/red]毫秒' %
                      (info['limit'], n2s(info['limit']), info['maxind'], info['maxprime'], info['lang'], int(r[2])),
                      end='\r\n')
        return 3

    pn = 'Hi|Hello Prime.*I.*'
    r = re.match(pn, str(line))
    if r is not None:
        console.print(
            '[green]%s[/green] [cyan]Prime[/cyan] [yellow]I\'m[/yellow] [bright_red]%s[/bright_red] :smile:' % (
                info['tag'][:-5], info['lang']), end='')
        return 1

    if line.startswith('Calculate prime') or line.startswith('使用分区埃拉托色尼筛选法'):
        console.print(' ---- 用分区埃拉托色尼筛选法计算[cyan]%.0e[/cyan]以内素数' % info['limit'], end='\r\n')
        return 2

    return 0


def print_result():
    global info

    if info['mode'] == 2: console.print(info)
    if info['mode'] == 1: console.print('time cost:' + str(info['costs']))

    table = Table.grid(expand=True)
    for i in range(3):
        table.add_column('id%s' % i, style="cyan")
        table.add_column('ct%s' % i, style="yellow")

    info['mincost'] = fm_time(min(info.get('costs')))
    info['avgcost'] = fm_time(sum(info.get('costs')) / info['repeat'])

    table.add_row('【语言】', info['lang'], '【版本】', info['version'], '【运行程序】', info['tag'])
    table.add_row('【机器架构】', info['machine'], '【操作系统】', info['os'], '【Docker镜像】', info['docker'])
    table.add_row('【页面大小】', n2s(info['page']), '【运行模式】', str(info['mode']), '【线程数】', str(info['thread']))
    table.add_row('【计算范围】', '%.0e(%s)' % (info['limit'], n2s(info['limit'])), '【素数数量】', info['maxind'], '【最大素数】',
                  str(info['maxprime']))
    table.add_row('【计算次数】', str(info['repeat']), '【最好成绩】', '[bold magenta][red]' + info['mincost'], '【平均成绩】',
                  info['avgcost'])

    console.rule('[green]运行结果[/] ' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
    console.rule('[cyan]【CPU】[/cyan][yellow]' + info['cpu'] + ' (' + info['core'] +' 核心 '+ info['lcore']+' 线程 ' + info['clock']+' MHz)[/yellow]')
    console.print(table)
    console.print(info['cpu'] + '-' + info['core'])


def fm_time(lt):
    temp = lt
    h_per = 60 * 60 * 1000
    m_per = 60 * 1000
    s_per = 1000
    s = ''

    if lt < s_per:
        return str(int(lt)) + '毫秒'
    if lt < m_per:
        return '%.2f秒' % (lt / 1000)

    if int(temp / h_per) > 0:
        s = s + str(int(temp / h_per)) + '时'

    temp = temp % h_per
    if int(temp / m_per) > 0:
        s = s + str(int(temp / m_per)) + '分'

    temp = temp % m_per
    if int(temp / s_per) > 0:
        s = s + str(int(temp / s_per)) + '秒'

    return s


if __name__ == '__main__':
    cli()
