class JHelloPrime {

    static int primeByEuler(int limit, Prime prime) {
        def top = 0
        def num = new boolean[limit]
        for (def i = 2; i < limit; i++) {
            if (!num[i]) { prime.add(i); top++ }
            for (int j = 0; j < prime.size() &&  i * prime.get(j) < limit; j++) {
                num[(int) (i * prime.get(j))] = true
                if (i % prime.get(j) == 0) break
            }
        }
        return top
    }

    static int primeByEratosthenesInterval( pos,  limit, Prime prime) {
        def top = 0
        boolean[] num = new boolean[limit]
        for (int i = 0; prime.get(i) < Math.sqrt(pos + limit); i++) {
            long p = prime.get(i)
            for (def j = ( (Math.ceil(pos * 1.0 / p))) * p; j < pos +  limit; j += p)
                num[(int) (j - pos)] = true
        }
        for (int i = 0; i < num.length; i++)
            if (!num[i]) { prime.add(pos + i); top++}
        return top
    }

    static void main(String[] args) {
        println("Hello Prime! I'm Groovy! :-)")
        int page = args[0].toInteger()
        long repeat = args[1].toLong()
        boolean isDebug = args[2].toBoolean()
        Prime prime = new Prime(page, repeat, isDebug)
        long top = 0

        println "使用分区埃拉托色尼筛选法计算${prime.getDfString(page * repeat)}以内素数："
        def startTime = System.currentTimeMillis()
        //首先使用欧拉法得到种子素数组
        top += primeByEuler(page, prime)
        prime.generateResults(page, top)
        //循环使用埃拉托色尼法计算分区
        for (int i = 1; i < repeat; i++) {
            long pos = page * (long) i
            top += primeByEratosthenesInterval(pos, page, prime)
            prime.generateResults(pos + page, top)
        }
        def totalTime = System.currentTimeMillis() - startTime
        prime.printTable()
        println "${prime.getDfString(page * repeat)}以内计算完毕。累计耗时：$totalTime 毫秒"
    }
}

class Prime{
    private ArrayList<Long> primeList
    private long maxInd
    private int maxKeep
    private ArrayList<String> seqList = new ArrayList<>()
    private ArrayList<String> interList = new ArrayList<>()
    private boolean isDebug
    private long prevNo

    Prime(long page, long repeat, boolean isDbg){
        isDebug = isDbg
        maxKeep = (int) (Math.sqrt(page*(repeat+1)) / Math.log(Math.sqrt(page*(repeat+1))) * 1.3)
        println "内存分配：$maxKeep"
        primeList = new ArrayList<>(maxKeep)
    }

    long get(int index){
        return  primeList.get(index)
    }

    int size() {
        return primeList.size()
    }

    void add(long p) {
        primeList.add(p)
        maxInd++
    }

    void generateResults (long inter, long endNo){
        putSequence(prevNo,endNo)
        putInterval(inter)
        prevNo = endNo
        freeUp()
    }

    void putInterval(long inter) {
        if (inter % Math.pow(10, String.valueOf(inter).length() - 1) == 0) {
            def s = "${getDfString(inter)}|$maxInd|${get(size() - 1)}"
            interList.add(s)
            if (isDebug) println "[In:]"+s
        }
    }

    void putSequence(long beginNo, long endNo){
        for (int i = String.valueOf(beginNo).length()-1; i <= String.valueOf(endNo).length()-1 ; i++) {
            for (int j = 1; j < 10 ; j++) {
                long seq = (long) (j*Math.pow(10,i))
                if (seq < beginNo) continue
                if (seq >= endNo) return
                long l = get(size() - 1 - (int)(endNo - seq))
                def s = getDfString(seq) + "|" + l
                seqList.add(s)
                if (isDebug) println "==>[No:]"+s
            }
        }
    }

    void freeUp(){
        if (maxInd > maxKeep)  primeList.subList(maxKeep, primeList.size()-1).clear()
    }

    void printTable(){
        println("## 素数区间表")
        println("区间|个数|最大值")
        println("---|---|---")
        interList.forEach(System.out::println)
        println("## 素数序列表")
        println("序号|数值")
        println("---|---")
        seqList.forEach(System.out::println)
    }

    static String getDfString(long l) {
        String s = l.toString()
        if(l % 10000_0000_0000L == 0) {
            s = s.substring(0,s.length() - 12 ) + "万亿"
        }else if(l % 10000_0000L == 0){
            s = s.substring(0,s.length() - 8 ) + "亿"
        }else if(l%10000 == 0){
            s = s.substring(0,s.length() - 4 ) + "万"
        }
        return s
    }
}