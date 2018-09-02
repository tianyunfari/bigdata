----------------创建表----------------

--通话记录表
CREATE EXTERNAL TABLE call_record(
call STRING, 
called STRING,
call_date STRING) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' 
STORED AS TEXTFILE LOCATION '/test/call';

--group相关演示表
CREATE EXTERNAL TABLE group_test (
month STRING,
day STRING, 
cookieid STRING 
) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' 
STORED AS TEXTFILE LOCATION '/test/group_test';


1.窗口函数ROW_NUMBER,RANK,DENSE_RANK

row_number()  从1开始，按照顺序，生成分组内记录的序列

对于同一主叫电话，按通话次数的多少（即被叫电话）进行排序

select a.call,a.called,a.count,row_number() OVER (partition BY a.call order BY a.count DESC) rn
from (
select call,called,count(*) as count 
from call_record group by call,called 
) a;


对于每个主叫获取通话次数最多的被叫以及呼叫次数
select b.call,called,count
from 
(
select a.call,called,count,row_number() OVER (partition BY call order BY count DESC) rn
from 
(
select call,called,count(*) as count 
from call_record group by call,called
) a
) b
where b.rn <=2;

对于每个主叫获取最后一次通话的记录
select b.call,b.called,b.call_date
from 
(
select a.call,a.called,a.call_date,row_number() OVER (partition BY call order BY a.call_date DESC) rn
from call_record a
) b
where b.rn = 1;

rank() 生成数据项在分组中的排名，排名相等会在名次中留下空位
dense_rank() 生成数据项在分组中的排名，排名相等会在名次中不会留下空位

select 
a.call,
called,
count,
rank() over (partition by call order by count desc) rank,
dense_rank() over (partition by call order by count desc) drank,
row_number() OVER (partition BY call order BY count DESC) rn
from
(
select call,called,count(*) as count
from call_record group by call,called
) a;

2.CUBE,ROLLUP

CUBE根据GROUP BY的维度的所有组合进行聚合。

SELECT 
month,
day,
COUNT(DISTINCT cookieid) AS uv,
GROUPING__ID 
FROM group_test 
GROUP BY month,day WITH CUBE 
ORDER BY GROUPING__ID;

SELECT 
GROUPING__ID,
month,
day,
cookieid,
COUNT(*) AS pv
FROM group_test 
GROUP BY month,day,cookieid WITH CUBE 
ORDER BY GROUPING__ID,month,day,cookieid;

ROLLUP是CUBE的子集，以最左侧的维度为主，从该维度进行层级聚合。

比如，以month维度进行层级聚合：

SELECT 
month,
day,
COUNT(DISTINCT cookieid) AS uv,
GROUPING__ID  
FROM group_test 
GROUP BY month,day
WITH ROLLUP 
ORDER BY GROUPING__ID;
