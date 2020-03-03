import java.util.ArrayList;

class Prime{
    private ArrayList<Long> primeList;
    private long maxInd; //用来存储当前计算的最大素数的序号
    private int maxKeep; //允许在内存中保留的素数数量
    private ArrayList<String> seqList = new ArrayList<>();
    private ArrayList<String> interList = new ArrayList<>();
    private boolean isDebug;
    private long prevNo;

    Prime(long page, long repeat, boolean isDbg){
        isDebug = isDbg;
        maxKeep = (int) (Math.sqrt(page*(repeat+1)) / Math.log(Math.sqrt(page*(repeat+1))) * 1.3);
        System.out.println("内存分配：" + maxKeep);
        primeList = new ArrayList<>(maxKeep);
    }

    long get(int index){
//        return index >= maxInd - 1 ? primeList.get(primeList.size() - 1) : primeList.get(index);
        return  primeList.get(index);
    }

    int size() {
        return primeList.size();
    }

    void add(long p) {
        primeList.add(p);
        maxInd++;
    }

    void generateResults (long inter, long endNo){
        putSequence(prevNo,endNo);
        putInterval(inter);
        prevNo = endNo;
        freeUp();
    }

    void putInterval(long inter) {
        String s;
        if (inter % Math.pow(10, String.valueOf(inter).length() - 1) == 0) {
            s = String.format("%s|%d|%d", getDfString(inter), maxInd, get(size() - 1));
            interList.add(s);
            if (isDebug) System.out.println("[In:]"+s);
        }
    }

    void putSequence(long beginNo, long endNo){
        String s;
        for (int i = String.valueOf(beginNo).length()-1; i <= String.valueOf(endNo).length()-1 ; i++) {
            for (int j = 1; j < 10 ; j++) {
                long seq = (long) (j*Math.pow(10,i));
                if (seq < beginNo) continue;
                if (seq >= endNo) return;
                long l = get(size() - 1 - (int)(endNo - seq));
                s = getDfString(seq) + "|" + l;
                seqList.add(s);
                if (isDebug) System.out.println("==>[No:]"+s);
            }
        }
    }

    void freeUp(){
        if (maxInd > maxKeep)  primeList.subList(maxKeep, primeList.size()-1).clear();
    }

    void printTable(){
        System.out.println("## 素数区间表");
        System.out.println("区间|个数|最大值");
        System.out.println("---|---|---");
        interList.forEach(System.out::println);
        System.out.println("## 素数序列表");
        System.out.println("序号|数值");
        System.out.println("---|---");
        seqList.forEach(System.out::println);
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
