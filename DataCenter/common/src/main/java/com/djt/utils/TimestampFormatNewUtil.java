package com.djt.utils;

import com.djt.enums.DefaultFieldValueEnum;
import com.djt.enums.UtilComstrantsEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampFormatNewUtil {

    public static String formatTimestamp(String timeStampStr,
            String logRundateIdStr) {

        SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
        String resultValue = null;
        Date date = null;
        try {
            date = new Date(Long.valueOf(timeStampStr) * 1000);
            String dateidParseFromTimestamp = time.format(date).trim();
            String houridParseFromTimestamp = String.valueOf(date.getHours());
            if (!dateidParseFromTimestamp.equalsIgnoreCase(logRundateIdStr)) {
                dateidParseFromTimestamp = logRundateIdStr;
                houridParseFromTimestamp = DefaultFieldValueEnum.hourIdDefault
                        .getValueStr();

            }
            resultValue = dateidParseFromTimestamp
                    + UtilComstrantsEnum.tabSeparator.getValueStr()
                    + houridParseFromTimestamp;
        }
        catch(Exception e) {
            // TODO Auto-generated catch block
            resultValue = logRundateIdStr
                    + UtilComstrantsEnum.tabSeparator.getValueStr()
                    + DefaultFieldValueEnum.hourIdDefault.getValueStr();
        }

        return resultValue;
    }

    public static String getTimestamp(String timeStampStr,
            String logRundateIdStr) {
        SimpleDateFormat timeDf = new SimpleDateFormat("yyyyMMdd");
        String resultValue = "0";
        try {
            new Date(Long.valueOf(timeStampStr) * 1000);
            resultValue = timeStampStr;
        }
        catch(NumberFormatException e) {
            // TODO Auto-generated catch block
            try {
                Date date = timeDf.parse(logRundateIdStr);
                resultValue = date.getTime() / 1000 + "";
            }
            catch(ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return resultValue;

    }

}
