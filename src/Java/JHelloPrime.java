package helloprime;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class JHelloPrime {

    public static void primeByEuler(final int limit) {
        final boolean[] num = new boolean[limit + 1];
        for (int i = 2; i < limit; i++) {
            if (!num[i]) prime.add(i);
            for (int j = 0; j < prime.size() && (long) i * prime.get(j) <= limit; j++) {
                num[(int) (i * prime.get(j))] = true;
                if (i % prime.get(j) == 0) break;
            }
        }
    }

    public static void primeByEratosthenesInterval(final Long pos, final int limit) {
        final boolean[] num = new boolean[limit];
        for (int i = 0; prime.get(i) < Math.sqrt(pos + limit); i++) {
            final long p = prime.get(i);
            for (long j = ((long)(Math.ceil(pos*1.0/p)))*p; j < pos + (long)limit; j += p)
                num[(int) (j - pos)] = true;
        }
        for (int i = 0; i < num.length; i++) if (!num[i]) prime.add(pos + i);
    }

    public static void main(final String[] args) {
        System.out.println("Hello Prime! I'm Java :-)");
        final int step = 10_0000;
        final int repeat = 10_0000;
        final long limit = (long) step * repeat;
        long startTime;
        final DecimalFormat dfne = new DecimalFormat("0E0");

        final int keep = (int) (Math.sqrt(limit) / Math.log(Math.sqrt(limit)) * 1.5);
        prime = new Prime(keep);

        System.out.println("使用分区埃拉托色尼筛选法计算" + dfne.format(limit) + "以内素数：");
        startTime = System.currentTimeMillis();
        //首先使用欧拉法得到种子素数组
        primeByEuler(step);
        //循环使用埃拉托色尼法计算分区
        for (int i = 1; i < repeat; i++) {
            final long pos = step * (long) i;
            if (!isSilent && pos  % Math.pow(10, Math.min(String.valueOf(pos).length() - 1, 9)) == 0){
                System.out.printf("%s以内，共%d个，最大素数为：%d %n",dfne.format(pos), prime.size(), prime.getlast());
            }
            primeByEratosthenesInterval(pos, step);
        }
        System.out.printf("%s以内，共%d个,最大素数为：%d%n", dfne.format(limit), prime.size(), prime.getlast());
        System.out.printf("累计耗时：%d毫秒%n", System.currentTimeMillis() - startTime);
//        for (int i = 0; i< prime.size();i++) System.out.print(prime.get(i)+",");
    }

    static String getDfString(final long l) {
        String s = String.valueOf(l);
        if (l >= 1_0000) s = s.substring(0, s.length() - 4) + "万";
        if (l % 1_0000_0000 == 0) s = s.substring(0, s.length() - 5) + "亿";
        else if (l >= 1_0000_0000) s = s.substring(0, s.length() - 5) + "亿" + s.substring(s.length() - 5);
        return s;
    }

    private static Prime prime;
    static boolean isSilent=false;//缄默模式，不输出中间信息
}

class Prime{
    private final ArrayList<Long> _prime;
    private long _maxInd; //用来存储当前计算的最大素数的序号
    private final int _maxKeep; //允许在内存中保留的素数数量

    Prime(final int keep){
        _maxKeep = keep;
        _maxInd = 0;
        _prime = new ArrayList<>(_maxKeep);
    }

    long get(final int index){
        return index >= _maxInd - 1 ? _prime.get(_prime.size() - 1) : _prime.get(index);
    }

    long getlast(){
        return _prime.get(_prime.size() - 1);
    }

    int size() {
        return (int) _maxInd;
    }

    void add(final long p) {
        final DecimalFormat dfne = new DecimalFormat("0E0");
        if (_maxInd < _maxKeep) _prime.add(p);
        else _prime.set(_prime.size() - 1, p);
        if (!JHelloPrime.isSilent && (_maxInd+1) % Math.pow(10, Math.min(String.valueOf(_maxInd+1).length() - 1, 8)) == 0)
               System.out.println("==>第" + dfne.format(_maxInd+1) + "个素数为：" + p);
        _maxInd++;
    }
}


