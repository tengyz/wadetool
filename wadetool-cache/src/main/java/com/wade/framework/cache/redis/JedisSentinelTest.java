package com.wade.framework.cache.redis;

import java.util.HashSet;
import java.util.Set;

import com.wade.framework.cache.util.Timer;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class JedisSentinelTest {
    /**
     * 哨兵模式链接redis
     * @throws Exception
     */
    public static JedisSentinelPool jedisSentinelPool() throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(3000);
        config.setMaxIdle(50);
        config.setMinIdle(20);
        config.setMaxWaitMillis(6 * 1000);
        config.setTestOnBorrow(true);
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("10.124.131.216:8000");
        sentinels.add("10.124.131.216:8002");
        sentinels.add("10.124.131.216:8003");
        sentinels.add("10.124.131.216:8001");
        sentinels.add("10.124.131.216:8004");
        sentinels.add("10.124.131.216:8005");
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels, config);
        //        Jedis jedis = null;
        //        try {
        //            jedis = jedisSentinelPool.getResource();
        //            jedis.set("hello", "world");
        //            String value = jedis.get("hello");
        //            System.out.println(value);
        //            
        //        }
        //        catch (Exception e) {
        //            System.out.println(e);
        //        }
        //        finally {
        //            if (jedis != null)
        //                try {
        //                    jedis.close();
        //                    jedisSentinelPool.close();
        //                }
        //                catch (Exception e) {
        //                    e.printStackTrace();
        //                }
        //        }
        
        return jedisSentinelPool;
    }
    
    /**
     * 分片模式链接   
     */
    public static JedisCluster jedisTest() {
        // 数据库链接池配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(3000);
        config.setMaxIdle(50);
        config.setMinIdle(20);
        config.setMaxWaitMillis(6 * 1000);
        config.setTestOnBorrow(true);
        // Redis集群的节点集合
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        jedisClusterNodes.add(new HostAndPort("10.124.131.216", 7000));
        jedisClusterNodes.add(new HostAndPort("10.124.131.216", 7001));
        jedisClusterNodes.add(new HostAndPort("10.124.131.216", 7002));
        jedisClusterNodes.add(new HostAndPort("10.124.131.216", 7003));
        jedisClusterNodes.add(new HostAndPort("10.124.131.216", 7004));
        jedisClusterNodes.add(new HostAndPort("10.124.131.216", 7005));
        // 根据节点集创集群链接对象
        //JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
        // 节点，超时时间，最多重定向次数，链接池
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes, 2000, 100, config);
        //        jedisCluster.set("test",
        //                "<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <Body><Response xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"Uvwh39miS3UtNDO96CQs3D08IHVvuYMs\" IssueInstant=\"2018-04-28 00:15:59\" Version=\"2.0\">");
        //        String aaa = jedisCluster.get("test");
        //        System.out.println(aaa);
        return jedisCluster;
    }
    
    public static void bench(final String xml, int threads, final int count, final JedisCluster jedisCluster,
            final JedisSentinelPool jedisSentinelPool) throws Exception {
        Thread[] t = new Thread[threads];
        Timer btimer = new Timer();
        for (int j = 0; j < threads; j++) {
            t[j] = new Thread() {
                public void run() {
                    Timer timer = new Timer();
                    for (int i = 0; i < count; i++) {
                        //                        Jedis jedis = null;
                        //                        try {
                        //                            jedis = jedisSentinelPool.getResource();
                        //                            //                            jedis.set("hello",
                        //                            //                                    "<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?><Envelope xmlns=\\\"http://schemas.xmlsoap.org/soap/envelope/\\\">\\r\\n");
                        //                            String value = jedis.get("hello");
                        //                            if (value == null) {
                        //                                System.out.println("jedisSentinelPool.get(\"test\")  ERROR");
                        //                            }
                        //                            //                            System.out.println(value);
                        //                            
                        //                        }
                        //                        catch (Exception e) {
                        //                            System.out.println(e);
                        //                        }
                        //                        finally {
                        //                            if (jedis != null)
                        //                                try {
                        //                                    jedis.close();
                        //                                    //jedisSentinelPool.close();
                        //                                }
                        //                                catch (Exception e) {
                        //                                    e.printStackTrace();
                        //                                }
                        //                        }
                        
                        try {
                            //                            jedisCluster.set("test",
                            //                                    "<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <Body><Response xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"Uvwh39miS3UtNDO96CQs3D08IHVvuYMs\" IssueInstant=\"2018-04-28 00:15:59\" Version=\"2.0\">");
                            String aaa = jedisCluster.get("test");
                            if (aaa == null) {
                                System.out.println("jedisCluster.get(\"test\")  ERROR");
                            }
                            //                            System.out.println(aaa);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            
                        }
                    }
                }
            };
            t[j].setName("THREAD" + j);
            t[j].setDaemon(true);
        }
        for (int j = 0; j < threads; j++) {
            t[j].start();
        }
        for (int j = 0; j < threads; j++) {
            try {
                t[j].join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        long ms = btimer.getUseTime();
        long avg = ms / threads * count;
        long rps = 1000000000L / avg;
        
        System.out.println(Runtime.getRuntime().availableProcessors() + "," + threads + "," + rps + "," + avg / 1000L);
    }
    
    public static void main(String args[]) throws Exception {
        
        //        try {
        //            JedisCluster jedisCluster = jedisTest();
        //            JedisSentinelPool jedisSentinelPool = jedisSentinelPool();
        //            String pp = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <Body><Response xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"Uvwh39miS3UtNDO96CQs3D08IHVvuYMs\" IssueInstant=\"2018-04-28 00:15:59\" Version=\"2.0\"><Status><StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>\r\n      </Status>\r\n      <Assertion ID=\"FHD8WsC7cf4FXP0bx8ryAc31Hy0zydpD\" IssueInstant=\"2018-04-28 00:15:59\" Version=\"2.0\">\r\n        <Issuer>http://idp_host/IDP</Issuer>\r\n        <Subject>\r\n          <NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">wangw1,彭洪军,1,wangw1@qq.com</NameID>\r\n        </Subject>\r\n        <Conditions>\r\n          <OneTimeUse/>\r\n        </Conditions>\r\n        <AuthnStatement AuthnInstant=\"2018-04-28 00:15:59\">\r\n          <AuthnContext>\r\n            <AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</AuthnContextClassRef>\r\n          </AuthnContext>\r\n        </AuthnStatement>\r\n      </Assertion>\r\n    </Response>\r\n  </Body>\r\n   \r\n</Envelope>";
        //            
        //            int THREADS = 32;
        //            int COUNT = 10000;
        //            if (args.length > 1) {
        //                COUNT = Integer.valueOf(args[1]).intValue();
        //                if (COUNT < 0)
        //                    COUNT = 10000;
        //            }
        //            
        //            String THREADS_STR = "";
        //            int cpus;
        //            int i;
        //            if (args.length > 0) {
        //                THREADS_STR = args[0];
        //            }
        //            else {
        //                cpus = Runtime.getRuntime().availableProcessors();
        //                for (i = 1; i < 16; i++) {
        //                    THREADS_STR = THREADS_STR + i * cpus + ',';
        //                }
        //            }
        //            
        //            System.out.println("Threads:" + THREADS_STR + ",CNT:" + COUNT);
        //            
        //            System.out.println("CPUS,THREADS,RPS,AVG(us)");
        //            
        //            for (String thrs : THREADS_STR.split(","))
        //                if ((thrs != null) && (thrs.length() > 0)) {
        //                    int t = Integer.valueOf(thrs).intValue();
        //                    bench(pp, t, COUNT, jedisCluster, jedisSentinelPool);
        //                }
        //        }
        //        catch (Exception e) {
        //            e.printStackTrace();
        //        }
        //        //        jedisSentinelPool();
        
    }
}