package com.djt.utils;

import java.util.ArrayList;
import java.util.List;

public final class StringFormatUtil {

    public static final char TAB_SEPARATOR = '\t';

    public static final char COMMA_SEPARATOR = ',';

    public static String[] splitLog(String content, char separator) {
        int length = content.length();
        int beginIndex = 0;
        List<String> pathStrings = new ArrayList<String>();
        for (int endIndex = 0; endIndex < length; endIndex++) {
            char ch = content.charAt(endIndex);
            if (ch == separator) {
                pathStrings.add(content.substring(beginIndex, endIndex));
                beginIndex = endIndex + 1;
            }
        }
        pathStrings.add(content.substring(beginIndex, length));

        return pathStrings.toArray(new String[0]);
    }

    public static String formatMessageId(String[] fields, String enumClassStr,
            String messageIdName) {
        String messageId = "-1";
        try {
            Class<Enum> logEnum = (Class<Enum>) Class.forName(enumClassStr);
            messageId = fields[Enum.valueOf(logEnum, messageIdName).ordinal()];
            if ("".equalsIgnoreCase(messageId.trim())
                    || messageId.trim().contains("null")) {
                messageId = "-1";
            }
        }
        catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return messageId;

    }

    /**
     * 
     * @Title: main
     * @Description: 这里用一句话描述这个方法的作用
     * @param @param args 参数说明
     * @return void 返回类型说明
     * @throws
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
