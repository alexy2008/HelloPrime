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
from primelist import primeList

install()

alias = {'c++':'Cpp','c#':'CSharp','cs':'CSharp'}

is_windows = True
osname = 'windows'
launch = ''
tag = 'Hello'
cur_path = '.'
info = {}
console = Console()


def check_lang(lang):
    global tag, cur_path, info

    if alias.get(lang) is not None:
        lang = alias[lang]
    
    info['lang'] = lang.capitalize()

    if Path(lang).is_dir():
        cur_path = './%s' % lang
    else:
        console.print('没有找到程序文件，请在正确目录下运行此脚本')
        return -1

    if info['lang'] == 'Cpp': info['lang'] = 'C++'
    if info['lang'] == 'Csharp': info['lang'] = 'C#'

    console.print('定位工作目录：', os.path.abspath(cur_path))
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
    info['platform'] = platform.platform()
    is_windows = (osname == 'Windows')

    console.print(info['platform'])

    if is_windows:
        launch = 'powershell.exe ./%s.ps1 '
        info['os'] = platform.system()  + platform.release()
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
        launch = 'bash ./%s '
        if osname == 'Linux':
            import distro
            info['os'] = distro.name()  + distro.version()
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
        elif osname == 'Darwin':
            osname == 'MacOS'
            info['os'] = ''.join(platform.platform().split('-')[:2])

            p = subprocess.Popen('sysctl machdep.cpu', shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
            lns = p.stdout.readlines()
            for ln in lns:
                if ln.startswith('machdep.cpu.brand_string'):
                    info['cpu'] = ln.split(':')[1].strip()
                if ln.startswith('machdep.cpu.core_count'):
                    info['core'] = ln.split(':')[1].strip()
                if ln.startswith('machdep.cpu.thread_count'):
                    info['lcore'] = ln.split(':')[1].strip()

            info['clock'] = 'N/A'


    console.print('[green]欢迎使用[red]HelloPrime[/red] Shell CLI for %s [green]' % osname)
    console.print('项目地址：[link=https://www.deepinjava.com]https://www.deepinjava.com[/link]')
    pass


@cli.command(help='编译源代码')
@click.argument('lang')
def build(lang):
    if check_lang(lang) < 0: return -1
    
    c = launch % 'build'

    console.print(c)
    click.echo('开始编译%s' % lang)
    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, cwd=cur_path)
    while p.poll() is None:
        out = p.stdout.readline().decode("utf8",  "ignore")
        print(out.replace('\n', '').replace('\r', ''))
    click.echo('%s编译完成' % lang)


@cli.command(help='运行程序')
@click.argument('args', nargs=-1)
@click.option('--limit', '-l', default='100000', help='计算范围')
@click.option('--page', '-p', default='1000', help='页面大小')
@click.option('--mode', '-m', default=0, help='运行模式')
@click.option('--thread', '-t', default=1, help='线程数')
@click.option('--repeat', '-r', default=1, help='执行次数')
def run(args, limit, page, mode, thread, repeat):
    global info
    info['thread'] = thread
    info['repeat'] = repeat
    info['mode'] = mode
    info['thread'] = thread
    info['docker'] = ''
    info['costs'] = []

    lang = args[0]
    if len(args) > 1: limit = args[1]
    if len(args) > 2: page = args[2]

    if check_lang(lang) < 0: return -1

    nlimit = e2n(limit)
    info['limit'] = nlimit
    npage = e2n(page)
    info['page'] = npage

    c = ((launch % 'run') + '%s %s -m %s -t %s -r %s') % (limit, page, mode, thread, repeat)

    if mode > 1: console.print(c)

    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True, cwd=cur_path)
    
    p.stdout.readline()
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

def showcode(lan):
    from rich.syntax import Syntax
    with open("./Java/JHelloPrime.java", "r") as f:
        code = f.read()
    syntax = Syntax(code, "java", theme="monokai", line_numbers=True)
    console.print(syntax)


def proc_out(line):
    global info

    pn = re.compile(r'(?<=the )[\d ]+?(?=th)|(?<=prime is )\d+|(?<=time cost: )\d+')
    r = re.findall(pn, str(line))
    if len(r) > 0:
        # console.print(r)
        info['maxind'] = int(r[0])
        info['maxprime'] = int(r[1])
        info['costs'].append(int(r[2]))
        console.print('%.0e([yellow]%s[/yellow])以内共有[yellow]%d[/yellow]个素数，最大素数为[yellow]%d[/yellow]，'
                      '[bright_red]%s[/bright_red]耗时[red]%d[/red]毫秒' %
                      (info['limit'], n2s(info['limit']), info['maxind'], info['maxprime'], info['lang'], int(r[2])),
                      end='\r\n')

        if info['limit'] in primeList:
            ind = primeList[info['limit']]['maxind']
            maxp = primeList[info['limit']]['maxprime']
            # print('%d -- %d' % (ind, maxp))
            if ind == info['maxind'] and maxp == info['maxprime']:
                console.print('计算结果校验[green]正确！[/green]')
            else:
                console.print('[red]计算结果校验错误！[/red]正确结果应为：%d-%d 请检查' % (ind, maxp))
        
        return 3
    
    pn = 'Hi|Hello Prime.*I.*'
    r = re.match(pn, str(line))
    if r is not None:
        console.print(
            '[green]%s[/green] [cyan]Prime[/cyan] [yellow]I\'m[/yellow] [bright_red]%s[/bright_red] :smile:' % (
                tag, info['lang']), end='')
        return 1

    if line.startswith('Calculate prime') or line.startswith('使用分区埃拉托色尼筛选法'):
        console.print(' ---- 用分区埃拉托色尼筛选法计算[cyan]%.0e[/cyan]以内素数' % info['limit'], end='\r\n')
        return 2

    return 0


def print_result():
    global info, tag

    if info['mode'] == 2: console.print(info)
    if info['mode'] == 1: console.print('time cost:' + str(info['costs']))

    table = Table.grid(expand=True)
    for i in range(3):
        table.add_column('id%s' % i, style="cyan")
        table.add_column('ct%s' % i, style="yellow")

    info['mincost'] = fm_time(min(info.get('costs')))
    info['avgcost'] = fm_time(sum(info.get('costs')) / info['repeat'])

    table.add_row('【语言】', info['lang'], '【版本】', info['version'],  '【操作系统】', info['os'])
    # table.add_row('【机器架构】', info['machine'], '【操作系统】', info['os'], '【Docker镜像】', info['docker'])
    table.add_row('【页面大小】', n2s(info['page']), '【运行模式】', str(info['mode']), '【线程数】', str(info['thread']))
    table.add_row('【计算范围】', '%.0e(%s)' % (info['limit'], n2s(info['limit'])), '【素数数量】', str(info['maxind']), '【最大素数】',
                  str(info['maxprime']))
    table.add_row('【计算次数】', str(info['repeat']), '【最好成绩】', '[bold magenta][red]' + info['mincost'], '【平均成绩】',
                  info['avgcost'])

    console.rule('[green]运行结果[/] ' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
    console.rule('[cyan]【CPU】[/cyan][yellow]' + info['cpu'] + ' (' + info['core'] +' 核心 '+ info['lcore']+' 线程 ' + info['clock']+' MHz)[/yellow]')
    console.print(table)

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