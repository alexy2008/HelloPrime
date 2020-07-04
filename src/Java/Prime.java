import java.util.ArrayList;
import static java.lang.Math.*;

class Prime{
    private ArrayList<Long> primeList;
    public long maxInd; //用来存储当前计算的最大素数的序号
    public long maxPrime;
    private int maxKeep; //允许在内存中保留的素数数量
    private ArrayList<String> seqList = new ArrayList<>();
    private ArrayList<String> interList = new ArrayList<>();
    private long prevNo;
    private int mode;

    Prime(long limit, long page, int mo){
        mode = mo;
        maxKeep = (int) (sqrt(limit) / log(sqrt(limit)) * 1.3);
        var reserve = (int) ((sqrt(limit)+page) / log(sqrt(limit)+page) * 1.3);
//        maxKeep = 80000;
//        var reserve = 150000;
        System.out.println("Memory allocation：" + maxKeep);
        primeList = new ArrayList<>(reserve);
    }

    long get(int index){
       return  primeList.get(index);
    }

    int size() {
        return primeList.size();
    }

    void add(long p) {
        primeList.add(p);
        maxInd++;
    }

    void generateResults (long inter){
        if (mode > 0){
            putSequence(prevNo);
            putInterval(inter);
            prevNo = maxInd;
        }
        maxPrime = primeList.get(primeList.size() - 1);
        freeUp();
    }

    void putInterval(long inter) {
        if (inter % pow(10, String.valueOf(inter).length() - 1) == 0) {
            var s = String.format("%s|%d|%d", getDfString(inter), maxInd, get(size() - 1));
            interList.add(s);
            if (mode > 1) System.out.println("[In:]"+s);
        }
    }

    void putSequence(long beginNo){
        for (int i = String.valueOf(beginNo).length()-1; i <= String.valueOf(maxInd).length()-1 ; i++) {
            for (int j = 1; j < 10 ; j++) {
                long seq = (long) (j* pow(10,i));
                if (seq < beginNo) continue;
                if (seq >= maxInd) return;
                long l = get(size() - 1 - (int)(maxInd - seq));
                var s = getDfString(seq) + "|" + l;
                seqList.add(s);
                if (mode > 1) System.out.println("==>[No:]"+s);
            }
        }
    }

    void freeUp(){
        if (maxInd > maxKeep)  primeList.subList(maxKeep, primeList.size()-1).clear();
    }

    void printTable(){
        if (mode < 1) return;
        System.out.println("## 素数序列表");
        System.out.println("序号|数值");
        System.out.println("---|---");
        seqList.forEach(System.out::println);
        System.out.println("## 素数区间表");
        System.out.println("区间|个数|最大值");
        System.out.println("---|---|---");
        interList.forEach(System.out::println);
    }

    static String getDfString(long l) {
        var s = String.valueOf(l);
        if(l % 10000_0000_0000L == 0) {
            s = s.substring(0,s.length() - 12 ) + "万亿";
        }else if(l % 10000_0000L == 0){
            s = s.substring(0,s.length() - 8 ) + "亿";
        }else if(l%10000 == 0){
            s = s.substring(0,s.length() - 4 ) + "万";
        }
        return s;
    }
}
