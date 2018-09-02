package com.bigdata.dao;

import com.bigdata.enums.ConstantEnum;
import com.bigdata.enums.DMInterURLEnum;
import com.bigdata.pojo.DMURLRule;
import com.bigdata.utils.DMURLRuleArrayList;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class DMInterURLDAO {
    private static Logger logger = Logger.getLogger(DMInterURLDAO.class.getName());
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
     */
    public void parseDMObj(File file) throws IOException {
        this.dmUrlRuleList = new DMURLRuleArrayList<DMURLRule>(
                new ArrayList<DMURLRule>());
        BufferedReader inFile = null;
        String line = null;

        try {
            inFile = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            String[] arr = null;
            while ((line = inFile.readLine()) != null) {
                if (line.contains("#")) {
                    continue;
                }
                arr = line.split("\t");

                DMURLRule dmurlRule = new DMURLRule(
                        Integer.parseInt(arr[DMInterURLEnum.THIRD_ID.ordinal()]),
                        arr[DMInterURLEnum.THIRD_NAME.ordinal()],
                        Integer.parseInt(arr[DMInterURLEnum.SECOND_ID.ordinal()]),
                        arr[DMInterURLEnum.SECOND_NAME.ordinal()],
                        Integer.parseInt(arr[DMInterURLEnum.FIRST_ID.ordinal()]),
                        arr[DMInterURLEnum.FIRST_NAME.ordinal()],
                        arr[DMInterURLEnum.URL.ordinal()],
                        Integer.parseInt(arr[DMInterURLEnum.NAME_ID.ordinal()]),
                        arr[DMInterURLEnum.NAME.ordinal()]);

                if (dmurlRule != null) {
                    dmUrlRuleList.add(dmurlRule);
                }
            }

        } catch (Exception e) {
            logger.error(file.getName()+"load file fail!", e);
        } finally {
            if (inFile != null)
                inFile.close();
        }
    }

    /**
     * 获取url所属类别
     * @param param
     * @return
     */
    public Map<ConstantEnum, String> getDMOjb(String param) {
        Map<ConstantEnum, String> map = new WeakHashMap<ConstantEnum, String>();
        map.put(ConstantEnum.URL_FIRST_ID, "-1");
        map.put(ConstantEnum.URL_SECOND_ID, "-1");
        map.put(ConstantEnum.URL_THIRD_ID, "-1");

        if (param == null || param.equals("")) {
            return map;
        }

        DMURLRule dmurlRule = null;
        int urlLength = 0;
        int lengthTmp = 0;

        for(DMURLRule o:dmUrlRuleList.getArrayList()){ //取配置文件中URL最长的做为所属类别URL
            if(param.contains(o.getUrl())){
                lengthTmp = o.getUrl().length();
                if(urlLength<lengthTmp){
                    urlLength = lengthTmp;
                    dmurlRule = o;
                }
            }
        }
        if(dmurlRule!=null){
            map.put(ConstantEnum.URL_FIRST_ID, String.valueOf(dmurlRule.getFirstId()));
            map.put(ConstantEnum.URL_SECOND_ID, String.valueOf(dmurlRule.getSecondId()));
            map.put(ConstantEnum.URL_THIRD_ID, String.valueOf(dmurlRule.getThirdId()));

            if (((!"http://fs.fun.tv/".equals(param)) && ("fs.fun.tv".equals(dmurlRule.getUrl()))) || ((!"http://www.fun.tv/".equals(param)) && ("www.fun.tv".equals(dmurlRule.getUrl())))) {
                map.put(ConstantEnum.URL_SECOND_ID, "-1");
                map.put(ConstantEnum.URL_THIRD_ID, "-1");
            }
        }

        return map;
    }

}
