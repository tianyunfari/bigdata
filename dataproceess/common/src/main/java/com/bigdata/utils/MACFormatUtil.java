package com.bigdata.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MACFormatUtil {
    public static String getCorrectMac(String macStr) throws Exception {
        String macRex = "[0-9a-fA-F]{32}|"
                + "([0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2})|"
                + "[0-9a-fA-F]{12}|"
                + "[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}"
                + "|null|\\(null\\)|NULL|\\(NULL\\)";
        Pattern pattern = Pattern.compile(macRex);
        Matcher matcher = pattern.matcher(macStr);
        if (!matcher.matches() && !macStr.equalsIgnoreCase("")) {
            return "000000000000";
        }

        return macFormatToCorrectStr(macStr);
    }
    public static void isCorrectMac(String macStr) throws Exception {
        String macRex = "[0-9a-fA-F]{32}|"
                + "([0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2}[:\\.][0-9a-fA-F]{2})|"
                + "[0-9a-fA-F]{12}|"
                + "[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}"
                + "|null|\\(null\\)|NULL|\\(NULL\\)";
        Pattern pattern = Pattern.compile(macRex);
        Matcher matcher = pattern.matcher(macStr);
        if (!matcher.matches() && !macStr.equalsIgnoreCase("")) {
            throw new Exception("MAC 地址不簿符合格式规范" + macRex);
        }
    }
    public static String macFormatToCorrectStr(String macStr) {
        String returnMacStr = macStr;
        if (macStr.equals("")) {
            return "000000000000";
        }
        if (macStr.contains(":")) {
            returnMacStr = macStr.replaceAll(":", "");
        }
        if (macStr.contains(".")) {
            returnMacStr = macStr.replaceAll("\\.", "");
        }
        return returnMacStr.toUpperCase();
    }

}
