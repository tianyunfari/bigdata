package com.hadoop.hbase.region;

import java.util.Random;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;


public class HashRowKeyGenerator implements RowKeyGenerator {

	private long currentId = 1;
    private long currentTime = System.currentTimeMillis();
    private Random random = new Random();
    public byte[] nextId() {
    	System.out.println(currentId);
        try {
            currentTime += random.nextInt(1000);
            byte[] lowT = Bytes.copy(Bytes.toBytes(currentTime), 4, 4);
            byte[] lowU = Bytes.copy(Bytes.toBytes(currentId), 4, 4);
            return Bytes.add(MD5Hash.getMD5AsHex(Bytes.add(lowU, lowT)).substring(0, 8).getBytes(),
                    Bytes.toBytes(currentId));
        } finally {
            currentId++;
        }
    }
    
    public static void main(String[] args) {
    	HashChoreWoker worker = new HashChoreWoker(1000000,10);
        byte [][] splitKeys = worker.calcSplitKeys();
        for(int i=0;i<9;i++){
        	System.out.println(Bytes.toString(splitKeys[i]));
        }
	}

}
