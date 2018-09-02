package com.bigdata.dao;


import com.bigdata.enums.ConstantEnum;
import com.bigdata.pojo.DMIPRule;
import com.bigdata.utils.DMIPRuleArrayList;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class DMIPRuleDAO<E, T> extends AbstractDMDAO<E, T> {
    private static Logger logger = Logger.getLogger(DMIPRuleDAO.class.getName());
    private DMIPRuleArrayList<DMIPRule> dmIPRuleList = null;


    public void parseDMObj(File file) throws IOException {
        BufferedReader in = null;

        try {
            this.dmIPRuleList = new DMIPRuleArrayList<DMIPRule>(
                    new ArrayList<DMIPRule>());
            in = new BufferedReader(new InputStreamReader(new
                    FileInputStream(file)));
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
                    DMIPRule dmipRule = new DMIPRule(Long.parseLong(
                            strPlate[0]), Long.parseLong(strPlate[1]),
                            Long.parseLong(strPlate[2]),
                            Long.parseLong(strPlate[3]),
                            Long.parseLong(strPlate[4]));
                    if (dmipRule != null) {
                        dmIPRuleList.add(dmipRule);
                    }
                } catch (Exception e) {
                    logger.error("ip format is wrong" + e.getMessage(),
                            e.getCause());
                    continue;
                }

            }
        } finally {
            in.close();
        }
    }

    public T getDMOjb(E param) throws Exception {
        Map<ConstantEnum, String> ipRuleMap = new WeakHashMap<ConstantEnum, String>();

        long iplong = (Long)param;
        DMIPRule dmipRule = this.dmIPRuleList.getDmIPRule(iplong);

        if (null == dmipRule) {
            ipRuleMap.put(ConstantEnum.PROVINCE_ID, "-1");
            ipRuleMap.put(ConstantEnum.CITY_ID, "-1");
            ipRuleMap.put(ConstantEnum.ISP_ID, "-1");
        } else {
            ipRuleMap.put(ConstantEnum.PROVINCE_ID, dmipRule.getProvinceId() + "");
            ipRuleMap.put(ConstantEnum.CITY_ID, dmipRule.getCityId() + "");
            ipRuleMap.put(ConstantEnum.ISP_ID, dmipRule.getIspId() + "");
        }
        return (T)ipRuleMap;
    }
}

