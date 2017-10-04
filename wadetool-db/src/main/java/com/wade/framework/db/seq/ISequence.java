package com.wade.framework.db.seq;

public abstract interface ISequence {
    public abstract String getNextval(String paramString) throws Exception;
}