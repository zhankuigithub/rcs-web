package org.iflytek.msp.lfasr.model;



import org.iflytek.msp.lfasr.util.Base64Encoder;
import org.iflytek.msp.lfasr.util.Md5Tool;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;

public class Signature {

    private String appId;
    private String signa;
    private String ts;

    public Signature(String appId, String secretKey) throws NoSuchAlgorithmException, SignatureException {
        this.appId = appId;
        this.ts = String.valueOf((new Date()).getTime() / 1000L);
        this.signa = hmacSHA1Encrypt(Md5Tool.encrypt(appId + this.ts), secretKey);
    }

    private static String hmacSHA1Encrypt(String encryptText, String encryptKey) throws SignatureException {
        byte[] rawHmac;
        try {
            byte[] data = encryptKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(data, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] text = encryptText.getBytes(StandardCharsets.UTF_8);
            rawHmac = mac.doFinal(text);
        } catch (InvalidKeyException var7) {
            throw new SignatureException("InvalidKeyException:" + var7.getMessage());
        } catch (NoSuchAlgorithmException var8) {
            throw new SignatureException("NoSuchAlgorithmException:" + var8.getMessage());
        }

        Base64Encoder encoder = new Base64Encoder();
        return encoder.encode(rawHmac);
    }

    public String getAppId() {
        return this.appId;
    }

    public String getSigna() {
        return this.signa;
    }

    public String getTs() {
        return this.ts;
    }
}
