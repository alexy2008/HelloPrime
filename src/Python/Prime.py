import math


class Prime:
    is_debug = True
    prime_list = []
    max_ind = 0
    max_keep = 0
    prev_no = 0
    seq_list = []
    inter_list = []

    def __init__(self, limit):
        self.max_keep = round(math.sqrt(limit) / math.log(math.sqrt(limit), math.e) * 1.5)

    def get(self, index):
        return self.prime_list[index] if (index < self.max_ind - 1) else self.prime_list[self.max_ind - 1]

    def getlast(self):
        return self.prime_list[len(self.prime_list) - 1]

    def size(self):
        return self.max_ind

    def add(self, p):
        self.prime_list.append(p)
        self.max_ind = self.max_ind + 1

    def generate_results(self, inter, end_no):
        self.output_sequence(self.prev_no, end_no)
        self.output_interval(inter)
        self.prev_no = end_no
        self.free_up()

    def output_interval(self, inter):
        if inter % pow(10, len(str(inter)) - 1) == 0:
            s = self.df_string(inter) + "|" + str(self.max_ind) + "|" + str(self.prime_list[len(self.prime_list) - 1])
            self.inter_list.append(s)
            if self.is_debug: print("[In:]" + s)

    def output_sequence(self, begin_no, end_no):
        for i in range(len(str(begin_no)) - 1, len(str(end_no))):
            for j in range(1, 10):
                seq = j * pow(10, i)
                if seq < begin_no: continue
                if seq >= end_no: return
                l = self.prime_list[len(self.prime_list) - 1 - (end_no - seq)]
                s = self.df_string(seq) + "|" + str(l)
                self.seq_list.append(s)
                if self.is_debug: print("==>[No:]" + s)

    def free_up(self):
        if self.max_ind > self.max_keep:
            del self.prime_list[self.max_keep:len(self.prime_list)]

    def df_string(self, l):
        s = str(l)
        if l % 1_0000_0000_0000 == 0:
            s = s[:-12] + "万亿"
        elif l % 1_0000_0000 == 0:
            s = s[:-8] + "亿"
        elif l % 1_0000 == 0:
            s = s[:-4] + "万"
        return s

    def print_table(self):
        print("## 素数区间表")
        print("区间|个数|最大值")
        print("---|---|---")
        for s in self.inter_list: print(s)
        print("## 素数序列表")
        print("序号|数值")
        print("---|---")
        for s in self.seq_list: print(s)
