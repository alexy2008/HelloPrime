from Prime import Prime
import math
import time

PAGE = 1_0000
REPEAT = 1_0000
isSilent = False

prime = Prime(PAGE * REPEAT)


def prime_by_eratosthenes_interval(pos, limit):
    top = 0
    num = [True] * limit
    for i in range(0, prime.size()):
        p = prime.get(i)
        if p * p >= pos + limit: break
        for j in range(math.ceil(pos / p), int((pos + limit - 1) / p) + 1):
            num[j * p - pos] = False
    for i in range(0, limit):
        if num[i]:
            prime.add(pos + i)
            top = top + 1
    return top


def prime_by_euler(limit):
    top = 0
    num = [True] * limit
    for i in range(2, limit):
        if num[i]:
            prime.add(i)
            top = top + 1
        for j in range(0, prime.size()):
            if i * prime.get(j) >= limit: break
            num[i * prime.get(j)] = False
            if i % prime.get(j) == 0: break
    return top


def main():
    print("Hello Mr.Prime I'm Python :-)")
    limit = REPEAT * PAGE
    top = 0

    print('使用分区埃拉托色尼筛选法计算', prime.df_string(limit), '以内全部质数')
    start_time = time.time()
    # 首先使用欧拉法得到种子素数组
    top = top + prime_by_euler(PAGE)
    prime.generate_results(PAGE, top)
    # 循环使用埃拉托色尼法计算分区
    for i in range(1, REPEAT):
        pos = PAGE * i
        top = top + prime_by_eratosthenes_interval(pos, PAGE)
        prime.generate_results(pos + PAGE, top)
    total_time = round((time.time() - start_time) * 1000)
    prime.print_table()
    print(prime.df_string(limit), '以内计算完毕。累计耗时：', total_time, '毫秒')


if __name__ == '__main__':
    main()
