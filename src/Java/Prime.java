//package helloprime;

import java.util.ArrayList;

class Prime{
    private ArrayList<Long> _prime;
    private long _maxInd = 0; //用来存储当前计算的最大素数的序号
    private int _maxKeep; //允许在内存中保留的素数数量
    private ArrayList<String> seqList = new ArrayList<>();
    private ArrayList<String> interList = new ArrayList<>();
    private boolean isDebug = false;
    private long prevNo = 0;

    Prime(long limit){
        _maxKeep = (int) (Math.sqrt(limit) / Math.log(Math.sqrt(limit)) * 2);
        _prime = new ArrayList<>(_maxKeep);
    }

    long get(int index){
        return index >= _maxInd - 1 ? _prime.get(_prime.size() - 1) : _prime.get(index);
    }

    long size() {
        return  _maxInd;
    }

    void add(long p) {
        _prime.add(p);
        _maxInd++;
    }

    void generateResults (long inter, long endNo){
        outputSequence(prevNo,endNo);
        outputInterval(inter);
        prevNo = endNo;
        freeUp();
    }

    void outputInterval(long inter) {
        String s;
        if (inter % Math.pow(10, String.valueOf(inter).length() - 1) == 0) {
            s = String.format("%s|%d|%d",
                    getDfString(inter), _maxInd, _prime.get(_prime.size() - 1));
            interList.add(s);
            if (isDebug) System.out.println("[In:]"+s);
        }
    }

    void outputSequence(long beginNo,long endNo){
        String s;
        for (int i = String.valueOf(beginNo).length()-1; i <= String.valueOf(endNo).length()-1 ; i++) {
            for (int j = 1; j < 10 ; j++) {
                long seq = (long) (j*Math.pow(10,i));
                if (seq < beginNo) continue;
                if (seq >= endNo) return;
                long l = _prime.get(_prime.size() - 1 - (int)(endNo - seq));
                s = getDfString(seq) + "|" + l;
                seqList.add(s);
                if (isDebug) System.out.println("==>[No:]"+s);
            }
        }
    }

    void freeUp(){
        if (_maxInd > _maxKeep) _prime.subList(_maxKeep, _prime.size()-1).clear();
    }

    String getDfString(long l) {
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

    void printTable(){
        System.out.println("## 素数区间表");
        System.out.println("区间|个数|最大值");
        System.out.println("---|---|---");
        interList.forEach(s -> System.out.println(s));
        System.out.println("## 素数序列表");
        System.out.println("序号|数值");
        System.out.println("---|---");
        seqList.forEach(s -> System.out.println(s));
    }
}
