import java.util.ArrayList;
import java.util.concurrent.*;
import static java.lang.Math.pow;

public class JHelloPrimeSeq extends JHelloPrime {
    private final ArrayList<Long> primeExt = new ArrayList<>(maxKeep);

    private static final ArrayList<String> seqList = new ArrayList<>();
    private long prevNo, curPos;

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
    public long[] call() {
        primeByEratosthenes(curPos, page);
        return null;
    }

    public void sieve() throws Exception {
        primeByEuler(page);
        maxInd = primeList.size();
        generateResults(page);

        var fts = new Future[threadCount];
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        JHelloPrimeSeq[] tasks = new JHelloPrimeSeq[threadCount];
        for (int i = 0; i < threadCount; i++) {
            tasks[i] = new JHelloPrimeSeq();
            tasks[i].tid = i;
        }

        for (int i = 1; i < limit / page; i += threadCount) {
            for (int n = 0; n < threadCount; n++) {
                tasks[n].maxInd = 0;
                tasks[n].primeExt.clear();
                tasks[n].curPos = page * (long) i + n * page;
                fts[n] = es.submit(tasks[n]);
            }
            for (int n = 0; n < threadCount; n++) {
                fts[n].get();
                if (page * (long) i + (n + 1) * page > limit) break;
                maxInd += tasks[n].maxInd;
                primeList.addAll(tasks[n].primeExt);
                generateResults(page * (long) i + (n + 1) * page);
            }
        }
        es.shutdown();
    }

    private void generateResults(long inter) {
        maxPrime = primeList.get(primeList.size() - 1);
        putSequence(prevNo);
        putInterval(inter);
        prevNo = maxInd;
        if (maxInd > maxKeep) primeList.subList(maxKeep, primeList.size() - 1).clear();
    }

    private void putSequence(long beginNo) {
        for (int i = String.valueOf(beginNo).length() - 1; i <= String.valueOf(maxInd).length() - 1; i++) {
            for (int j = 1; j < 10; j++) {
                long seq = (long) (j * pow(10, i));
                if (seq < beginNo) continue;
                if (seq >= maxInd) return;
                long l = primeList.get(primeList.size() - 1 - (int) (maxInd - seq));
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