package com.djt.dao;

import com.djt.enums.ConstantEnum;
import com.djt.pojo.DMIPRule;
import com.djt.utils.DMIPRuleArrayList;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class DMIPRuleDAO<E, T> extends AbstractDMDAO<E, T> {
	private static Logger logger = Logger.getLogger(DMIPRuleDAO.class.getName());
	private DMIPRuleArrayList<DMIPRule> dmIPRuleList = null;

	@Override
	public void parseDMObj(File file) throws IOException {
		BufferedReader in = null;
		try {
			this.dmIPRuleList = new DMIPRuleArrayList<DMIPRule>(new ArrayList<DMIPRule>());
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}

				String[] strPlate = line.split("\t");
				if (this.isContainsEmptyStrs(strPlate)) {
					continue;
				}
				try {

					DMIPRule dmIPRule = new DMIPRule(Long.parseLong(strPlate[0]),
							Long.parseLong(strPlate[1]),
							Long.parseLong(strPlate[2]),
							Long.parseLong(strPlate[3]),
							Long.parseLong(strPlate[4]));
					if (null != dmIPRule) {
						dmIPRuleList.add(dmIPRule);
					}
				} catch (Exception e) {
					logger.error("ip格式不对:" + e.getMessage(), e.getCause());
					continue;
				}


			}
		} finally {
			in.close();
		}

	}

	@Override
	public T getDMOjb(E param) {
		// TODO Auto-generated method stub
		Map<ConstantEnum, String> ipRuleMap = new WeakHashMap<ConstantEnum, String>();
		long iplong = (Long) param;
		DMIPRule dmIPRule = this.dmIPRuleList.getDmIPRule(iplong);
		if (null == dmIPRule) {
			ipRuleMap.put(ConstantEnum.PROVINCE_ID, "-1");
			ipRuleMap.put(ConstantEnum.CITY_ID, "-1");
			ipRuleMap.put(ConstantEnum.ISP_ID, "-1");
		} else {
			ipRuleMap.put(ConstantEnum.PROVINCE_ID, dmIPRule.getProvinceId()
					+ "");
			ipRuleMap.put(ConstantEnum.CITY_ID, dmIPRule.getCityId() + "");
			ipRuleMap.put(ConstantEnum.ISP_ID, dmIPRule.getIspId() + "");
		}
		return (T) ipRuleMap;
	}
}
