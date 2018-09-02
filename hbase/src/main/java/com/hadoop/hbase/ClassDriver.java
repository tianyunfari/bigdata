package com.hadoop.hbase;

import org.apache.hadoop.util.ProgramDriver;

import com.hadoop.flume.RegexExtractorExtInterceptor;

public class ClassDriver {
	  public static void main(String argv[]){
		    int exitCode = -1;
		    ProgramDriver pgd = new ProgramDriver();
		    try {
		      pgd.addClass("Ansb", Anagrams.class, 
		                   "A map/reduce program that counts the anagrams in the input files.");
		      pgd.addClass("extractor", RegexExtractorExtInterceptor.class,
		                   "Aflume program that extractor the basename in the input files.");
		      exitCode = pgd.run(argv);
		    }
		    catch(Throwable e){
		      e.printStackTrace();
		    }
		    
		    System.exit(exitCode);
		  }
}
