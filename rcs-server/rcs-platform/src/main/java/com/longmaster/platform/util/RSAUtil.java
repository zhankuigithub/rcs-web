package com.longmaster.platform.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Component
public class RSAUtil {

    public static String sGuijkPubKey;
    public static String sGuijkPriKey;
    public static String sLmtxPubkey;

    @Value("${rsa.guijkPubkey}")
    private String guijkPubkey;

    @Value("${rsa.guijkPrikey}")
    private String guijkPrikey;

    @Value("${rsa.lmtxPubkey}")
    private String lmtxPubkey;

    @PostConstruct
    public void init() {
        sGuijkPubKey = guijkPubkey;
        sGuijkPriKey = guijkPrikey;
        sLmtxPubkey = lmtxPubkey;
    }

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    public static String getLmTxLoginInfo(String userPhone, String payload) {
        if (!StrUtil.isEmpty(userPhone)) {
            if (userPhone.length() > 11) {
                userPhone = userPhone.substring(userPhone.length() - 11);
            }
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("phone", userPhone);

            JsonNode node = sObjectMapper.readTree(payload);
            String text = node.get("action").get("urlAction").get("openUrl").get("url").asText();
            Map<String, String> stringMap = urlSplit(text);
            String type = stringMap.get("type");
            map.put("type", Integer.parseInt(type));
            String infoStr = sObjectMapper.writeValueAsString(map);
            infoStr = encrypt(infoStr, sLmtxPubkey);
            return URLEncoder.encode(infoStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getLoginInfo(String userPhone, String chatBotId) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", userPhone);
        map.put("chatBotId", chatBotId);

        String infoStr = null;
        try {
            infoStr = sObjectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            infoStr = encrypt(infoStr, sGuijkPubKey);
        } catch (Exception ex) {
            infoStr = "";
        }
        return URLEncoder.encode(infoStr);
    }

    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim().toLowerCase();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                for (int i = 1; i < arrSplit.length; i++) {
                    strAllParam = arrSplit[i];
                }
            }
        }
        return strAllParam;
    }

    public static Map<String, String> urlSplit(String URL) {
        Map<String, String> mapRequest = new HashMap<>();
        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //???????????????
            if (arrSplitEqual.length > 1) {
                //????????????
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //?????????????????????????????????
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * ?????????????????????
     *
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator??????????????????????????????????????????RSA??????????????????
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // ?????????????????????????????????????????????96-1024???
        keyPairGen.initialize(1024, new SecureRandom());
        // ?????????????????????????????????keyPair???
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // ????????????
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // ????????????
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // ?????????????????????
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
    }

    /**
     * RSA????????????
     *
     * @param str       ???????????????
     * @param publicKey ??????
     * @return ??????
     * @throws Exception ??????????????????????????????
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64???????????????
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA??????
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA????????????
     *
     * @param str        ???????????????
     * @param privateKey ??????
     * @return ??????
     * @throws Exception ??????????????????????????????
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64??????????????????????????????
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64???????????????
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA??????
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

}
