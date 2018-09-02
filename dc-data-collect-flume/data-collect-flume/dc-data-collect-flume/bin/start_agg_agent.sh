#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

${flume_home}/bin/flume-ng agent \
--classpath ${lib_home}/log-flume-1.0-SNAPSHOT.jar \
--conf ${flume_home}/conf \
-f ${conf_home}/agg.conf -n a1 \
>> ${logs_home}/agg.log 2>&1 &

echo $! > ${logs_home}/agg.pid
