import math
import time

STEP = 100_0000
REPEAT = 1_0000
isSilent = False


class Prime:
    _prime = []
    _maxInd = 0
    _maxKeep = 0
    _tCheck = 0

    def __init__(self):
        lim = STEP * REPEAT
        self._maxKeep = math.sqrt(lim) / math.log(math.sqrt(lim), math.e) * 1.5

    def get(self, index):
        return self._prime[index] if (index < self._maxInd - 1) else self._prime[self._maxInd - 1]

    def getlast(self):
        return self._prime[len(self._prime) - 1]

    def size(self):
        return self._maxInd

    def add(self, p):
        if self._maxInd < self._maxKeep:
            self._prime.append(p)
        else:
            self._prime[len(self._prime) - 1] = p

        if (not isSilent) and (self._maxInd + 1) % pow(10, len(str(self._maxInd + 1)) - 1) == 0:
            print("==>第", format(self._maxInd + 1, '0.0E'), "个素数为：", p)

        self._maxInd = self._maxInd + 1


prime = Prime()


def prime_by_eratosthenes_interval(pos, limit):
    num = [True] * limit

    for i in range(0, prime.size()):
        p = prime.get(i)
        if p * p >= pos + limit: break
        for j in range(math.ceil(pos / p), int((pos + limit - 1) / p) + 1):
            num[j * p - pos] = False

    for i in range(0, limit):
        if num[i]: prime.add(pos + i)


def prime_by_euler(limit):
    num = [True] * limit

    for i in range(2, limit):
        if num[i]: prime.add(i)

        for j in range(0, prime.size()):
            if i * prime.get(j) >= limit: break
            num[i * prime.get(j)] = False
            if i % prime.get(j) == 0: break


def main():
    print("Hello Mr.Prime I'm Python :-)")
    limit = REPEAT * STEP

    print('使用分区埃拉托色尼筛选法计算', format(limit, '0.0E'), '以内全部质数')
    start_time = time.time()
    # 首先使用欧拉法得到种子素数组
    prime_by_euler(STEP)
    # 循环使用埃拉托色尼法计算分区
    for i in range(1, REPEAT):
        pos = STEP * i
        # if (not isSilent) and pos % pow(10, int(math.log10(pos))) == 0:
        if (not isSilent) and pos % pow(10, len(str(pos)) - 1) == 0:
            print(format(pos, '0.0E'), "以内，共", prime.size(), "个，最大素数：", prime.getlast())
        prime_by_eratosthenes_interval(pos, STEP)

    print(format(limit, '0.0E'), '以内，共', prime.size(), '个,最大素数为：', prime.getlast())
    print('耗时', round((time.time() - start_time) * 1000), '毫秒')


if __name__ == '__main__':
    main()
