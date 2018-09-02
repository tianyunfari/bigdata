package com.hadoop.hbase.region;

public interface RowKeyGenerator {
	byte [] nextId();
}
