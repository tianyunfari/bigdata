/**
 * IntPair.java
 * com.bigdata.mapreduce
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * TODO(自定义数据类型)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-26 	 
 */
public class IntPair implements WritableComparable<IntPair> {
	private int first;
	private int second;
	
	public IntPair(){}
	
	public IntPair(int left, int right) {
		set(left, right);
	}
	
	public void set(int left, int right) {
		this.first = left;
		this.second = right;
	}
	
	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		first = in.readInt();
		second = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(first);
		out.writeInt(second);
	}

	@Override
	public int compareTo(IntPair o) {
		// TODO Auto-generated method stub
		if (first != o.first) {
			return first < o.first ? -1 : 1;
		} else if (second != o.second) {
			return second < o.second ? -1 : 1;
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntPair other = (IntPair) obj;
		if (first != other.first)
			return false;
		if (second != other.second)
			return false;
		return true;
	}

}

