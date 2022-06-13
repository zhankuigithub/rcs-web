package com.longmaster.core.util;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

public class AuthorizationUtil {

    public static String getAuthStr(String gmt, String authID, String appId) {
        String token = Sha256Util.getSHA256Str(authID);
        String s = appId + ":" + Sha256Util.getSHA256Str(token + gmt);
        return "Basic " + Base64.getEncoder().encodeToString(s.getBytes());
    }

    public static String getGMTStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    public static String getAuthStrTmp(String gmt, String cspToken, String cspID) {
        String s = cspID + ":" + Sha256Util.getSHA256Str(cspToken + gmt);
        return "Basic " + Base64.getEncoder().encodeToString(s.getBytes());
    }

}
