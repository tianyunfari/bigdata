#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)
bin_home=$home/bin
conf_home=$home/conf
logs_home=$home/logs
data_home=$home/data
lib_home=$home/lib

flume_home=/home/hadoop/app/apache-flume-1.7.0-bin