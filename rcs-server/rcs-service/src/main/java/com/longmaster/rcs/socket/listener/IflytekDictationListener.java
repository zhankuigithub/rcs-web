package com.longmaster.rcs.socket.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.TextUtil;
import com.longmaster.rcs.config.BeanConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * author zk
 * date 2021/9/26 14:12
 * description 科大讯飞语音听写接口
 */
@Slf4j
public class IflytekDictationListener extends WebSocketListener {

    private static final String APP_ID = "c1ff49bc";

    private static final ObjectMapper sObjectMapper = new ObjectMapper();
    private static final int STATUS_FIRST = 0;
    private static final int STATUS_PROCESS = 1;
    private static final int STATUS_END = 2;
    private final StringBuilder stringBuilder = new StringBuilder();

    private static final String ENCODING_SPEXX = "speex"; // speex压缩后的音频（8k）
    private static final String ENCODING_SPEXX_WB = "speex-wb"; // speex压缩后的音频（16k）
    private static final String ENCODING_PCM = "raw"; // 原生音频（支持单声道的pcm）
    private static final String ENCODING_MP3 = "lame"; // mp3格式

    private RocketMQTemplate rocketMQTemplate = BeanConfig.getBean(RocketMQTemplate.class);

    private final byte[] bytes; // 待发送的字节

    private MaapMessage maapMessage;

    private String destination;

    public IflytekDictationListener(byte[] bytes, MaapMessage maapMessage, String destination) {
        this.bytes = bytes;
        this.maapMessage = maapMessage;
        this.destination = destination;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if (this.bytes.length > 0) {
            sendDate(webSocket, bytes);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonNode jsonNode = sObjectMapper.readTree(text);

            if (jsonNode != null) {
                int code = jsonNode.get("code").asInt();
                String message = jsonNode.get("message").asText();
                String sid = jsonNode.get("sid").asText();

                if (code != 0) {
                    log.error("onMessage code：{}，message：{}，sid：{}", code, message, sid);
                    return;
                }

                JsonNode data = jsonNode.get("data");
                if (data != null) {
                    JsonNode result = data.get("result");

                    if (result != null) {
                        JsonNode ws = result.get("ws");
                        StringBuilder builder = new StringBuilder();
                        for (JsonNode node : ws) {
                            JsonNode cw = node.get("cw");
                            for (JsonNode son : cw) {
                                String w = son.get("w").asText();
                                builder.append(w);
                            }
                        }
                        stringBuilder.append(builder);
                    }

                    int status = data.get("status").asInt();
                    if (status == 2) {
                        log.info("最终识别结果：{}，sid:{}", stringBuilder, sid);
                        sendMessage(stringBuilder.toString());
                        webSocket.close(1000, "识别结束");
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        try {
            if (null != response) {
                int code = response.code();
                log.error("onFailure code：{}", code);
                log.error("onFailure body：{}", response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String content) throws IOException {
        content = TextUtil.filterSpecialChar(content);
        maapMessage.setBodyText(content);
        maapMessage.setContentType("text/plain;charset=UTF-8");

        rocketMQTemplate.asyncSendOrderly(this.destination, maapMessage, maapMessage.getUserPhone(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                try {
                    log.info("生产audio：{}，sendResult：{}", maapMessage, sObjectMapper.writeValueAsString(sendResult));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Throwable throwable) {
                log.error(throwable.toString());
                throwable.printStackTrace();
            }
        });
    }

    // 发送数据包
    private void sendDate(WebSocket webSocket, byte[] bytes) {
        int bytesLength = bytes.length;
        int start = 0;
        int readLength = 1280; // 每次发送1280个字节
        int interval = 40; // 每次间隔40ms

        int status = STATUS_FIRST;
        send:
        // 定义send循环标签
        while (true) {
            if (bytesLength <= 0) {
                status = STATUS_END;
            }
            ByteBuffer allocate = ByteBuffer.allocate(readLength);
//            allocate.clear();

            if (readLength > bytesLength) {
                readLength = bytesLength;
            }

            allocate.put(bytes, start, readLength);
  //          allocate.flip();

            switch (status) {

                case STATUS_FIRST:
                    webSocket.send(createFirstForm(allocate.array())); // 发送开始包，每一次任务只发送一次
                    status = STATUS_PROCESS;
                    break;

                case STATUS_PROCESS:
                    webSocket.send(createProcessForm(allocate.array())); // 发送过程包，此过程分为多次
                    break;

                case STATUS_END:
                    webSocket.send(createLastForm()); // 发送结束包，此过程只发送一次
                    break send; // 跳出send循环
            }

            start = start + readLength; // 更新开始读取的位置
            bytesLength = bytesLength - readLength; // 更新剩余字节长度

            try {
                TimeUnit.MILLISECONDS.sleep(interval); // 间隔40ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 构建开始包
    private String createFirstForm(byte[] bytes) {
        ObjectNode frame = sObjectMapper.createObjectNode();
        ObjectNode business = sObjectMapper.createObjectNode();
        ObjectNode common = sObjectMapper.createObjectNode();
        ObjectNode data = sObjectMapper.createObjectNode();

        common.put("app_id", APP_ID);

        business.put("language", "zh_cn");
        business.put("domain", "iat");
        business.put("accent", "mandarin");
        business.put("dwa", "wpgs");

        data.put("status", STATUS_FIRST);
        data.put("format", "audio/L16;rate=16000");
        data.put("encoding", ENCODING_MP3);
        data.put("audio", Base64.getEncoder().encodeToString(bytes));

        frame.set("common", common);
        frame.set("business", business);
        frame.set("data", data);
        return frame.toString();
    }

    // 构建过程包
    private String createProcessForm(byte[] bytes) {
        ObjectNode frame = sObjectMapper.createObjectNode();
        ObjectNode data = sObjectMapper.createObjectNode();
        data.put("status", STATUS_PROCESS);
        data.put("format", "audio/L16;rate=16000");
        data.put("encoding", ENCODING_MP3);
        data.put("audio", Base64.getEncoder().encodeToString(bytes));
        frame.set("data", data);
        return frame.toString();
    }

    // 构建结束包
    private String createLastForm() {
        ObjectNode frame = sObjectMapper.createObjectNode();
        ObjectNode data = sObjectMapper.createObjectNode();
        data.put("status", STATUS_END);
        data.put("audio", "");
        data.put("format", "audio/L16;rate=16000");
        data.put("encoding", ENCODING_MP3);
        frame.set("data", data);
        return frame.toString();
    }
}
