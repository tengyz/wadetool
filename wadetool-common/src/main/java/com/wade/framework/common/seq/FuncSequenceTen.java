package com.wade.framework.common.seq;

public class FuncSequenceTen extends AbstractSequenceTen {
    
    public FuncSequenceTen(String seqName) {
        super(seqName);
    }
    
    public FuncSequenceTen(String seqName, int fetchSize) {
        super(seqName, fetchSize);
    }
    
    @Override
    public String getNextval(final String connName) throws Exception {
        return nextval(connName);
    }
    
    @Override
    public String getNextval(String seqName, String filterKeyword) throws Exception {
        return null;
    }
}
