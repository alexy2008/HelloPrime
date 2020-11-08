import java.util.ArrayList;
import static java.lang.Math.pow;

public class JHelloPrimeSeq extends JHelloPrime {
    private final ArrayList<Long> primeExt = new ArrayList<>(maxKeep);
    private static volatile int curTid = 1;
    private static final ArrayList<String> seqList = new ArrayList<>();
    private static long prevNo;

    public void primeByEratosthenes(Long pos, Integer page) {
        var num = new boolean[page];
        for (var i = 0; primeList.get(i) * primeList.get(i) < pos + (long) page; i++) {
            var p = primeList.get(i);
            for (var j = ((long) (Math.ceil((double) pos / p))) * p; j < pos + page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < page; i++)
            if (!num[i]) {
                primeExt.add(pos + i);
                maxInd++;
            }
    }

    @Override
    public void run() {
        if (threadCount == 1) curTid = 0;
        for (int i = tid ; i < limit/page ; i+= threadCount) {
            maxInd = 0;
            if (i==0) continue;
            primeByEratosthenes(page * (long) i, page);
            for(;;)  if (curTid == tid) break;
            baseInd += maxInd;
            maxInd = baseInd;
            generateResults(page * (long) i + page);
            var cTid = curTid + 1;
            curTid = (cTid == threadCount)?0:cTid;
            primeExt.clear();
        }
        cd.countDown();
    }

    protected void generateResults(long inter) {
        maxPrime = primeList.get(primeList.size() - 1);
        putSequence(prevNo, (prevNo == 0)?primeList:primeExt);
        putInterval(inter);
        prevNo = maxInd;
    }

    private void putSequence(long beginNo, ArrayList<Long> prList) {
        for (int i = String.valueOf(beginNo).length() - 1; i <= String.valueOf(maxInd).length() - 1; i++) {
            for (int j = 1; j < 10; j++) {
                long seq = (long) (j * pow(10, i));
                if (seq < beginNo) continue;
                if (seq >= maxInd) return;
                long l = prList.get(prList.size() - 1 - (int) (maxInd - seq));
                var s = getDfString(seq) + "|" + l;
                seqList.add(s);
                if (mode > 1) System.out.println("==>[No:]" + s);
            }
        }
    }

    protected void printTable() {
        System.out.println("## 素数序列表");
        System.out.println("序号|数值");
        System.out.println("---|---");
        seqList.forEach(System.out::println);
        super.printTable();
    }
}