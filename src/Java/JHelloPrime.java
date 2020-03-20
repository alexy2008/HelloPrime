public class JHelloPrime {

    public static int primeByEuler(int page, Prime prime) {
        int top = 0;
        boolean[] num = new boolean[page];
        for (int i = 2; i < page; i++) {
            if (!num[i]) { prime.add(i); top++; }
            for (int j = 0; j < prime.size() && (long) i * prime.get(j) < page; j++) {
                num[(int) (i * prime.get(j))] = true;
                if (i % prime.get(j) == 0) break;
            }
        }
        return top;
    }

    public static int primeByEratosthenes(Long pos, int page, Prime prime) {
        int top = 0;
        boolean[] num = new boolean[page];
        for (int i = 0; prime.get(i) < Math.sqrt(pos + page); i++) {
            long p = prime.get(i);
            for (long j = ((long) (Math.ceil(pos * 1.0 / p))) * p; j < pos + (long) page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (int i = 0; i < num.length; i++)
            if (!num[i]) { prime.add(pos + i); top++;}
        return top;
    }

    public static void main(String[] args) {
        System.out.println("Hello Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        int mode = Integer.parseInt(args[2]);
        Prime prime = new Prime(limit, page, mode);
        long top = 0;

        System.out.println("使用分区埃拉托色尼筛选法计算" + Prime.getDfString(limit) + " 以内素数：");
        long startTime = System.currentTimeMillis();
        //首先使用欧拉法得到种子素数组
        top += primeByEuler(page, prime);
        prime.generateResults(page, top);
        //循环使用埃拉托色尼法计算分区
        for (int i = 1; i < limit/page; i++) {
            long pos = page * (long) i;
            top += primeByEratosthenes(pos, page, prime);
            prime.generateResults(pos + page, top);
        }
        long totalTime = System.currentTimeMillis() - startTime;
        prime.printTable();
        System.out.printf("Java finished within %.0e the %dth prime is %d; time cost: %d ms \n" ,
                (double) limit, prime.maxInd, prime.maxPrime, totalTime);
    }
}