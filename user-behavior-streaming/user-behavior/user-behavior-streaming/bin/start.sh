#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

echo "put 'user_behavior_status','status','status:status','run'" | hbase shell

jars=""

for file in ${lib_home}/*
do
    if [ -z ${jars} ]; then
        jars=${file}
    else
        jars="${jars},${file}"
    fi
done

${spark_submit} \
--master spark://bigdata:7077 \
--deploy-mode client \
--name user-behavior \
--driver-memory 512M \
--executor-memory 512M \
--jars ${jars} \
--class com.djt.stream.streaming.UserBehaviorStreaming \
${lib_home}/user-behavior-1.0-SNAPSHOT.jar ${configFile} \
>> ${logs_home}/behavior.log 2>&1 &

echo $! > ${logs_home}/behavior_stream.pid
