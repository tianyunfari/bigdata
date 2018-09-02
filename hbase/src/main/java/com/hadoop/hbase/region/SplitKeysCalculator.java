package com.hadoop.hbase.region;

public interface SplitKeysCalculator {
	byte[][] calcSplitKeys();
}
