#!/bin/bash

#hadoop=/home/hadoop/hadoop-2.6.0-cdh5.4.7/bin/hadoop

#mysql -h172.25.220.12 -uroot -proot -P 3306 logconfig -N -e \
"select concat_ws(',',log_id,log_name,log_url,hdfs_base_output) from meta_log where status=1" > /home/hadoop/loghandle/data/metalog.csv

#mysql -h172.25.220.12 -uroot -proot -P 3306 logconfig -N -e \
"select concat_ws(',',id,log_id,field_name,field_desc,seq) from meta_log_field where status=1 order by log_id,seq" > /home/hadoop/loghandle/data/metalogfield.csv

hadoop jar /home/hadoop/loghandle/lib/logsplit-jar-with-dependencies.jar com.djt.mapreduce.LogSplitMR \
-files /home/hadoop/shell/data/metalog.csv,/home/hadoop/shell/data/metalogfield.csv \
-D inputPath=/flume/events/20170524/*/* \
-D outputPath=/flume/logsplit \
-D day=$1
#>> /home/hadoop/loghandle/logs/1.log 2>&1
