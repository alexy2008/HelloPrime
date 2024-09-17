# coding=utf-8
import glob
import mysql.connector
from mysql.connector import Error
import configparser
from datetime import datetime
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
import time
import psutil
import cpuinfo

install()

alias = {'c++':'Cpp','c#':'CSharp','cs':'CSharp'}

is_windows = True
osname = 'windows'
launch = ''
tag = 'Hello'
cur_path = '.'
info = {}
console = Console()

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
    info['ostype'] = osname
    info['platform'] = platform.platform()
    info['hostname'] = platform.node()
    is_windows = (osname == 'Windows')

    console.print('【平台信息】' + info['platform'])

    if is_windows:
        launch = 'pwsh ./%s.ps1 '
        if platform.release() == '10' and int(platform.version().split('.')[-1]) >= 22000:
           info['os'] = 'Windows 11'
        else:
           info['os'] =  platform.system() + ' ' + platform.release()

    else:
        launch = 'bash ./%s '
        if osname == 'Linux':
            import distro
            info['os'] = distro.name() + ' ' + distro.version()
        elif osname == 'Darwin':
            osname == 'MacOS'
            info['os'] = f"{info['platform'].split('-')[0]} {info['platform'].split('-')[1]}"

    info['cpu'] = cpuinfo.get_cpu_info().get('brand_raw')
    info['core'] = psutil.cpu_count(logical=False)
    info['lcore'] = psutil.cpu_count(logical=True)
    info['clock'] = int(psutil.cpu_freq().current)

    console.print('[green]欢迎使用[red]HelloPrime[/red] Shell CLI for %s [green]' % osname)
    console.print('项目地址：[link=https://www.deepinjava.com]https://www.deepinjava.com[/link]')
    pass


@cli.command(help='运行程序')
@click.argument('args', nargs=-1)
@click.option('--limit', '-l', default='100000', help='计算范围')
@click.option('--page', '-p', default='1000', help='页面大小')
@click.option('--mode', '-m', default=0, help='运行模式')
@click.option('--thread', '-t', default=1, help='线程数')
@click.option('--repeat', '-r', default=1, help='执行次数')
@click.option('--docker', '-d', help='docker镜像')
def run(args, limit, page, mode, thread, repeat, docker):
    global info
    info['thread'] = thread
    info['repeat'] = repeat
    info['mode'] = mode
    if thread < 1 and info['lcore'] > 1: thread = info['lcore']
    info['thread'] = thread
    info['docker'] = docker
    info['costs'] = []

    lang = args[0].capitalize()
    info['lang'] = lang
    if len(args) > 1: limit = args[1]
    if len(args) > 2: page = args[2]

    # if check_lang(lang) < 0: return -1

    nlimit = e2n(limit)
    info['limit'] = nlimit
    npage = e2n(page)
    info['page'] = npage

    c = ((launch % 'run') + '%s %s %s -m %s -t %s -r %s -d %s') % (lang, limit, page, mode, thread, repeat, docker)
    console.print(c, end='\r\n')

    p = subprocess.Popen(c, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True, cwd=cur_path)
    
    while p.poll() is None or out:
        try:
            out = p.stdout.readline().replace('\n', '').replace('\r', '')
            if mode > 1: console.print(out)
            if "PRETTY_NAME" in out: 
                console.print(out.replace('PRETTY_NAME=', 'OS in docker is '))
                continue
            if "Executing ver command" in out: 
                print(out)
                break
        except Exception as ex:
            print('异常：' + str(ex))
            return -1
    
    out = p.stdout.readline().replace('\n', '').replace('\r', '')
    pn = r'[0-9][0-9\.]+'
    v = re.findall(pn, out)
    if len(v) > 0:
        # print(v)
        for vi in v:
            if '.' in vi:
                info['version'] = vi
                break
        if info.get('version') is None:
            info['version'] = v[0]
        console.print(out, end='\r\n')
        info['ver_info'] = out


    while p.poll() is None or out:
        try:
            out = p.stdout.readline().replace('\n', '').replace('\r', '')
            if "Executing run command" in out: 
                print(out, end='\r\n')
                continue
            k = proc_out(out)
            if mode > 0: console.print(out, end='\r\n')
            if k < 0: return k
        except Exception as ex:
            print('异常：' + str(ex))
            return -1

    print_result()

@cli.command(help='显示代码')
@click.argument('lang')
def showcode(lang):
    from rich.syntax import Syntax
    lang = lang.capitalize()
    # 构造目标目录路径
    target_dir = os.path.join(os.getcwd(), lang)
    
    # 检查目标目录是否存在
    if not os.path.exists(target_dir):
        print(f"目录 {target_dir} 不存在")
        return
    
    # 查找目标目录下的文件
    file_pattern = os.path.join(target_dir, "*H*Prime.*")
    files = glob.glob(file_pattern)
    
    # 遍历找到的文件并用rich的Syntax输出
    for file_path in files:
        with open(file_path, 'r', encoding='utf-8') as file:
            code = file.read()
        # 确定文件的语言类型
        file_extension = os.path.splitext(file_path)[1][1:]  # 获取文件扩展名并去除点号
        syntax = Syntax(code, file_extension, theme="monokai", line_numbers=True)
        console.print(f"Show the code ：{file_path}")
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
                return -1
        
        return 1
    
    pn = 'Hi|Hello Prime.*I.*'
    r = re.match(pn, str(line))
    if r is not None:
        console.print(
            '[green]%s[/green] [cyan]Prime[/cyan] [yellow]I\'m[/yellow] [bright_red]%s[/bright_red] :smile:' % (
                tag, info['lang']), end='')
        return 1

    if line.startswith('Calculate prime') or line.startswith('使用分区埃拉托色尼筛选法'):
        console.print(' ---- 用分区埃拉托色尼筛选法计算[cyan]%.0e[/cyan]以内素数' % info['limit'], end='\r\n')
        return 1
    
    if line.startswith('Run the command'):
        console.print(line.replace('Run the command','运行指令'), end='\r\n')
        return 1   

    return 0

def load_db_config(config_file):
    """ 从配置文件加载数据库连接信息 """
    config = configparser.ConfigParser()
    config.read(config_file)
    db_config = {
        'host': config.get('mysql', 'host'),
        'user': config.get('mysql', 'user'),
        'password': config.get('mysql', 'password'),
        'database': config.get('mysql', 'database')
    }
    return db_config

def create_connection(db_config):
    """ 创建一个数据库连接到MySQL数据库 """
    conn = None
    try:
        conn = mysql.connector.connect(**db_config)
        return conn
    except Error as e:
        print(e)
    return conn

def insert_info(conn, info):
    """ 将信息插入到数据库中 """
    sql = '''
        INSERT INTO results (
            lang, version, os, ostype, cpu, core, lcore, clock, page, mode, thread, upto, maxind, maxprime,
            rep, mincost, avgcost, creatdt, ver_info, platform, hostname) VALUES (
             %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
    '''
    data = (
        info['lang'],
        info['version'],
        info['os'],
        info['ostype'],
        info['cpu'],
        info['core'],
        info['lcore'],
        info['clock'],
        info['page'],
        info['mode'],
        info['thread'],
        info['limit'],
        info['maxind'],
        info['maxprime'],
        info['repeat'],
        info['mincost'],
        info['avgcost'],
        # time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()),
        datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-3],
        info['ver_info'],
        info['platform'],
        info['hostname']
    )

    # 打印SQL语句及其参数
    # print("Executing SQL:", sql)
    # print("With data:", data)

    try:
        cur = conn.cursor()
        cur.execute(sql, data)
        conn.commit()
    except Error as e:
        console.print(e)

def print_result():
    global info, tag

    if info['mode'] > 0: console.print(info)

    info['mode'] = 'Host' if info['docker'] is None else 'Docker'
    if info['docker'] is None:
        if 'WSL' in info['platform']:
            info['mode'] = 'WSL'
        else:
            info['mode'] = 'Host'
    else:
        info['mode'] = 'Docker'

    table = Table.grid(expand=True)
    for i in range(3):
        table.add_column('id%s' % i, style="cyan")
        table.add_column('ct%s' % i, style="yellow")

    info['mincost'] = min(info.get('costs'))
    info['avgcost'] = sum(info.get('costs')) / info['repeat']
   
    table.add_row('【语言】', info['lang'], '【版本】', info['version'],  '【操作系统】', info['os'])
    table.add_row('【页面大小】', n2s(info['page']), '【运行模式】', str(info['mode']), '【线程数】', str(info['thread']))
    table.add_row('【计算范围】', '%.0e(%s)' % (info['limit'], n2s(info['limit'])), '【素数数量】', str(info['maxind']), '【最大素数】', str(info['maxprime']))

    table.add_row('【计算次数】', str(info['repeat']), '【最好成绩】', f'[bold magenta][red]{fm_time(info["mincost"])}', '【平均成绩】', fm_time(info['avgcost']))

    console.rule('[green]运行结果[/] ' + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
    console.rule(f'[cyan]【CPU】[/cyan][yellow]{info["cpu"]} ({info["core"]}核心 {info["lcore"]}线程 {info["clock"]}MHz)[/yellow]')    

    # console.rule('[cyan]【CPU】[/cyan][yellow]' + info['cpu'] + ' (' + str(info['core'])+' 核心 '+ str(info['lcore'])+' 线程 ' + str(info['clock'])+' MHz)[/yellow]')
    console.print(table)

    if info['mincost'] > 100:
        db_config = load_db_config('..//tools//config.ini')
        print('将计算结果存入数据库...', end='', flush=True)
        conn = create_connection(db_config)
        insert_info(conn, info)
        conn.close()
        print('Done!')

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