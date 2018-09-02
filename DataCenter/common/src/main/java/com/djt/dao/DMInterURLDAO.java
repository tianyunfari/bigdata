package com.djt.dao;

import com.djt.enums.ConstantEnum;
import com.djt.enums.DMInterURLEnum;
import com.djt.pojo.DMURLRule;
import com.djt.utils.DMURLRuleArrayList;
import com.djt.utils.StringDecodeFormatUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class DMInterURLDAO {
	private static Logger logger = Logger.getLogger(DMInterURLDAO.class
			.getName());

	private DMURLRuleArrayList<DMURLRule> dmUrlRuleList = null;
	
	public static void main(String[] args) {
		DMInterURLDAO o = new DMInterURLDAO();
		try {
			o.parseDMObj(new File("dm_inter_url"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 风行域名变更导致下面同名方法不能应用，重新修改以适应变化
	 * @param file
	 * @throws IOException
	 * @author hujq 2014年5月29日下午12:37:01
	 */
	public void parseDMObj(File file) throws IOException {
		
		this.dmUrlRuleList = new DMURLRuleArrayList<DMURLRule>(new ArrayList<DMURLRule>());
		
		BufferedReader inFile = null;
		String line = null;
		
		try {
			inFile = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String [] arr = null;
			while ((line = inFile.readLine()) != null) {
				if(line.contains("#"))
					continue;
				arr = line.split("\t");
				
				
				DMURLRule dmURLRule = new DMURLRule(
						Integer.parseInt(arr[DMInterURLEnum.THIRD_ID.ordinal()]),
						arr[DMInterURLEnum.THIRD_NAME.ordinal()],
						Integer.parseInt(arr[DMInterURLEnum.SECOND_ID.ordinal()]), 
						arr[DMInterURLEnum.SECOND_NAME.ordinal()],
						Integer.parseInt(arr[DMInterURLEnum.FIRST_ID.ordinal()]), 
						arr[DMInterURLEnum.FIRST_NAME.ordinal()],
						arr[DMInterURLEnum.URL.ordinal()],
						Integer.parseInt(arr[DMInterURLEnum.NAME_ID.ordinal()]), 
						arr[DMInterURLEnum.NAME.ordinal()]);
				
				if (null != dmURLRule) {
					dmUrlRuleList.add(dmURLRule);
				}
			}
		} catch (Exception e) {
			logger.error(file.getName()+"配置文件加载失败！", e);
		}finally{
			if(inFile!=null)
				inFile.close();
		}
	}
	
	/**
	 * 获取url所属类别
	 * @param param
	 * @return
	 * @author hujq 2014年5月29日下午2:13:26
	 */
	public Map<ConstantEnum, String> getDMOjb(String param) {
		Map<ConstantEnum, String> map = new WeakHashMap<ConstantEnum, String>();
		map.put(ConstantEnum.URL_FIRST_ID, "-1");
		map.put(ConstantEnum.URL_SECOND_ID, "-1");
		map.put(ConstantEnum.URL_THIRD_ID, "-1");
		
		if ((param == null) || (param.equals(""))) {
		      return map;
		}

		DMURLRule dmURLRule= null;
		int urlLenth = 0;
		int lenthTmp = 0;
		for(DMURLRule o:dmUrlRuleList.getArrayList()){ //取配置文件中URL最长的做为所属类别URL
			if(param.contains(o.getUrl())){
				lenthTmp = o.getUrl().length();
				if(urlLenth<lenthTmp){
					urlLenth = lenthTmp;
					dmURLRule = o;
				}
			}
		}
		
		if(dmURLRule!=null){
			map.put(ConstantEnum.URL_FIRST_ID, String.valueOf(dmURLRule.getFirstId()));
			map.put(ConstantEnum.URL_SECOND_ID, String.valueOf(dmURLRule.getSecondId()));
			map.put(ConstantEnum.URL_THIRD_ID, String.valueOf(dmURLRule.getThirdId()));
			
			if (((!"http://fs.fun.tv/".equals(param)) && ("fs.fun.tv".equals(dmURLRule.getUrl()))) || ((!"http://www.fun.tv/".equals(param)) && ("www.fun.tv".equals(dmURLRule.getUrl())))) {
				map.put(ConstantEnum.URL_SECOND_ID, "-1");
				map.put(ConstantEnum.URL_THIRD_ID, "-1");
			}
		}
		
		return map;
	}
}
