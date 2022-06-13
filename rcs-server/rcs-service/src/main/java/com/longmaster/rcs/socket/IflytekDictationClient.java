package com.longmaster.rcs.socket;

import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.rcs.socket.listener.IflytekDictationListener;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * author zk
 * date 2021/9/26 17:52
 * description 语音听写客户端
 */
public class IflytekDictationClient {

    private static final String API_KEY = "03b6b4da2ea5460a4de5e5fc285de4fb";
    private static final String API_SECRET = "MTVjYWZhOGNjODc4MWEyMWUyYzBhOTNi";
    private static final String BASE_URL = "https://iat-api.xfyun.cn/v2/iat";
    private static final ThreadLocal<SimpleDateFormat> threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US));

    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = threadLocal.get();
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());

        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").
                append("date: ").append(date).append("\n").
                append("GET ").append(url.getPath()).append(" HTTP/1.1");

        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).
                addQueryParameter("date", date).
                addQueryParameter("host", url.getHost()).
                build();

        return httpUrl.toString();
    }

    public static void createConnection(byte[] bytes, MaapMessage message, String destination) {
        String authUrl = null;
        try {
            authUrl = getAuthUrl(BASE_URL, API_KEY, API_SECRET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, new IflytekDictationListener(bytes, message, destination));
        client.dispatcher().executorService().shutdown();
    }

}
