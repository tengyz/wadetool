package com.wade.framework.db.seq;

public class FuncSequence extends AbstractSequence {
    
    public FuncSequence(String seqName) {
        super(seqName);
    }
    
    public FuncSequence(String seqName, int fetchSize) {
        super(seqName, fetchSize);
        
    }
    
    @Override
    public String getNextval(final String connName) throws Exception {
        return nextval(connName);
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        FuncSequence get = new FuncSequence("aaaa");
        
        for (int i = 0; i < 100000; i++) {
            System.out.println(get.getNextval("bbb"));
        }
    }
    
}
