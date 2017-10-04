package com.wade.framework.db.seq;

abstract class SeqBase extends AbstractSequence {
    
    public SeqBase(String seqName) {
        super(seqName);
    }
    
    public SeqBase(String seqName, int fetchSize) {
        super(seqName, fetchSize);
    }
}
