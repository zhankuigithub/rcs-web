package com.longmaster.guixjks.handler.guixjks;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.annotation.RcsAction;
import com.longmaster.core.bean.annotation.RcsScene;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.CommonUtil;
import com.longmaster.core.util.RSAUtil;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.service.ICspService;
import com.longmaster.guixjks.service.IHealthInformationService;
import com.longmaster.core.original.Card;
import com.longmaster.core.original.Suggestion;
import com.longmaster.core.original.file.MaterialFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 健康资讯
 */
@Component
@RcsScene
public class HealthInformation {

    @Resource
    private ICspService mSendToCspService;

    @Resource
    private IHealthInformationService mHealthInformationService;

    @Resource
    private ICspService cspService;

    @Value("${guijk.url}")
    private String guijkUrl;

    private static final int PAGE_SIZE = 5;

    private Map<String, AtomicInteger> clickMap = new ConcurrentHashMap();

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    private static ClassLoader classLoader = HealthInformation.class.getClassLoader();

    private static JsonNode getRandomJson(String fileName) throws IOException {
        ArrayNode result = sObjectMapper.createArrayNode();
        int[] numbers = CommonUtil.randomNumber(5, 29);
        ObjectNode jsonNode = (ObjectNode) sObjectMapper.readTree(classLoader.getResource("static/" + fileName));
        JsonNode videos = jsonNode.get("list");
        for (int number : numbers) {
            result.add(videos.get(number));
        }
        return result;
    }

    /**
     * 健康资讯/换一批，实时
     *
     * @param maapMessage
     */
    @RcsAction(Constants.Scene.HEALTH_INFORMATION)
    public void healthInformation(MaapMessage maapMessage) throws JsonProcessingException {
        JsonNode news = mHealthInformationService.getRecommendsNew(maapMessage.getUserPhone(), 8, 0, 0);
        sendCard(news.get("data"), maapMessage, Constants.Scene.HEALTH_INFORMATION, Constants.Scene.HEALTH_INFORMATION_VIDEO, false);
    }

    /**
     * 健康资讯/换一批，模拟
     *
     * @param maapMessage
     */
    @RcsAction(Constants.Scene.HEALTH_INFORMATION_SIMULATION)
    public void healthInformationSimulation(MaapMessage maapMessage) throws IOException {
        sendCard(getRandomJson("information.json"), maapMessage, Constants.Scene.HEALTH_INFORMATION_SIMULATION, Constants.Scene.HEALTH_INFORMATION_VIDEO_SIMULATION, true);
    }


    /**
     * 视频资讯，实时
     */
    @RcsAction(Constants.Scene.HEALTH_INFORMATION_VIDEO)
    public void healthInformationVideo(MaapMessage maapMessage) throws IOException {
        clickMap.computeIfAbsent(maapMessage.getUserPhone(), k -> new AtomicInteger(1));
        JsonNode videos = mHealthInformationService.getVideos(1, PAGE_SIZE);
        sendCardOFVideo(videos.get("data"), maapMessage, Constants.Scene.HEALTH_INFORMATION_VIDEO, false);
    }

    /**
     * 视频资讯 换一批，实时
     */
    @RcsAction(Constants.Scene.HEALTH_INFORMATION_VIDEO_NEXT)
    public void healthInformationNext(MaapMessage maapMessage) throws JsonProcessingException {
        AtomicInteger atomicInteger = clickMap.get(maapMessage.getUserPhone());
        int pageIndex = atomicInteger == null ? 1 : atomicInteger.incrementAndGet();
        clickMap.computeIfAbsent(maapMessage.getUserPhone(), k -> atomicInteger);

        JsonNode videos = mHealthInformationService.getVideos(pageIndex, PAGE_SIZE);
        sendCardOFVideo(videos.get("data"), maapMessage, Constants.Scene.HEALTH_INFORMATION_VIDEO, false);
    }

    /**
     * 视频资讯/换一批，模拟
     */
    @RcsAction(Constants.Scene.HEALTH_INFORMATION_VIDEO_SIMULATION)
    public void healthInformationVideoSimulation(MaapMessage maapMessage) throws IOException {
        sendCardOFVideo(getRandomJson("video.json"), maapMessage, Constants.Scene.HEALTH_INFORMATION_VIDEO_SIMULATION, true);
    }

    /**
     * 获取资讯后发送卡片消息
     *
     * @param
     * @param maapMessage
     */
    private void sendCard(JsonNode jsonNode, MaapMessage maapMessage, String action1, String action2, boolean isSimulation) throws JsonProcessingException {

        // 构建悬浮菜单
        List<Suggestion> suggestions = new ArrayList<>();

        //换一批
        Suggestion suggestion1 = new Suggestion(Suggestion.REPLY, "换一批", action1);

        // 更多
        Suggestion suggestion2 = new Suggestion(Suggestion.ACTION,
                "更多资讯",
                guijkUrl + "/uni/#/pages/news/news" + "?source=10019&info=" + RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));

        // 视频
        Suggestion suggestion3 = new Suggestion(Suggestion.REPLY, "视频资讯", action2);

        //suggestions.add(suggestion);
        suggestions.add(suggestion1);
        suggestions.add(suggestion2);
        suggestions.add(suggestion3);

        ObjectNode objectNode = buildCommonBody(maapMessage, 3);
        objectNode.set("suggestions", sObjectMapper.readValue(sObjectMapper.writeValueAsString(suggestions), ArrayNode.class));

        // 卡片
        List<Card> cards = new ArrayList<>();

        ArrayNode jsonArray = (ArrayNode) jsonNode;
        jsonArray.forEach(object -> {

            String url = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), object.get("materialId").asLong(), maapMessage.getMessageBack());
            MaterialFile file = new MaterialFile(url);

            // 按钮
            List<Suggestion> suggestionList = new ArrayList<>();
            Suggestion sug = new Suggestion(Suggestion.ACTION,
                    "查看详情",
                    "http://m.39.net/news/quick_" + object.get("Id").asInt() + ".html");
            suggestionList.add(sug);

            Card card = new Card(object.get("Title").asText(), object.get("Summary").asText(), file, suggestionList);
            cards.add(card);
        });

        objectNode.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
        objectNode.set("layout", defaultStyle());
        mSendToCspService.sendToCsp(objectNode);
    }


    private void sendCardOFVideo(JsonNode jsonNode, MaapMessage maapMessage, String action, boolean isSimulation) throws JsonProcessingException {
        ObjectNode objectNode = buildCommonBody(maapMessage, 3);
        // 通用

        // 悬浮菜单
        List<Suggestion> suggestions = new ArrayList<>();

        //换一批
        Suggestion suggestion = new Suggestion(Suggestion.REPLY, "换一批", action);

        suggestions.add(suggestion);

        objectNode.set("suggestions", sObjectMapper.readValue(sObjectMapper.writeValueAsString(suggestions), ArrayNode.class));

        // 卡片
        List<Card> cards = new ArrayList<>();


        jsonNode.forEach(object -> {
            String title = StrUtil.isEmpty(object.get("subject_name").asText()) ? " " : object.get("subject_name").asText();
            String summary = object.get("intro_txt").asText();

            String url = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), object.get("material_id").asLong(), maapMessage.getMessageBack());
            MaterialFile file = new MaterialFile(url);

            //卡片按钮
            List<Suggestion> suggestionList = new ArrayList<>();
            Suggestion sug = new Suggestion(
                    Suggestion.ACTION,
                    "查看详情", guijkUrl + "/uni/#/pages/video/videoDetail?subject="
                    + URLEncoder.encode(object.toString())
                    + "&source=10019&info=" + RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));
            suggestionList.add(sug);

            Card card = new Card(title, summary, file, suggestionList);
            cards.add(card);
        });

        objectNode.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
        objectNode.set("layout", defaultStyle());
        mSendToCspService.sendToCsp(objectNode);
    }

    private JsonNode defaultStyle() {
        ObjectNode layout = sObjectMapper.createObjectNode();
        layout.put("width", "MEDIUM_WIDTH");
        layout.put("orientation", "VERTICAL");
        return layout;
    }


    private ObjectNode buildCommonBody(MaapMessage maapMessage, int messageType) {
        ObjectNode objectNode = sObjectMapper.createObjectNode();
        objectNode.put("type", messageType);
        objectNode.put("chatBotId", maapMessage.getDestinationAddress());
        objectNode.put("userPhone", maapMessage.getUserPhone());
        objectNode.put("messageId", maapMessage.getMessageId());
        objectNode.put("conversationId", maapMessage.getConversationId());
        objectNode.put("contributionId", maapMessage.getContributionId());
        objectNode.put("isMessageBack", maapMessage.getMessageBack());
        return objectNode;
    }
}
