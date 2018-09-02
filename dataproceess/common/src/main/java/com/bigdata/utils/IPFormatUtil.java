package com.bigdata.utils;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPFormatUtil {
    private static Logger logger = Logger.getLogger(IPFormatUtil.class.getName());

    public static String ipFormat(String ipStr) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(ipStr);

        if (ipStr == null || "".equalsIgnoreCase(ipStr.trim()) ||
            !(matcher.matches())) {
            ipStr = "0.0.0.0";
        }

        return ipStr;
    }

    public static long ip2long(String ipStr) {
        String retVal = "0";
        long[] nFields = new long[4];

        try {
            ipStr = ipFormat(ipStr);
            String[] strFields = ipStr.split("\\.");
            for (int i = 0; i < 4; i++) {
                nFields[i] = Integer.parseInt(strFields[i]);
            }

            retVal = String.format("%s", (nFields[0] << 24)	| (nFields[1] << 16) | (nFields[2] << 8) | nFields[3]);

        } catch (Exception e) {
            logger.error("ip format wrong" + e.getMessage(), e.getCause());
        }
        return Long.parseLong(retVal);
    }
}
