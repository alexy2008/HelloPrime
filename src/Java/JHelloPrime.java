import java.util.ArrayList;
import java.util.concurrent.*;
import static java.lang.Math.*;

public class JHelloPrime extends JHiPrime implements Runnable {
    protected static ArrayList<String> interList = new ArrayList<>();
    protected static int mode,threadCount,page;
    protected static long limit, baseInd;
    protected static CountDownLatch cd;
    protected int tid;

    @Override
    public void run() {
        for (int i = tid ; i < limit/page ; i+= threadCount) {
            if (i==0) continue;
            primeByEratosthenes(page * (long) i, page);
            if (mode > 0)  putInterval(page * (long) i + page);
        }
        cd.countDown();
    }

    public void sieve() throws InterruptedException {
        cd = new CountDownLatch(threadCount);
        primeByEuler(page);
        maxInd = primeList.size();
        maxPrime = primeList.get((int) maxInd - 1);
        if (mode > 0)  generateResults(page);
        baseInd = maxInd;
        var ths = new JHelloPrime[threadCount];

        for (int i = 0; i < threadCount; i++) {
            if (mode == 3){
                ths[i] = new JHelloPrimeSeq();
            }else {
                ths[i] = new JHelloPrime();
            }
            ths[i].tid = i;
            new Thread(ths[i]).start();
        }
        cd.await();
        for (int i = 0; i < threadCount; i++) {
            if (ths[i].maxPrime > maxPrime) maxPrime = ths[i].maxPrime;
            if (mode == 3){
                if (ths[i].maxInd > maxInd) maxInd = ths[i].maxInd;
            }else {
                maxInd += ths[i].maxInd;
            }
        }
    }

    protected void generateResults(long inter) {
        putInterval(inter);
    }

    public static void main(String[] args) {
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
        try { hello.sieve();} catch (InterruptedException e) { e.printStackTrace();}
        long totalTime = System.currentTimeMillis() - startTime;
        if (mode == 1 || mode == 3) hello.printTable();
        System.out.printf("Java used %d thread to finished within %.0e the %dth prime is %d; time cost: %d ms \n" ,
                threadCount, (double) limit, hello.maxInd, hello.maxPrime, totalTime);
//        System.out.printf("【语言】Java;【模式】%d;【线程】%d;【页面】%.0e;【耗时】%s\n【范围】%s;【素数数量】%d;【最大素数】%d;\n",
//                mode,threadCount,(double)page,getFmTime(totalTime),getDfString(limit),hello.maxInd,hello.maxPrime);
//        hello.printInfo(totalTime);
    }

    protected void putInterval(long inter) {
        long m = (long) pow(10, min(11,String.valueOf(inter).length() - 1));
        if (inter % m == 0) {
            String s;
            if (mode == 3)
                s = String.format("%s|%s|%d",getDfString(inter),maxInd,maxPrime);
            else if (threadCount == 1) {
                s = String.format("%s|%s|%d",getDfString(inter),maxInd + baseInd,maxPrime);
            }else {
                s = String.format("%s|%d",getDfString(inter), maxPrime);
            }
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

    void  printInfo(long cost){
        System.out.println("┏" + "━".repeat(80) + "┓");
        System.out.printf("|  语言：Java  |  模式：%d  |  线程：%d  |  页面：%.0e  |  耗时：%s  |\n",
                mode,threadCount,(double)page,getFmTime(cost));
        System.out.println("├" + "-".repeat(80) + "┤");
        System.out.printf("|  范围：%s  |  素数个数：%d  |  最大素数：%d  |\n",
                getDfString(limit),maxInd,maxPrime);
        System.out.println("└" + "-".repeat(80) + "┘");
    }

    static String getFmTime(long l){

        if (l< 1000) return ""+l+"毫秒";
        if (l < 1000*60) return String.format("%.1f秒",(double)l/1000);

        StringBuilder strBuilder = new StringBuilder();
        long temp = l;
        long hper = 60 * 60 * 1000;
        long mper = 60 * 1000;
        long sper = 1000;
        if (temp / hper > 0) {
            strBuilder.append(temp / hper).append("时");
        }
        temp = temp % hper;

        if (temp / mper > 0) {
            strBuilder.append(temp / mper).append("分");
        }
        temp = temp % mper;
        if (temp / sper > 0) {
            strBuilder.append(temp / sper).append("秒");
        }

        return strBuilder.toString();
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
        return String.format("%.0e(%s)",(double)l,s);
    }
}