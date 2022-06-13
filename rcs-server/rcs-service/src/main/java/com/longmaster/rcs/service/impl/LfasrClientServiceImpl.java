package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.bean.maap.MaapFile;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.rcs.entity.ChatBotInfo;
import com.longmaster.rcs.entity.Lfasr;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.LfasrClientService;
import com.longmaster.rcs.service.channel.ICmService;
import com.longmaster.rcs.service.channel.ICtService;
import com.longmaster.rcs.service.channel.ICuService;
import com.longmaster.rcs.service.channel.IGxCmService;
import com.longmaster.rcs.socket.IflytekDictationClient;
import org.iflytek.msp.lfasr.LfasrClient;
import org.iflytek.msp.lfasr.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class LfasrClientServiceImpl implements LfasrClientService {

    private static LfasrClient lfasrClient;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    @Resource
    private Lfasr lfasr;

    @Resource
    private ICspService cspService;

    @Resource
    private ICtService mCTCCService;

    @Resource
    private ICuService mCUCCService;

    @Resource
    private ICmService mCMCCService;

    @Resource
    private IGxCmService mGXCMCCService;

    private static final Object sObject = new Object();

    @Resource
    private RestTemplate balancedRestTemplate;

    @Value("${rcs.csp-platform-url}")
    private String server;

    @Override
    public Map<String, Object> transformText(String path) throws InterruptedException, JsonProcessingException {
        Map<String, Object> res = new HashMap<>();

        if (lfasrClient == null) {
            synchronized (sObject) {
                if (lfasrClient == null) {
                    lfasrClient = LfasrClient.getInstance(lfasr.getAppId(), lfasr.getSecretKey());
                }
            }
        }
        Map<String, String> param = new HashMap<>(16);
        //语种： cn-中文（默认）;en-英文（英文不支持热词）
        param.put("language", "cn");
        //垂直领域个性化：法院-court；教育-edu；金融-finance；医疗-medical；科技-tech
        //param.put("pd", "finance");

        Message task = lfasrClient.upload(path, param);
        String taskId = task.getData();
        res.put("taskId", taskId);

        //3、查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JsonNode jsonNode = sObjectMapper.readTree(message.getData());
            status = jsonNode.get("status").asInt();
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(1);
        }
        res.put("result", lfasrClient.getResult(taskId));
        return res;
    }

    @Override
    public Map<String, Object> transformText(byte[] bytes) throws JsonProcessingException, InterruptedException {
        Map<String, Object> res = new HashMap<>();

        if (lfasrClient == null) {
            synchronized (sObject) {
                if (lfasrClient == null) {
                    lfasrClient = LfasrClient.getInstance(lfasr.getAppId(), lfasr.getSecretKey());
                }
            }
        }
        Map<String, String> param = new HashMap<>(16);
        //语种： cn-中文（默认）;en-英文（英文不支持热词）
        param.put("language", "cn");
        //垂直领域个性化：法院-court；教育-edu；金融-finance；医疗-medical；科技-tech
        //param.put("pd", "finance");

        Message task = lfasrClient.upload(bytes, param);
        String taskId = task.getData();
        res.put("taskId", taskId);

        //3、查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JsonNode jsonNode = sObjectMapper.readTree(message.getData());
            status = jsonNode.get("status").asInt();
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(1);
        }
        res.put("result", lfasrClient.getResult(taskId));
        return res;
    }

    @Override
    public Map<String, Object> transformText(MaapMessage message) throws JsonProcessingException, InterruptedException {
        List<MaapFile> maapFile = message.getMaapFile();
        if (maapFile != null && maapFile.size() > 0) {
            Optional<MaapFile> optional = maapFile.stream().filter(a -> a.getContentType().equals("audio/amr")).findFirst();
            if (optional.isPresent()) {
                MaapFile file = optional.get();
                String url = file.getUrl();

                ChatBotInfo chatBotInfo = cspService.getChatBotInfo(message.getDestinationAddress());

                if (chatBotInfo == null) {
                    return null;
                }

                Long carrierId = chatBotInfo.getCarrierId();

                if (carrierId == 3L) {
                    byte[] bytes = mCTCCService.downloadFile(chatBotInfo.getChatBotId(), url);
                    return transformText(bytes);
                }

                if (carrierId == 5L) {
                    byte[] bytes = mGXCMCCService.downloadFile(chatBotInfo.getChatBotId(), chatBotInfo.getAppId(), chatBotInfo.getAppKey(), url);
                    return transformText(bytes);
                }

            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public void dictation(MaapMessage message, String destination) {
        List<MaapFile> maapFile = message.getMaapFile();
        if (maapFile != null && maapFile.size() > 0) {
            Optional<MaapFile> optional = maapFile.stream().filter(a -> a.getContentType().equals("audio/amr")).findFirst();
            if (optional.isPresent()) {

                MaapFile file = optional.get();
                String url = file.getUrl();

                ChatBotInfo chatBotInfo = cspService.getChatBotInfo(message.getDestinationAddress());

                if (chatBotInfo == null) {
                    return;
                }

                Long carrierId = chatBotInfo.getCarrierId();
                byte[] bytes = new byte[0];

                if (carrierId == 1L) {
                    bytes = mCMCCService.downloadFile(chatBotInfo.getChatBotId(), chatBotInfo.getAppId(), chatBotInfo.getAppKey(), url);
                } else if (carrierId == 2L) {
                    bytes = mCUCCService.downloadFile(chatBotInfo.getChatBotId(), url);
                } else if (carrierId == 3L) {
                    bytes = mCTCCService.downloadFile(chatBotInfo.getChatBotId(), url);
                } else if (carrierId == 5L) {
                    bytes = mGXCMCCService.downloadFile(chatBotInfo.getChatBotId(), chatBotInfo.getAppId(), chatBotInfo.getAppKey(), url);
                }

                if (bytes.length > 0) {
                    // 生成amr
                    long millis = System.currentTimeMillis();
                    boolean b = createTmpFile(bytes, millis);
                    if (b) {

                        byte[] allBytes = new byte[0];
                        try {
                            allBytes = Files.readAllBytes(Paths.get("/data/rcs-service/tmp/" + millis + ".mp3"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Map<String, Object> map = new HashMap();
                        map.put("bytes", allBytes);
                        map.put("fileName", millis + ".mp3");
                        map.put("contentType", "audio/mp3");
                        ResponseEntity<JsonNode> response = balancedRestTemplate.exchange(server + "/api/message/uploadFile", HttpMethod.PUT, new HttpEntity<>(map), JsonNode.class);
                        if (response.getBody().get("code").asInt() == 200) {
                            String data = response.getBody().get("data").asText();
                            file.setMUrl(data);
                        }

                        IflytekDictationClient.createConnection(allBytes, message, destination);
                    }
                }
            }
        }
    }

    private boolean createTmpFile(byte[] bytes, long millis) {
        try {
            String fileName = millis + ".amr";
            String fileNameMp3 = millis + ".mp3";

            Path path = Paths.get("/data/rcs-service/tmp/" + fileName);
            Files.createFile(path);
            Files.write(path, bytes);

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("ffmpeg -i tmp/" + fileName + " -ac 1 -ar 16.0k tmp/" + fileNameMp3);
            if (process.waitFor() == 0) { // waitFor阻塞
                return true;
            }
            return false;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}
