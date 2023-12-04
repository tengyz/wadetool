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
- 

**统一会话：**分布式应用下，不再依赖会话复制，将会话独立在WEB服务器外统一管控

**路由缓存：**提供对工号、地州、号码等路由策略

**权限缓存：**将权限与业务解耦，通过缓存独立访问；

**产品缓存：**将产品与业务解耦，通过缓存独立访问

**参数缓存：**将业务参数放入缓存管控

**服务缓存：**对服务的接入管控，并发管控；



#### 本地缓存

在我们的系统中，本地缓存也称JVM缓存，即存在于JVM内部，其本质是一个HashMap，适用于缓存少量数据，又需要以极高频率访问的场景，典型的例子就是：CODE_CODE模板、CACHE_TABLES表、TD_S_BIZENV表、MVEL配置表、数据模糊化配置表。
少量：控制在1W条记录以下，否则容易导致OOM 速度：单次访问 < 1微妙，即一秒钟能访问几百万次

特点 访问速度极快 容量有限 在所有JVM中冗余，在分布式环境中会缓存多份

**本地只读缓存**（IReadOnlyCache） 特点 通过实现loadData接口，采用一次性全量加载方式 100%命中 由于做了只读限制，多线程访问下可以做到无锁，极大提高访问速度，要求缓存数据一次性全量加载，确保缓存 100%命中。

**本地读写缓存**（IReadWriteCache） 特点 采用按需增量加载方式 采用LRU算法淘汰冷数据 多线程环境下需要采用读写锁控制数据一致性（框架级做）适合那些没法一次性全量加载的场合，采用按需加载方式，即先看缓存有没有，没有再查数据库或读文件，再将数据缓存起来。

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

````
获取数据库缓存时间，获取的是缓存时间，避免反复调用数据库查询数据库时间
Date getNow = TimeHelper.getSysTimeForDate();
````





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

###使用方法，可以这样获取缓存和查询数据库
private static final ICache cache = CacheManager.getCache("ACCT_CONFIG");
String key = "BATCH_JOB_TYPE_" + infoTypes;
return CacheUtil.get(cache, key, new ICacheSourceProvider<IDataList>() {
			@Override
			public IDataList getSource() throws Exception {
				IDataList model = new DataArrayList();
				String[] infoTypeArr = infoTypes.split(",");
				for (int i = 0; i < infoTypeArr.length; i++) {
					IDataList ds = ParamMgr.getList("TD_B_IDTONAME", "SPECIAL_FLAG,INFO_TYPE", "1," + infoTypeArr[i]);
					model.addAll(ds);
				}
				return model;
			}
		});

```

获取所有缓存

CacheContainer.getCacheList();

缓存清理

String[] cacheNameArr = (String[])data.get("CACHE_NAME");

CacheContainer.clear(cacheNameArr);



#### 分布式缓存（会话缓存、共享缓存、CCD缓存、静态参数缓存、并发控制缓存、 权限缓存）

**1.session_cache**
**会话缓存**：sna系统登录会话缓存

**2.shc_cache**
**共享缓存**：场景1：页面跳转时不需要携带大量数据进行下一个页面传递，类似网盘一样；场景2：分布式锁

````
SharedCache.set(sixMonInfo, sixMonInfos, 1800);
SharedCache.get(sixMonInfo)
````



**3.codecode_cache**
**CCD查询结果集缓存**（Memcache 集群）：SQL结果集缓存，配置*.sql文件，这是一条根据优惠编码查询优惠信息的SQL，由于优惠表查询频繁，且数据不会经常变动，因此考虑将此SQL查询出来的结果集做缓存。
在sql文件的首行，打上--IS_CACHE=Y的标记，注意大写。
检查SQL涉及的所有表，是否存在于CACHE_TABLES中，如果不存在需要手工加上，如果已经存在就不用管了，注意大写，VERSION配置成SYSDATE，STATE配置成U，CACHE_TABLES用来保存表的版本号，通过改变版本号，可实现缓存的刷新，后面会说明原理。
select SQL_STMT from CODE_CODE where TAB_NAME = ? and SQL_REF = ? 先查询memCache缓存，如果没有直接查询数据库，然后在放入缓存

```
先查询缓存，如果没有查询数据库再放入缓存
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

````
//分布式缓存也可以这样获取缓存和查询数据库
CacheUtil.get(CacheManager.getCache("REDIS_STATICPARAM_CACHE"),
				"TEST_TEST_2013_04_04", new ICacheSourceProvider<IDataList>() {
					@Override
					public IDataList getSource() throws Exception {
						IReadOnlyCache cache = CacheFactory
								.getReadOnlyCache(ReadOnlyCache.class);
						return (IDataList) cache.get("TEST_TEST_2013_04_04");
					}
});
````



**5.bcc_cache**
**并发控制缓存**（Memcache 集群）：业务并发缓存（全局锁），服务端调用频率控制，控制某服务在一分钟内能够被调用的次数。 作为核心支撑系统，会和许多周边系统有交互，而且很多都是实时接口。在设计该类实时
接口时，需要考虑到当外系统故障时，波及到自身的问题，业务调用被锁的外系统接口时，直接抛“XX系统处 理能力达到上限！”

````
String bccKey = "bcc-" + clientName + "-" + serviceName + "@" + (System.currentTimeMillis() >> 16);
long currentCount = bccCache.incrWithTTL(bccKey, 3600);

String msg = "接入渠道:" + clientName + ", 服务名:" + serviceName + 
        ", 流量已超硬阀值, 系统已开启自我保护! 预警阀值:" + cfg.warnThreshold + "次/分钟; 硬阀值:" + cfg.hardThreshold + "次/分钟，当前流量:" + currentCount + "次/分钟。";
      LOG.error(msg);
````



**本地缓存使用方法，快速开始**

````
本地缓存使用方法：
第一步：新建本地缓存配置文件localcache.xml，文件内容如下：
<?xml version = '1.0' encoding = 'UTF-8'?>
<localcaches>
    <!-- 本地只读缓存，配置刷新时间 cronExpr 只可配置：分 小时 日 月 周-->
    <readonly>
        <cache className="com.wade.framework.common.cache.readonly.DBSystemTimeCache" cronExpr="0 0 * * ?" init="true"/>
        <cache className="com.wade.framework.common.cache.readonly.UacCacheTablesCache" cronExpr="1 1 * * ?"
               init="true"/>
        <cache className="com.wade.framework.common.cache.readonly.ParamTablesCache" cronExpr="2 1 * * ?" init="true"/>
        <cache className="com.wade.framework.common.cache.readonly.StaticParamCache" cronExpr="3 1 * * ?"
               init="false"/>
    </readonly>
    <!-- 本地读写缓存-->
    <readwrite>
        <cache name="COMMON_CACHE" maxSize="1000" cronExpr="*/1 0 * * ?"/>
        <!--        <cache name="TD_M_APP" maxSize="1000" cronExpr="*/1 * * * ?"/>-->
        <cache name="LOGIN_USER_CACHE" maxSize="1000" cronExpr="30 * * * ?"/>
    </readwrite>
</localcaches>

第二步：新增配置--版本表：
CREATE TABLE `td_m_cache_tables` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `update_no` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `TABLE_NAME` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '缓存表名',
  `VERSION` datetime NOT NULL COMMENT '版本号, 用于缓存刷新',
  `STATE` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '状态：0是无效，1是有效',
  `REMARK` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`,`VERSION`,`TABLE_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='静态缓存表-系统缓存版本号';

插入语句：
INSERT INTO `uac_param`.`td_m_cache_tables` (`table_id`, `update_no`, `update_time`, `TABLE_NAME`, `VERSION`, `STATE`, `REMARK`) VALUES (1, 1, '2023-03-09 15:37:02', 'TD_M_STATIC', '2023-03-09 15:37:20', '1', '本地缓存开关');
INSERT INTO `uac_param`.`td_m_cache_tables` (`table_id`, `update_no`, `update_time`, `TABLE_NAME`, `VERSION`, `STATE`, `REMARK`) VALUES (2, 2, '2023-09-29 10:50:26', 'TD_M_APP', '2023-09-29 10:50:30', '1', 'cs');

第三步：新增缓存配置表
CREATE TABLE `td_m_param_tables` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `update_no` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `table_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名',
  `cache_columns` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '需要缓存的列',
  `sort_columns` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '按配置的字段排序,捞数据sql ORDER BY ',
  `data_src` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数据源',
  `need_load_all` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否加载全部 Y:是,N:否',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生失效标记 Y:生效,N:失效',
  `primary_columns` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缓存主键,列用逗号(,)隔开',
  `indexes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缓存索引列,索引列用逗号(,)隔开,多个索引用竖杠(|)分割',
  `remark` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统缓存表';

字段说明：
TABLE_NAME: 表名
CACHE_COLUMNS: 需要缓存的列
PRIMARY_COLUMNS: 缓存主键,列用逗号(,)隔开
INDEXES: 缓存索引列,索引列用逗号(,)隔开,多个索引用竖杠(|)分割
SORT_COLUMNS: 按配置的字段排序,捞数据sql ORDER BY 
DATA_SRC: 数据源

注意: 在使用ParamMgr获取缓存的时候一定要按表td_m_param_tables配置的主键和索引列查找 否则会出现缓存无法刷新的情况

插入语句：
INSERT INTO `uac_param`.`td_m_param_tables` (`table_id`, `update_no`, `update_time`, `table_name`, `cache_columns`, `sort_columns`, `data_src`, `need_load_all`, `status`, `primary_columns`, `indexes`, `remark`) VALUES (1, 1, '2023-03-09 15:16:12', 'TD_M_STATIC', 'TYPE_ID,DATA_ID,DATA_NAME,PDATA_ID', 'TYPE_ID', 'USER_CENTER_PARAM', 'Y', '1', 'TYPE_ID', 'TYPE_ID|DATA_ID,TYPE_ID|PDATA_ID,TYPE_ID', '静态字典表');
INSERT INTO `uac_param`.`td_m_param_tables` (`table_id`, `update_no`, `update_time`, `table_name`, `cache_columns`, `sort_columns`, `data_src`, `need_load_all`, `status`, `primary_columns`, `indexes`, `remark`) VALUES (2, 2, '2023-11-28 17:30:53', 'TD_M_APP', 'APP_ID,APP_NAME', 'APP_ID', 'product', 'N', '1', 'APP_ID', 'APP_ID', 'app表');

第四步：新增字典参数配置表：
CREATE TABLE `td_m_static` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `update_no` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `TYPE_ID` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `DATA_ID` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_NAME` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `VALID_FLAG` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `PDATA_ID` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `REMARK` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`table_id`,`TYPE_ID`,`DATA_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=209 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统常量配置表';

插入语句：
INSERT INTO `uac_param`.`td_m_static` (`table_id`, `update_no`, `update_time`, `TYPE_ID`, `DATA_ID`, `DATA_NAME`, `VALID_FLAG`, `PDATA_ID`, `REMARK`) VALUES (206, NULL, NULL, 'TEST', '1', 'TEST111', '1', 'pddd', '测试');
INSERT INTO `uac_param`.`td_m_static` (`table_id`, `update_no`, `update_time`, `TYPE_ID`, `DATA_ID`, `DATA_NAME`, `VALID_FLAG`, `PDATA_ID`, `REMARK`) VALUES (207, NULL, NULL, 'TEST', '2', 'TEST222', '1', 'pddd', '测试');
INSERT INTO `uac_param`.`td_m_static` (`table_id`, `update_no`, `update_time`, `TYPE_ID`, `DATA_ID`, `DATA_NAME`, `VALID_FLAG`, `PDATA_ID`, `REMARK`) VALUES (208, NULL, NULL, 'TEST', '3', 'TEST333', '1', 'pxxx', NULL);


第五步：在配置文件application.properties中新增数据库连接和账号密码，根据自己环境修改ip和账号密码，配置如下：
#本地缓存，配置数据库信息
cache.dbUrl=jdbc:mysql://127.43.37.123:3306/uac_param?useUnicode=true&characterEncoding=UTF-8
cache.dbUserName=root
cache.dbPassowrd=123456
#核心业务库
product.cache.dbUrl=jdbc:mysql://127.43.37.123:3306/uac_product?useUnicode=true&characterEncoding=UTF-8
product.cache.dbUserName=root
product.cache.dbPassowrd=123456
#是否开启分布式缓存
staticparam.disabled=true
#本读缓存读取分布式缓存redis配置
redis.address=127.43.37.123:7004,127.43.37.123:7005,127.43.37.123:7006
redis.master=mymaster
redis.password=123456

第六步：使用缓存工具ParamMgr或者CacheUtil.get以上方法进行获取缓存数据，示例如下：
//根据类型typeId和值id dataId获取具体值的名称
String getString1 = ParamMgr.getStaticValue("TEST", "1");
log.info("根据类型typeId和值id dataId获取具体值的名称getString1=：" + getString1);

//根据类型获取类型typeId下面所有的list
IDataList getList1 = ParamMgr.getStaticList("TEST");
log.info("根据类型获取类型typeId下面所有的list=getList1=：" + getList1);

//根据类型TYPE_ID获取list
IDataList getList2 = ParamMgr.getList("TD_M_STATIC", "TYPE_ID", "NATION_56");
log.info("==根据类型TYPE_ID获取list====getList==:" + getList2);//打印56个民族的list

//根据类型typeId和父id pDataId获取所有的值
IDataList getList3 = ParamMgr.getStaticListByParent("TEST", "pxxx");
log.info("根据类型typeId和父id pDataId获取所有的值getList3=：" + getList3);

//获取单个值的名称
IDataMap IDataMap4 = ParamMgr.getData("TD_M_APP", "APP_ID", "002022062864893455736832");
log.info("缓存获取getList1=：" + IDataMap4);

//获取模糊值的list
IDataList getList5 = ParamMgr.getListLike("TD_M_APP", "APP_ID", "'0020%'");
log.info("缓存获取getList5=：" + getList5);


//获取所有缓存
IDataList getListCache2 = CacheContainer.getCacheList();
log.info("缓存获取getListCache2=：" + getListCache2);
            
//缓存刷新
IDataMap getData = new DataHashMap();
getData.put("CACHE_NAME", "ALL");
String[] cacheNameArr = (String[])getData.getString("CACHE_NAME").split(",");
CacheContainer.clear(cacheNameArr);

//每次发版，更新了字典表等参数表时，需要根据表名称进行刷新缓存，更新表的版本号，实现批量多个微服务或者多个服务刷新
//表名就是table_id，更新哪张表，条件中就带哪张表的表名。不要全量更新，以免刷新失败
update td_m_cache_tables t set  t.version = now()  where   t.table_name in ('TD_M_STATIC','TD_M_APP')

````





#### 序列工具

用于获取数据库序列或者雪花算法序列id ，特点是一次获取10个序列放入缓存，避免频繁与数据库交互

工具类：SeqHelper

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

工具使用说明：
//批量获取10个序列id放入本地内存中，再获取的时候直接从内存获取的
log.info("======getSeqIdBySeqName==:" + SeqHelper.getSeqIdBySeqName("aaaa"));

```

#### 字符串工具

工具类：StringHelper





#### json工具

工具类：JsonHelper



工具类的科普

什么是Helper类？
Helper类是一个包含一些常用方法或功能的类，用来辅助完成某个模块或任务的功能。它们通常不是直接提供业务功能的类，而是被其他类调用来完成一些特定的任务。Helper类的作用是提高代码的重用率、可维护性和可测试性，减少代码的冗余、依赖性和耦合度。在面向对象编程中，Helper类通常被归类为工具类或辅助类。
什么是Utility类？
Utility类是一种实用工具类，它包含一组静态方法，目的是提供常用的实用方法，例如字符串处理、日期处理、文件操作等。通常，它不应该被实例化，因为不需要其状态，而是通过调用静态方法来使用其中的功能。Utility类通常作为工具库或框架的一部分提供给开发人员使用。

Java Helper 和 Utility 类都是为了帮助程序员编写更加高效、可维护的代码而设计的，但它们之间有一些区别。 Java Helper
类通常是为了实现某一特定功能而创建的，它们通常是一组静态方法或常量的集合，可以在应用程序中的多个地方使用。Helper 类主要是为了在代码中进行功能分离，提高代码的可读性和可维护性。 Java Utility
类则是具有更加通用性的工具类，这些类通常包含一组静态方法，可以在多个应用程序中使用。这些类提供了一些常见的方法，比如日期计算、字符串操作、文件读写等。Utility 类通常是使代码更加灵活和可重用的方式。 因此，Java Helper
类主要通过分离功能来提高代码可读性和可维护性，而 Java Utility 类主要通过提供通用工具方法来提高代码的灵活性和可重用性。