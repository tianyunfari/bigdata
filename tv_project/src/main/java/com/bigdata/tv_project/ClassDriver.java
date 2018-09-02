/**
 * ClassDriver.java
 * com.bigdata.tv_project
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.tv_project;
import org.apache.hadoop.util.ProgramDriver;
/**
 * TODO(mvn 打包用)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-24 	 
 */
public class ClassDriver {
	public static void main(String argv[]){
		int exitCode = -1;
	    ProgramDriver pgd = new ProgramDriver();
	    try {
	    	pgd.addClass("channelRating", AnalyzeChannelRating.class, 
	    			"A map program that counts the channel rating in the input data.");
	    	pgd.addClass("programRating", AnalyzeProgramRating.class, 
	                "A map program that counts the program rating in the input data.");
	    	pgd.addClass("extractChannelAvg", ExtractChannelAvgAndReachNum.class,
	                "A map program that counts the program channel avg " +
	                "and reach num in the input data.");
	    	pgd.addClass("extractChannelNum", ExtractChannelNumAndTimeLen.class,
	                "A map program that counts the program channel " +
	                "num and time len in the input data.");
	    	pgd.addClass("extractProgramAvg", ExtractProgramAvgAndReachNum.class,
	                "A map program that counts the program channel " +
	                "num and time len in the input data.");
	    	pgd.addClass("extractChannelNum", ExtractProgramNumAndTimeLen.class,
	                "A map program that counts the program channel " +
	                "num and time len in the input data.");
	    	pgd.addClass("extractCurrentNum", ExtractCurrentNum.class,
	                "A map program that counts the current " +
	                "num in the input data.");
	    	pgd.addClass("parselog", ParseAndFilterLog.class,
	                "A map program that parse the log to get data");
	    	exitCode = pgd.run(argv);
	    }
	    catch(Throwable e){
	    	e.printStackTrace();
	    }
	    
	    System.exit(exitCode);
	  }
}

