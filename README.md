# wadetool

wadetool java tool

#### 介绍

工具类，涵盖缓存，数据库，文件，异常，加密，aop等

#### 缓存说明（wadetool-cache）

1.场景：

- 配置文件（xxx.properties）
- SQL模板文件(*.sql)
- 业务级配置表(权限、菜单、静态参数 ...)
- 其它(你能想到的，需要频繁访问，又变动不频繁的数据)

#### 缓存分类

**1.本次缓存**

- 本地只读缓存
- 本地读写缓存

**2.分布式缓存**

A. Memcached缓存：在 **Memcached**中可以保存的item数据量是没有限制的，只要内存足够

- 会话缓存
- 共享缓存
- CCD缓存
- 静态参数翻译缓存
- 并发控制缓存

B. Redis缓存

- 权限缓存：
  当初我们的权限缓存是基于Memcached做的，但没考虑批量判权限的场景，在批量判2000个权限时，由于反复多次的反序列化操作，以及2000多次网络Round-Trip（往返时延）开销，遇到了严重的性能问题，后来深入研究了Redis数据结构特性，将权限框架做了适度的改造，一下子性能提高了4000多倍。最终采用redis的hmset，hmget返回Set<String>
  类型

**统一会话：**分布式应用下，不再依赖会话复制，将会话独立在WEB服务器外统一管控

**路由缓存：**提供对工号、地州、号码等路由策略

**权限缓存：**将权限与业务解耦，通过缓存独立访问；

**产品缓存：**将产品与业务解耦，通过缓存独立访问

**参数缓存：**将业务参数放入缓存管控

**服务缓存：**对服务的接入管控，并发管控；

会话缓存、共享缓存、CCD缓存、静态参数缓存、并发控制缓存 权限缓存 会话缓存 静态参数缓存 并发控制缓存 CCD缓存 共享缓存

#### 本地缓存

在我们的系统中，本地缓存也称JVM缓存，即存在于JVM内部，其本质是一个HashMap，适用于缓存少量数据，又需要以极高频率访问的场景，典型的例子就是：CODE_CODE模板、CACHE_TABLES表、TD_S_BIZENV表、MVEL配置表、数据模糊化配置表。
少量：控制在1W条记录以下，否则容易导致OOM 速度：单次访问 < 1微妙，即一秒钟能访问几百万次

特点 访问速度极快 容量有限 在所有JVM中冗余，在分布式环境中会缓存多份

**本地只读缓存**（IReadOnlyCache） 特点 通过实现loadData接口，采用一次性全量加载方式 100%命中 由于做了只读限制，多线程访问下可以做到无锁，极大提高访问速度，要求缓存数据一次性全量加载，确保缓存 100%
命中。

**本地读写缓存**（IReadWriteCache） 特点 采用按需增量加载方式 采用LRU算法淘汰冷数据 多线程环境下需要采用读写锁控制数据一致性（框架级做）

适合那些没法一次性全量加载的场合，采用按需加载方式，即先看缓存有没有，没有再查数据库或读文件，再将数据缓存起来。

#### 本地缓存--配置说明

配置localcache.xml文件 说明： cronExpr="2 0 * * ?"，配置刷新周期，这里是每天凌晨2点刷新 init="true"，系统启动时初始化该缓存，否则在首次使用时初始化

注：业务侧的本地只读缓存全配置成init="false"，否则系统启动时加载顺 序不对，容易造成死锁。

```
第一步：先定义初始化加载到只读缓存中，只加载一次
public class UacCacheTablesCache extends AbstractReadOnlyCache {
    private static final Logger log = LogManager.getLogger(UacCacheTablesCache.class);
    
    @Override
    public Map<String, Object> loadData() throws Exception {
        Map rtn = new HashMap();
        String sql = "SELECT TABLE_NAME, date_format(VERSION,'%Y-%c-%d %H:%i:%s') VERSION FROM TD_M_CACHE_TABLES WHERE STATE =1 ";
        IDataList ds = null;
        try {
            ds = DbUtil.queryList(sql);
            log.info("UacCacheTablesCache直接jdbc获取数据库时间=:" + ds);
        }
        catch (Exception e) {
            log.error("UacCacheTablesCache直接jdbc获取数据库时间异常:", e);
        }
        
        int i = 0;
        for (int size = ds.size(); i < size; i++) {
            IDataMap data = ds.getData(i);
            String tableName = data.getString("TABLE_NAME");
            String version = data.getString("VERSION");
            version = StringUtils.replaceChars(version, ":- ", "").substring(6, 12);
            if (log.isDebugEnabled()) {
                log.debug("UacCacheTablesCache表的版本号本地缓存tableName=:" + tableName);
                log.debug("UacCacheTablesCache表的版本号本地缓存version=:" + version);
            }
            rtn.put(tableName, version);
        }
        if (log.isDebugEnabled()) {
            log.debug("UacCacheTablesCache表的版本号本地缓存size=:" + ds.size());
        }
        return rtn;
    }
}

第二步：localcache.xml文件中的 <readonly>元素里面
<!--
			className: 缓存实现类  (必配参数)
			cronExpr: 缓存清理时间 (可选参数，默认不自动清空。)
			init: 系统初始化时是否立即初始化缓存 (可选参数, 默认不初始化)
-->
<cache className="com.wade.framework.common.cache.readonly.UacCacheTablesCache" cronExpr="30 * * * ?"  init="true" />

第三步：只读缓存的使用方式
IReadOnlyCache cacheTables = CacheManager.getReadOnlyCache(UacCacheTablesCache.class);
String version = (String)cacheTables.get(tableName);


/** 从工厂里获取缓存实例 */
IReadOnlyCache cache = CacheFactory.getReadOnlyCache(BizEnvCache.class);

/** 从缓存实例里获取缓存对象 */
Object obj = cache.get("XXX");

...



```

#### 时间缓存

在业务处理中，如果每一次时间请求都从数据库中获取，会占用网络带宽，消耗主机资源，增加数据库负担。 采用时间缓存这种方式，首次从标准时间源（数据库）获得时间，再缓存到本地服务器，后续直接从本地服务器推算出最新的时间，只需要经历1个步骤。
工具类：TimeHelper

#### 业务环境配置表

业务经常需要配置一些开关、接口调用地址等需要高频访问的数据，以往我们都是将这些数据配置字典表

#### 本地读写缓存

第一步：配置localcached.xml在 <readwrite>元素里面 说明： name="MVEL_STATEMENT_CACHE"，配置的读写缓存名 cronExpr="1 4 * * ?"，配置刷新周期，这里是每天凌晨4:
01刷新 maxSize="2000"，当超过此容量时，会采用LRU算法淘汰冷数据

注：maxSize不可设置得过大，一般5000以下为宜，否则容易造成OOM 代码实例：

```
/** 根据配置的缓存名，获取读写缓存实例 */`
`IReadWriteCache cache = CacheFactory.getReadWriteCache("MVEL_STATEMENT_CACHE");`

`/** 从缓存实例中，根据缓存的key，获取缓存对象 */`
`String cacheValue = cache.get(cacheKey);`
`if ( null == cacheValue ) { // 缓存未命中`
`cacheValue = ... // 查数据库或读文件`
`if ( null != cacheValue ) { // 设置缓存`
`cache.put(cacheKey, cacheValue);`
`}`
`}


private static final ICache cache = CacheManager.getCache("ACCT_CONFIG");
String key = "BATCH_JOB_TYPE_" + infoTypes;
		return CacheUtil.get(cache, key, new ICacheSourceProvider<IDataset>() {
			@Override
			public IDataset getSource() throws Exception {
				IDataset model = new DatasetList();
				String[] infoTypeArr = infoTypes.split(",");
				for (int i = 0; i < infoTypeArr.length; i++) {
					IDataset ds = ParamMgr.getList("TD_B_IDTONAME", "SPECIAL_FLAG,INFO_TYPE", "1," + infoTypeArr[i]);
					model.addAll(ds);
				}
				return model;
			}
		});
```

#### 分布式缓存

**1.session_cache**
**会话缓存**：系统登录会话缓存

**2.shc_cache**
**共享缓存**：场景1：页面跳转时不需要携带大量数据进行下一个页面传递，类似网盘一样；场景2：分布式锁

**3.codecode_cache**
**CCD查询结果集缓存**：配置*.sql文件，这是一条根据优惠编码查询优惠信息的SQL，由于优惠表查询频繁，且数据不会经常变动，因此考虑将此SQL查询出来的结果集做缓存。
在sql文件的首行，打上--IS_CACHE=Y的标记，注意大写。
检查SQL涉及的所有表，是否存在于CACHE_TABLES中，如果不存在需要手工加上，如果已经存在就不用管了，注意大写，VERSION配置成SYSDATE，STATE配置成U，CACHE_TABLES用来保存表的版本号，通过改变版本号，可实现缓存的刷新，后面会说明原理。
select SQL_STMT from CODE_CODE where TAB_NAME = ? and SQL_REF = ? 先查询memCache缓存，如果没有直接查询数据库，然后在放入缓存

```
CODE_CODE字段
String tabName;
String sqlRef;
String sqlStmt;
List<String> sqlStmtLines;
char isCache;
int ttl;
```

**4.staticparam_cache**
**静态参数翻译缓存**：所有基于StaticUtil返回的数据都会被缓存，并且都存进了参数翻译缓存， 并不仅限于TD_S_STATIC表，静态参数缓存的更新：同样也是基于表的版本号进行更新 工具类：ParamMgr

**5.bcc_cache**
**并发控制缓存**：服务端调用频率控制，控制某服务在一分钟内能够被调用的次数。 作为核心支撑系统，会和许多周边系统有交互，而且很多都是实时接口。在设计该类实时
接口时，需要考虑到当外系统故障时，波及到自身的问题，业务调用被锁的外系统接口时，直接抛“XX系统处 理能力达到上限！”

#### 序列工具

用于获取数据库序列或者雪花算法序列id 工具类：SeqHelper

```
根据序列名称获取当前值，比如zhangsan，返回zhangsan1、zhangsan2、zhangsan3、、、、以此类推
mysql脚本：
CREATE TABLE `st_s_sequence` (
  `seq_name` varchar(50) NOT NULL,
  `current_val` int(20) NOT NULL,
  `increment_val` int(11) NOT NULL DEFAULT '1',
  `create_staff_id` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统序列号生成表';

函数：
CREATE DEFINER=`root`@`%` FUNCTION `currval`(seqname VARCHAR(50)) RETURNS bigint
    READS SQL DATA
BEGIN
  DECLARE current bigint;
  SET current = 0;
  SELECT current_val INTO current
    FROM td_m_sequence
   WHERE seq_name = upper(seqname);
  RETURN current;
END

CREATE DEFINER=`root`@`%` FUNCTION `nextval`(seqname VARCHAR(50)) RETURNS bigint
    DETERMINISTIC
BEGIN
  UPDATE td_m_sequence
     SET current_val = current_val + increment_val
   WHERE seq_name = upper(seqname);
  RETURN currval(seqname);
END


-- 插入序列到序列表
insert into td_m_sequence select 'aaaa1',0,1,'admin',NOW();

-- 查看序列当前值
select currval('aaaa1');

-- 查看序列下一个的值，并使序列当前值设置为下一个的值
select nextval('aaaa1');

```

#### 字符串工具

工具类：StringHelper

#### json工具

工具类：JsonHelper

什么是Helper类？
Helper类是一个包含一些常用方法或功能的类，用来辅助完成某个模块或任务的功能。它们通常不是直接提供业务功能的类，而是被其他类调用来完成一些特定的任务。Helper类的作用是提高代码的重用率、可维护性和可测试性，减少代码的冗余、依赖性和耦合度。在面向对象编程中，Helper类通常被归类为工具类或辅助类。
什么是Utility类？
Utility类是一种实用工具类，它包含一组静态方法，目的是提供常用的实用方法，例如字符串处理、日期处理、文件操作等。通常，它不应该被实例化，因为不需要其状态，而是通过调用静态方法来使用其中的功能。Utility类通常作为工具库或框架的一部分提供给开发人员使用。

Java Helper 和 Utility 类都是为了帮助程序员编写更加高效、可维护的代码而设计的，但它们之间有一些区别。 Java Helper
类通常是为了实现某一特定功能而创建的，它们通常是一组静态方法或常量的集合，可以在应用程序中的多个地方使用。Helper 类主要是为了在代码中进行功能分离，提高代码的可读性和可维护性。 Java Utility
类则是具有更加通用性的工具类，这些类通常包含一组静态方法，可以在多个应用程序中使用。这些类提供了一些常见的方法，比如日期计算、字符串操作、文件读写等。Utility 类通常是使代码更加灵活和可重用的方式。 因此，Java Helper
类主要通过分离功能来提高代码可读性和可维护性，而 Java Utility 类主要通过提供通用工具方法来提高代码的灵活性和可重用性。