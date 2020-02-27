package helloprime;
import java.util.ArrayList;

public class JHelloPrime {

    public static int primeByEuler(int limit, Prime prime) {
        int top = 0;
        boolean[] num = new boolean[limit + 1];
        for (int i = 2; i < limit; i++) {
            if (!num[i]) {
                prime.add(i);
                top++;
            }
            for (int j = 0; j < prime.size() && (long) i * prime.get(j) <= limit; j++) {
                num[(int) (i * prime.get(j))] = true;
                if (i % prime.get(j) == 0) break;
            }
        }
        return top;
    }

    public static int primeByEratosthenesInterval(Long pos, int limit, Prime prime) {
        int top = 0;
        boolean[] num = new boolean[limit];
        for (int i = 0; prime.get(i) < Math.sqrt(pos + limit); i++) {
            long p = prime.get(i);
            for (long j = ((long)(Math.ceil(pos*1.0/p)))*p; j < pos + (long)limit; j += p)
                num[(int) (j - pos)] = true;
        }
        for (int i = 0; i < num.length; i++) if (!num[i]) {
            prime.add(pos + i);
            top++;
        }
        return top;
    }

    public static void main(String[] args) {
        System.out.println("Hello Prime! I'm Java :-)");
        int step = 100_0000;
        int repeat = 1_000;
        long limit = (long) step * repeat;
        long maxIndex = 0;
        int keepIn = (int) (Math.sqrt(limit) / Math.log(Math.sqrt(limit)) * 2);
        Prime prime  = new Prime(keepIn);

        System.out.println("使用分区埃拉托色尼筛选法计算" + prime.getDfString(limit) + "以内素数：");
        long startTime = System.currentTimeMillis();
        //首先使用欧拉法得到种子素数组
        int n = primeByEuler(step,prime);
        maxIndex += n;
        prime.outputSequence(0,maxIndex);
        prime.outputInterval(step);
        //循环使用埃拉托色尼法计算分区
        for (int i = 1; i < repeat; i++) {
            long pos = step * (long) i;
            n = primeByEratosthenesInterval(pos, step, prime);
            maxIndex += n;
            prime.outputSequence(maxIndex-n,maxIndex);
            prime.outputInterval(pos+step);
            prime.freeUp();
        }
        System.out.printf("%s以内计算完毕。累计耗时：%d毫秒%n",
                prime.getDfString(limit), System.currentTimeMillis() - startTime);
        prime.printTable();
    }
}

class Prime{
    private ArrayList<Long> _prime;
    private long _maxInd; //用来存储当前计算的最大素数的序号
    private int _maxKeep; //允许在内存中保留的素数数量
    private ArrayList<String> seqList = new ArrayList<>();
    private ArrayList<String> interList = new ArrayList<>();
    private boolean isDebug = false;

    Prime(int keep){
        _maxKeep = keep;
        _maxInd = 0;
        _prime = new ArrayList<>(_maxKeep);
    }

    long get(int index){
        return index >= _maxInd - 1 ? _prime.get(_prime.size() - 1) : _prime.get(index);
    }

    long size() {
        return  _maxInd;
    }

    void add(long p) {
        _prime.add(p);
        _maxInd++;
    }

    void outputInterval(long inter) {
        String s;
        if (inter % Math.pow(10, String.valueOf(inter).length() - 1) == 0) {
            s = String.format("%s|%d|%d",
                    inter, _maxInd, _prime.get(_prime.size() - 1));
            interList.add(s);
            if (isDebug) System.out.println("[In:]"+getDfString(s));
        }
    }

    void outputSequence(long beginNo,long endNo){
        String s;
        for (int i = String.valueOf(beginNo).length()-1; i <= String.valueOf(endNo).length()-1 ; i++) {
            for (int j = 1; j < 10 ; j++) {
                long seq = (long) (j*Math.pow(10,i));
                if (seq < beginNo) continue;
                if (seq >= endNo) return;
                long l = _prime.get(_prime.size() - 1 - (int)(endNo - seq));
                s = seq + "|" + l;
                seqList.add(s);
                if (isDebug) System.out.println("==>[No:]"+getDfString(s));
            }
        }
    }

    void freeUp(){
        if (_maxInd > _maxKeep) _prime.subList(_maxKeep, _prime.size()-1).clear();
    }

    String getDfString(long l) {
        return getDfString(String.valueOf(l));
    }

    String getDfString(String s) {
        return s.replace("000000000000","万亿").
                replace("00000000000","000亿").
                replace("0000000000","00亿").
                replace("000000000","0亿").
                replace("00000000","亿").
                replace("0000000","000万").
                replace("000000","00万").
                replace("00000","0万").
                replace("0000","万");
    }

    void printTable(){
        System.out.println("## 素数区间表");
        System.out.println("区间|个数|最大值");
        System.out.println("---|---|---");
        interList.forEach(s -> System.out.println(getDfString(s)));
        System.out.println("## 素数序列表");
        System.out.println("序号|数值");
        System.out.println("---|---");
        seqList.forEach(s -> System.out.println(getDfString(s)));
    }
}


