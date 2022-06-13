package org.iflytek.msp.lfasr.connect;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.iflytek.msp.lfasr.exception.LfasrException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Connector {
    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager pool;
    private RequestConfig config;

    private Connector() {
        this.pool = new PoolingHttpClientConnectionManager();
    }

    public static Connector build(int maxConnections, int connTimeout, int soTimeout, String proxyUrl) {
        Connector connector = ConnectorBuilder.connector;
        connector.pool.setMaxTotal(maxConnections);
        connector.pool.setDefaultMaxPerRoute(5);
        RequestConfig.Builder builder = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(connTimeout).setSocketTimeout(soTimeout);
        if (proxyUrl != null) {
            builder.setProxy(HttpHost.create(proxyUrl.trim()));
        }

        connector.config = builder.build();
        connector.httpClient = HttpClients.custom().setDefaultRequestConfig(connector.config).setConnectionManager(connector.pool).build();
        return connector;
    }

    public String post(String url, Map<String, String> param) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(convertMapToPair(param), Consts.UTF_8));
        return this.doExecute(httpPost, Consts.UTF_8.toString());
    }

    public String post(String url, Map<String, String> param, byte[] body) {
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
        reqEntity.addPart("content", new ByteArrayBody(body, ContentType.DEFAULT_BINARY, (String) param.get("slice_id")));
        Iterator var6 = param.entrySet().iterator();

        while (var6.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) var6.next();
            StringBody value = new StringBody((String) entry.getValue(), ContentType.create("text/plain", Consts.UTF_8));
            reqEntity.addPart((String) entry.getKey(), value);
        }

        HttpEntity httpEntry = reqEntity.build();
        httpPost.setEntity(httpEntry);
        return this.doExecute(httpPost, Consts.UTF_8.toString());
    }

    private String doExecute(HttpRequestBase requestBase, String charset) {
        CloseableHttpResponse response = null;

        try {
            String result;
            try {
                response = this.httpClient.execute(requestBase);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26005\", \"failed\":\"HTTP请求失败!\", \"data\":\"\"}");
                }

                result = charset == null ? EntityUtils.toString(response.getEntity()) : EntityUtils.toString(response.getEntity(), charset);
            } finally {
                if (null != response) {
                    EntityUtils.consumeQuietly(response.getEntity());
                }

                if (null != requestBase) {
                    requestBase.releaseConnection();
                }

            }

            return result;
        } catch (IOException var10) {
            throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26005\", \"failed\":\"HTTP请求失败!\", \"data\":\"\"}");
        }
    }

    public void release() {
        try {
            this.httpClient.close();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    private static List<NameValuePair> convertMapToPair(Map<String, String> params) {
        List<NameValuePair> pairs = new ArrayList();
        Iterator var2 = params.entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) var2.next();
            pairs.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
        }

        return pairs;
    }

    private static class ConnectorBuilder {
        private static Connector connector = new Connector();

        private ConnectorBuilder() {
        }
    }
}
