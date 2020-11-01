import java.util.ArrayList;
import java.util.concurrent.*;
import static java.lang.Math.*;

public class JHelloPrime extends JHiPrime implements Callable<long[]> {
    protected static ArrayList<String> interList = new ArrayList<>();
    protected static int mode,threadCount,page;
    protected static long limit, baseInd;
    protected int tid;

    @Override
    public long[] call() {
        for (int i = tid ; i < limit/page ; i+= threadCount) {
            if (i==0) continue;
            primeByEratosthenes(page * (long) i, page);
            if (mode > 0)  putInterval(page * (long) i + page);
        }
        return new long[]{maxInd,maxPrime};
    }

    public void sieve() throws Exception {
        primeByEuler(page);
        maxInd = primeList.size();

        maxPrime = primeList.get((int) maxInd - 1);
        if (mode > 0)  putInterval(page);
        baseInd = maxInd;

        var fts = new FutureTask[threadCount];

        for (int i = 0; i < threadCount; i++) {
            var task = new JHelloPrime();
            task.tid = i;
            fts[i] = new FutureTask<>(task);
            new Thread(fts[i]).start();
        }

        for (int i = 0; i < threadCount; i++) {
            var l = (long[]) fts[i].get();
            if (l[1] > maxPrime) maxPrime = l[1];
            maxInd += l[0];
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello Prime! I'm Java :-)");
        limit = Long.parseLong(args[0]);
        page = Integer.parseInt(args[1]);
        mode = Integer.parseInt(args[2]);
        threadCount = Integer.parseInt(args[3]);
        JHelloPrime hello;
        if (threadCount == 0) {
            JHiPrime.main(args);
            return;
        }
        if (mode == 3) hello = new JHelloPrimeSeq();
        else hello = new JHelloPrime();
        System.out.println("使用分区埃拉托色尼筛选法计算" + getDfString(limit) + " 以内素数：");
        long startTime = System.currentTimeMillis();
        hello.sieve();
        long totalTime = System.currentTimeMillis() - startTime;
        if (mode == 1 || mode == 3) hello.printTable();
        System.out.printf("Java finished within %.0e the %dth prime is %d; time cost: %d ms \n" ,
                (double) limit, hello.maxInd, hello.maxPrime, totalTime);
    }

    protected void putInterval(long inter) {
        long m = (long) pow(10, min(9,String.valueOf(inter).length() - 1));
        if (inter % m == 0) {
            var s =  (threadCount == 1 || mode == 3)? String.format("%s|%s|%d",getDfString(inter),maxInd + baseInd,maxPrime)
                    :String.format("%s|%d",getDfString(inter), maxPrime);
            if (mode == 1|| mode == 3)interList.add(s);
            if (mode >= 2) System.out.println(s);
        }
    }

    protected void printTable(){
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