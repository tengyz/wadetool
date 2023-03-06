package com.wade.framework.common.seq;

public abstract interface ISequence {
    public abstract String getNextval(String seqName) throws Exception;
    
    public abstract String getNextval(String seqName, String filterKeyword) throws Exception;
}