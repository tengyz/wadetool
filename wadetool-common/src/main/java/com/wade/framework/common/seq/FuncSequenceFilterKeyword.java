package com.wade.framework.common.seq;

public class FuncSequenceFilterKeyword extends AbstractSequenceFilterKeyword {
    
    public FuncSequenceFilterKeyword(String seqName, String filterKeyword) {
        super(seqName, filterKeyword);
    }
    
    public FuncSequenceFilterKeyword(String seqName, int fetchSize, String filterKeyword) {
        super(seqName, fetchSize, filterKeyword);
    }
    
    @Override
    public String getNextval(final String connName) throws Exception {
        return null;
    }
    
    @Override
    public String getNextval(String seqName, String filterKeyword) throws Exception {
        return nextval(seqName, filterKeyword);
    }
}
