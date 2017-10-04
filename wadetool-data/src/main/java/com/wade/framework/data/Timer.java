package com.wade.framework.data;

/**
 * 计时工具
 * 
 * @Description 计时工具
 * @ClassName Timer
 * @Date 2016年9月19日 上午10:00:18
 * @Author tengyizu
 */
public class Timer {
    private long start;
    
    public Timer() {
        this.start = System.nanoTime();
    }
    
    /**
     * 获取耗时（纳秒单位ns）
     * 
     * @return
     * @Date 2016年10月10日 下午5:26:35
     * @Author tengyizu
     */
    public long getUseTime() {
        return System.nanoTime() - this.start;
    }
    
    /**
     * 获取耗时（毫秒秒单位ms）
     * 
     * @return
     * @Date 2016年10月10日 下午5:27:32
     * @Author tengyizu
     */
    public long getUseTimeInMillis() {
        return (System.nanoTime() - this.start) / 1000000L;
    }
    
    public long reset() {
        long end = System.nanoTime();
        long use = end - this.start;
        this.start = end;
        return use;
    }
    
    public long resetInMillis() {
        return reset() / 1000000L;
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        // 开始计算时间
        Timer timer = new Timer();
        System.out.println("use time:" + Long.valueOf(timer.getUseTime()));
    }
}
