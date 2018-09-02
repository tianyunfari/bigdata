package com.bigdata.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class StringDecodeFormatUtil {
    private static final int URL_MAX_LENGTH = 100;
    public static String changeCharset(String str, String sourceCharset,
                                       String targetCharset) throws UnsupportedEncodingException {
        String targetStr = null;
        if (str != null) {
            byte[] byteString = str.getBytes(sourceCharset);
            targetStr = new String(byteString, targetCharset);
        }
        return targetStr;
    }

    public static String normalizeStr(String srcStr) {
        String keyStr = srcStr.trim();
        if (null == keyStr || "".equals(keyStr))
            return "";
        else{
            keyStr = keyStr.replace("\t", "");
            keyStr = keyStr.replace("\r\n", "");
            keyStr = keyStr.replace("\n", "");
            return keyStr;
        }
    }

    public static String decodeStr(String srcStr) throws Exception {
        if (null == srcStr || "".equals(srcStr))
            return "";
        String encodeStr = srcStr;
        if (srcStr.length() > URL_MAX_LENGTH)
            encodeStr = srcStr.substring(0, URL_MAX_LENGTH);

        String isoStr = URLDecoder.decode(encodeStr, "iso-8859-1");
        String rule = "^(?:[\\x00-\\x7f]|[\\xe0-\\xef][\\x80-\\xbf]{2})+$";

        if (isoStr.matches(rule)) {
            return URLDecoder.decode(encodeStr, "UTF-8");
        }
        else {
            return URLDecoder.decode(encodeStr, "GBK");
        }
    }

    public static String decodeCodedStr(String sourceStr, String targetCharset)
            throws UnsupportedEncodingException{
        String decodedStr;
        String changedStr = sourceStr; // String changedStr =
        // changeCharset(sourceStr,
        // targetCharset);
        if (changedStr != null) {
            try {
                decodedStr = URLDecoder.decode(
                        URLDecoder.decode(changedStr, targetCharset),
                        targetCharset);
                //decodedStr = URLDecoder.decode(changedStr, targetCharset);
            } catch (Exception e) {
                decodedStr = "";
                return decodedStr;
            }
            return decodedStr;
        }
        return "";
    }

}
