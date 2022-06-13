package org.iflytek.msp.lfasr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Tool {

    private Md5Tool() {
    }

    public static String encrypt(String pstr) throws NoSuchAlgorithmException {
        char[] md5String = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte[] btInput = pstr.getBytes();
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;
        byte[] var8 = md;
        int var9 = md.length;

        for(int var10 = 0; var10 < var9; ++var10) {
            byte byte0 = var8[var10];
            str[k++] = md5String[byte0 >>> 4 & 15];
            str[k++] = md5String[byte0 & 15];
        }

        return new String(str);
    }
}
