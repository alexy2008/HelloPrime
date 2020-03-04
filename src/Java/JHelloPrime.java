public class JHelloPrime {

    public static int primeByEuler(int limit, Prime prime) {
        int top = 0;
        boolean[] num = new boolean[limit];
        for (int i = 2; i < limit; i++) {
            if (!num[i]) { prime.add(i); top++; }
            for (int j = 0; j < prime.size() && (long) i * prime.get(j) < limit; j++) {
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
            for (long j = ((long) (Math.ceil(pos * 1.0 / p))) * p; j < pos + (long) limit; j += p)
                num[(int) (j - pos)] = true;
        }
        for (int i = 0; i < num.length; i++)
            if (!num[i]) { prime.add(pos + i); top++;}
        return top;
    }

    public static void main(String[] args) {
        System.out.println("Hello Prime! I'm Java :-)");
        int page = Integer.parseInt(args[0]);
        long repeat = Long.parseLong(args[1]);
        boolean isDebug = Boolean.parseBoolean(args[2]);
        Prime prime = new Prime(page, repeat, isDebug);
        long top = 0;

        System.out.println("使用分区埃拉托色尼筛选法计算" + Prime.getDfString(page * repeat) + "以内素数：");
        long startTime = System.currentTimeMillis();
        //首先使用欧拉法得到种子素数组
        top += primeByEuler(page, prime);
        prime.generateResults(page, top);
        //循环使用埃拉托色尼法计算分区
        for (int i = 1; i < repeat; i++) {
            long pos = page * (long) i;
            top += primeByEratosthenesInterval(pos, page, prime);
            prime.generateResults(pos + page, top);
        }
        long totalTime = System.currentTimeMillis() - startTime;
        prime.printTable();
        System.out.println( Prime.getDfString(page * repeat) + "以内计算完毕。累计耗时："+totalTime+"毫秒");
    }
}