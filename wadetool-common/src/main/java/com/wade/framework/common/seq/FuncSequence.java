package com.wade.framework.common.seq;

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
    
    @Override
    public String getNextval(String seqName, String filterKeyword) throws Exception {
        return null;
    }
}
