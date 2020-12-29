# coding=utf-8
import platform
import click
# import colorama


@click.command()
@click.option('--count', default=1, help='Number of greetings.')
@click.option('--name', prompt='Your name',
              help='The person to greet.')
def hello(count, name):
    """Simple program that greets NAME for a total of COUNT times."""
    for x in range(count):
        click.echo('Hello %s!' % name)


@click.group()
def cli():
    click.secho('欢迎HiPrime CLI！', fg='blue', bg='black')
    click.secho('https://www.deepinjava.com', fg='yellow', underline=True)
    pass


@cli.command(help='编译源代码')
@click.argument('lang')
def build(lang):
    click.echo('开始编译'+lang)


@cli.command(help='运行程序')
@click.argument('lang')
@click.option('--limit', '-l', default='1000', help='计算范围')
@click.option('--page', '-p', default='100', help='页面大小')
@click.option('--mode', '-m', default=0, help='运行模式')
@click.option('--thread', '-t', default=1, help='线程数')
@click.option('--repeat', '-r', default=1, help='执行次数')
@click.option('--docker', '-d', help='使用docker运行')
def run(lang, limit, page, mode, thread, repeat, docker):
    click.secho('limit to: %s' % limit, fg='red', bg='black')


if __name__ == '__main__':
    cli()

print(platform.system())

if platform.system() == 'Windows':
    print('Windows系统')
else:
    if platform.system() == 'Linux':
        print('Linux系统')
    else:
        print('其他')
