#!/bin/bash

#hadoop=/home/hadoop/hadoop-2.6.0-cdh5.4.7/bin/hadoop

day=$1

hdfs dfs -rm -r /flume/format/pv/$day

hadoop jar /home/hadoop/jar/logformat-jar-with-dependencies.jar com.bigdata.format.PvForMatMR \
-files /home/hadoop/shell/data/dm_inter_url,/home/hadoop/shell/data/dm_outer_url,/home/hadoop/shell/data/ip_table \
-D input=/flume/logsplit/pv/$day \
-D output=/flume/format/pv/$day 
#>> /home/hadoop/loghandle/logs/2.log 2>&1
