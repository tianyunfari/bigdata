package com.bigdata.dao;

import com.bigdata.enums.ConstantEnum;
import com.bigdata.pojo.DMURLRule;
import com.bigdata.utils.DMURLRuleArrayList;
import com.bigdata.utils.IOFormatUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class DMOuterURLRuleDAO<E, T> extends AbstractDMDAO<E, T> {
    private static Logger logger = Logger.getLogger(DMOuterURLRuleDAO
        .class.getName());
    private DMURLRuleArrayList<DMURLRule> dmUrlRuleList = null;

    // 加载URL维度表
    public void parseDMObj(File file) throws IOException {
        this.dmUrlRuleList = new DMURLRuleArrayList<DMURLRule>(new
                ArrayList<DMURLRule>());

        for (String line : IOFormatUtil.ReadLinesEx(file, "utf-8")) {
            try {
                if (line.contains("#") || line.equals("")) {
                    continue;
                }
                String[] strPlate = line.split("\t", -1);
                DMURLRule dmurlRule = new DMURLRule(
                        Integer.parseInt(strPlate[0]), strPlate[1],
                        Integer.parseInt(strPlate[2]), strPlate[3],
                        Integer.parseInt(strPlate[4]), strPlate[5],
                        strPlate[6], Integer.parseInt(strPlate[7]), strPlate[8]);

                if (null != dmurlRule) {
                    dmUrlRuleList.add(dmurlRule);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.error("url format is wrong" + e.getMessage(),
                        e.getCause());
                continue;
            }

        }
    }

    //获取URL的一级分类，二级分类等信息
    public T getDMOjb(E param) throws Exception {
        Map<ConstantEnum, String> urlRuleMap = new WeakHashMap<ConstantEnum, String>();
        String url = (String)param;

        if (url == null || "".equals(url)) {
            urlRuleMap.put(ConstantEnum.URL_FIRST_ID, "1007");
            urlRuleMap.put(ConstantEnum.URL_SECOND_ID, "1041");
            urlRuleMap.put(ConstantEnum.URL_THIRD_ID, "1149");
            return (T) urlRuleMap;
        }

        DMURLRule dmurlRule = this.dmUrlRuleList.getDmURLRule(url);
        if (null == dmurlRule) {
            urlRuleMap.put(ConstantEnum.URL_FIRST_ID, "-1");
            urlRuleMap.put(ConstantEnum.URL_SECOND_ID, "-1");
            urlRuleMap.put(ConstantEnum.URL_THIRD_ID, "-1");

        } else if (dmurlRule.getFirstId() == 1007) {

            urlRuleMap.put(ConstantEnum.URL_FIRST_ID, "1008");
            urlRuleMap.put(ConstantEnum.URL_SECOND_ID, "1042");
            urlRuleMap.put(ConstantEnum.URL_THIRD_ID, "1150");
        } else {
            urlRuleMap.put(ConstantEnum.URL_FIRST_ID, dmurlRule.getFirstId()
                    + "");
            urlRuleMap.put(ConstantEnum.URL_SECOND_ID, dmurlRule.getSecondId()
                    + "");
            urlRuleMap.put(ConstantEnum.URL_THIRD_ID, dmurlRule.getThirdId()
                    + "");
        }
        return (T) urlRuleMap;
    }
}
