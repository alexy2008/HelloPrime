package helloprime;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class JHelloPrime {
    private static boolean isBench = false;

    public static int primeByEuler(int limit, Prime prime) {
        int top = 0;
        boolean[] num = new boolean[limit + 1];
        for (int i = 2; i < limit; i++) {
            if (!num[i]) {
                prime.add((long)i);
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

    static DecimalFormat dfne;

    public static void main(String[] args) {
        System.out.println("Hello Prime! I'm Java :-)");
        int step = 100_0000;
        int repeat = 1_0000;
        long limit = (long) step * repeat;
        long maxIndex = 0;

        int keepin = (int) (Math.sqrt(limit) / Math.log(Math.sqrt(limit)) * 2);
        Prime prime  = new Prime(keepin);
        long startTime;
        dfne = new DecimalFormat("0E00");

        System.out.println("使用分区埃拉托色尼筛选法计算" + dfne.format(limit) + "以内素数：");
        startTime = System.currentTimeMillis();
        //首先使用欧拉法得到种子素数组
        int n = primeByEuler(step,prime);
        maxIndex += n;
        if (!isBench) prime.outputSequence(maxIndex);
        if (!isBench) prime.outputInterval(step);
        //循环使用埃拉托色尼法计算分区
        for (int i = 1; i < repeat; i++) {
            long pos = step * (long) i;
            n = primeByEratosthenesInterval(pos, step, prime);
            maxIndex += n;
            if (!isBench) prime.outputSequence(maxIndex-n,maxIndex);
            if (!isBench) prime.outputInterval(pos+step);
            prime.freeUp();
        }
        System.out.printf("%s以内计算完毕。累计耗时：%d毫秒%n",prime.getDfString(limit), System.currentTimeMillis() - startTime);
        prime.printTable();
    }



//    private static Prime prime;
    static boolean isSilent=false;//缄默模式，不输出中间信息
}

class Prime{
    private ArrayList<Long> _prime;
    private long _maxInd; //用来存储当前计算的最大素数的序号
    private int _maxKeep; //允许在内存中保留的素数数量
    private ArrayList<String> seqList = new ArrayList<>();
    private ArrayList<String> interList = new ArrayList<>();
    private boolean isDebug = true;

    Prime(int keep){
        _maxKeep = keep;
        _maxInd = 0;
        _prime = new ArrayList<>(_maxKeep);
    }

    long get(int index){
        return index >= _maxInd - 1 ? _prime.get(_prime.size() - 1) : _prime.get(index);
    }

    long getlast(){
        return _prime.get(_prime.size() - 1);
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
            s = String.format("%s以内，共%d个，最大素数为：%d",
                    getDfString(inter), _maxInd, _prime.get(_prime.size() - 1));
            interList.add(s);
            if (isDebug) System.out.println(s);
        }
    }

    void outputSequence(long beginNo, long endNo) {
//        System.out.println(beginNo+ " - " + endNo);
        String s;
        String beginStr = String.valueOf(beginNo);
        String endStr = String.valueOf(endNo);
        if (beginStr.charAt(0) == endStr.charAt(0) && beginStr.length() == endStr.length() ) return;

        int k = beginStr.charAt(0) - '0' + 1;
        do{
            long seq = (long) (k * Math.pow(10, beginStr.length()-1));
//            System.out.println(seq);
            long l = _prime.get(_prime.size() - 1 - (int)(endNo - seq));
            s = "第"+ getDfString(seq) + "个素数是：" + l;
            seqList.add(s);
            if (isDebug) System.out.println("==>"+s);
            k++;
        }while (k<endStr.charAt(0) - '0' + 1);
    }

    void outputSequence(long endNo){
        String s;
        String endStr = String.valueOf(endNo);
        for (int i = 0; i <= endStr.length()-1 ; i++) {
            for (int j = 1; j < 10 ; j++) {
                long seq = (long) (j*Math.pow(10,i));
                if (seq >= endNo) return;
                long l = _prime.get((int) (seq-1));
                s = "第"+ getDfString(seq) + "个素数是：" + l;
                seqList.add(s);
                if (isDebug) System.out.println("==>"+s);
            }
        }
    }

    void freeUp(){
        if (_maxInd > _maxKeep) _prime.subList(_maxKeep, _prime.size()-1).clear();
    }

    String getDfString(long l) {
        if (l < 10000) return String.valueOf(l);
        String s = String.valueOf(l);

        if (l >= 1_0000) s = s.substring(0, s.length() - 4) + "万";
        if (l % 1_0000_0000 == 0) s = s.substring(0, s.length() - 5) + "亿";
        else if (l >= 1_0000_0000) s = s.substring(0, s.length() - 5) + "亿" + s.substring(s.length() - 5);
        return s;
    }

    void printTable(){
        System.out.println("===素数个数表====");
        interList.forEach(System.out::println);
        System.out.println("===素数序列表====");
        seqList.forEach(System.out::println);
    }
}


