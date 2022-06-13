package com.longmaster.guixjks.handler.guixjks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.bean.annotation.RcsAction;
import com.longmaster.core.bean.annotation.RcsScene;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.RSAUtil;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.service.ICspService;
import com.longmaster.guixjks.service.ISaasService;
import com.longmaster.guixjks.service.TalkHistoryService;
import com.longmaster.core.original.Card;
import com.longmaster.core.original.Suggestion;
import com.longmaster.core.original.file.MaterialFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智能导诊
 */
@Component
@RcsScene
public class SaasScene {

    @Resource
    private ICspService mSendToCspService;

    @Resource
    private ISaasService saasService;

    @Resource
    private TalkHistoryService mTalkHistoryService;

    private Map<String, String> sessionMap = new ConcurrentHashMap<>();
    private Map<String, String> genderMap = new ConcurrentHashMap<>();
    private Map<String, String> ageMap = new ConcurrentHashMap<>();
    private Map<String, Integer> pregnantMap = new ConcurrentHashMap<>();

    // 记录上一次会话的科室
    private Map<String, String> departmentMap = new ConcurrentHashMap<>();

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    private static ClassLoader classLoader = SaasScene.class.getClassLoader();

    @Value("${guijk.url}")
    private String guijkUrl;

    @Value("${material.saas.defaultAvatarId}")
    private long defaultAvatarId;

    @Value("${material.saas.manAvatarId}")
    private long manAvatarId;

    @Value("${material.saas.womanAvatarId}")
    private long womanAvatarId;

    @Value("${material.saas.orthopedicsDepartmentId}")
    private long orthopedicsDepartmentId;

    @Value("${material.saas.commonDepartmentId}")
    private long commonDepartmentId;

    @Resource
    private ICspService cspService;

    /**
     * 选择问诊
     *
     * @param maapMessage
     */
    @RcsAction(Constants.ToSeeDoctor.EN_SEE_DOCTOR)
    public void seeDoctor(MaapMessage maapMessage) throws IOException {
        if (!maapMessage.getBelongPostBack()) {
            sendChat(maapMessage);
            return;
        }

        ObjectNode jsonNode = (ObjectNode) sObjectMapper.readTree(classLoader.getResource("template/ask-1.json"));

        jsonNode.put("userPhone", maapMessage.getUserPhone());
        jsonNode.put("chatBotId", maapMessage.getDestinationAddress());
        jsonNode.put("messageId", maapMessage.getMessageId());
        jsonNode.put("conversationId", maapMessage.getConversationId());
        jsonNode.put("contributionId", maapMessage.getContributionId());
        jsonNode.put("isMessageBack", maapMessage.getMessageBack());

        mSendToCspService.sendToCsp(jsonNode);
    }


    /**
     * 选择男性
     *
     * @param maapMessage
     */
    @RcsAction(Constants.ToSeeDoctor.EN_SEE_DOCTOR_SEX_MAN)
    public void seeDoctorSexMan(MaapMessage maapMessage) throws IOException {
        if (!maapMessage.getBelongPostBack()) {
            sendChat(maapMessage);
            return;
        }
        genderMap.put(maapMessage.getUserPhone(), Constants.ToSeeDoctor.Patient.MALE);

        ObjectNode jsonNode = (ObjectNode) sObjectMapper.readTree(classLoader.getResource("template/ask-2.json"));
        jsonNode.put("userPhone", maapMessage.getUserPhone());
        jsonNode.put("chatBotId", maapMessage.getDestinationAddress());
        jsonNode.put("messageId", maapMessage.getMessageId());
        jsonNode.put("conversationId", maapMessage.getConversationId());
        jsonNode.put("contributionId", maapMessage.getContributionId());
        jsonNode.put("isMessageBack", maapMessage.getMessageBack());

        mSendToCspService.sendToCsp(jsonNode);
    }


    /**
     * 选择女性
     *
     * @param maapMessage
     */
    @RcsAction(Constants.ToSeeDoctor.EN_SEE_DOCTOR_SEX_WOMAN)
    public void seeDoctorSexWoMan(MaapMessage maapMessage) throws IOException {
        if (!maapMessage.getBelongPostBack()) {
            sendChat(maapMessage);
            return;
        }
        genderMap.put(maapMessage.getUserPhone(), Constants.ToSeeDoctor.Patient.FEMALE);

        ObjectNode jsonNode = (ObjectNode) sObjectMapper.readTree(classLoader.getResource("template/ask-2.json"));
        jsonNode.put("userPhone", maapMessage.getUserPhone());
        jsonNode.put("chatBotId", maapMessage.getDestinationAddress());
        jsonNode.put("messageId", maapMessage.getMessageId());
        jsonNode.put("conversationId", maapMessage.getConversationId());
        jsonNode.put("contributionId", maapMessage.getContributionId());
        jsonNode.put("isMessageBack", maapMessage.getMessageBack());

        mSendToCspService.sendToCsp(jsonNode);
    }

    /**
     * 选择年龄
     *
     * @param maapMessage
     */
    @RcsAction({Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_1,
            Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_2,
            Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_3,
            Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_4,
            Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_5,
            Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_6})
    public void seeDoctorAge(MaapMessage maapMessage) throws IOException {
        if (!maapMessage.getBelongPostBack()) {
            sendChat(maapMessage);
            return;
        }
        // 女性 16-35  36-60时

        String userPhone = maapMessage.getUserPhone();
        ageMap.put(userPhone, maapMessage.getScene().substring(maapMessage.getScene().length() - 1));
        String gender = genderMap.get(userPhone);

        if (Constants.ToSeeDoctor.Patient.FEMALE.equals(gender) &&
                (Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_3.equals(maapMessage.getScene())
                        || Constants.ToSeeDoctor.EN_SEE_DOCTOR_AGE_4.equals(maapMessage.getScene()))) {

            ObjectNode jsonNode = (ObjectNode) sObjectMapper.readTree(classLoader.getResource("template/ask-3.json"));
            jsonNode.put("userPhone", maapMessage.getUserPhone());
            jsonNode.put("chatBotId", maapMessage.getDestinationAddress());
            jsonNode.put("messageId", maapMessage.getMessageId());
            jsonNode.put("conversationId", maapMessage.getConversationId());
            jsonNode.put("contributionId", maapMessage.getContributionId());
            jsonNode.put("isMessageBack", maapMessage.getMessageBack());

            mSendToCspService.sendToCsp(jsonNode);

        } else {
            // 男性或者女性不在规定年龄内
            pregnantMap.put(userPhone, 0);
            // 获取session
            sendNewSession(userPhone);
            maapMessage.setBodyText("症状自查");
            sendChat(maapMessage);
        }
    }

    /**
     * 选择是否怀孕
     *
     * @param maapMessage
     */
    @RcsAction({Constants.ToSeeDoctor.EN_SEE_DOCTOR_PREGNANT_1, Constants.ToSeeDoctor.EN_SEE_DOCTOR_PREGNANT_0})
    public void seeDoctorIsPregnant(MaapMessage maapMessage) {
        if (!maapMessage.getBelongPostBack()) {
            sendChat(maapMessage);
            return;
        }
        String userPhone = maapMessage.getUserPhone();
        pregnantMap.put(userPhone, Integer.parseInt(maapMessage.getScene().substring(maapMessage.getScene().length() - 1)));

        // 获取session
        sendNewSession(userPhone);
        maapMessage.setBodyText("症状自查");
        sendChat(maapMessage);
    }

    /**
     * 第一次回答
     *
     * @param maapMessage
     */
    @RcsAction(Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_CHAT)
    public void seeDoctorChat(MaapMessage maapMessage) {
        if (!maapMessage.getBelongPostBack()) {
            sendChat(maapMessage);
            return;
        }
        sendSelect(maapMessage);
    }

    /**
     * 手动点击recommend
     *
     * @param maapMessage
     */
    @RcsAction(Constants.ToSeeDoctor.SBC_MENU_GUIDANCE_RECOMMEND)
    public void seeDoctorRecommend(MaapMessage maapMessage) throws JsonProcessingException {
        recommend("", departmentMap.get(maapMessage.getUserPhone()), maapMessage);
    }

    /**
     * 继续自查
     *
     * @param maapMessage
     */
    @RcsAction(Constants.ToSeeDoctor.SBC_MENU_GUIDANCE_SYMPTOMS)
    public void seeDoctorContinue(MaapMessage maapMessage) {
        sendContinue(maapMessage);
    }

    /**
     * 去问诊
     *
     * @param maapMessage
     */
    @RcsAction(Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_ASK)
    public void seeDoctorGoAsk(MaapMessage maapMessage) {
        Map map = new HashMap<String, Object>();
        map.put("session_id", sessionMap.get(maapMessage.getUserPhone()));
        map.put("doctor_id", maapMessage.getScene().replace(Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_ASK, ""));
        JsonNode access = saasService.access(map);
    }


    // 保存session
    private void sendNewSession(String userPhone) {
        Map params = new HashMap();
        params.put("gender", genderMap.get(userPhone));
        params.put("age", ageMap.get(userPhone));
        params.put("pregnant", pregnantMap.get(userPhone));
        JsonNode jsonObject = saasService.triageGetSession(params);

        JsonNode data = jsonObject.get("data");
        String action = data.get("action").asText();

        if ("result".equals(action)) {
            JsonNode results = data.get("results");
            sessionMap.put(userPhone, results.get("session").asText());
        }
    }


    // 导诊通用 chat select continue
    private void resolveTriage(JsonNode jsonObject, MaapMessage maapMessage) throws IOException {

        JsonNode data = jsonObject.get("data");
        String action = data.get("action").asText();

        // 继续往下选
        if ("select".equals(action)) {

            JsonNode results = data.get("results");
            ArrayNode list = (ArrayNode) results.get("options");
            // 自行构建生成下一次推送的btn
            List<Suggestion> suggestions = new ArrayList<>();

            for (int i = 0; i < (Math.min(list.size(), 10)); i++) {
                if (list.get(i).size() > 8) {
                    continue;
                }
                String text = list.get(i).asText();

                if (text.length() > 8) {
                    text = text.substring(0, 7) + "...";
                }
                Suggestion suggestion = new Suggestion(Suggestion.REPLY, text, Constants.ToSeeDoctor.Dynamic.SBC_MENU_GUIDANCE_CHAT + list.get(i).asText());
                suggestions.add(suggestion);
            }

            ObjectNode objectNode = sObjectMapper.createObjectNode();
            objectNode.set("suggestions", sObjectMapper.readValue(sObjectMapper.writeValueAsString(suggestions), ArrayNode.class));
            objectNode.put("type", 1);
            objectNode.put("chatBotId", maapMessage.getDestinationAddress());
            objectNode.put("content", results.get("title").asText());
            objectNode.put("userPhone", maapMessage.getUserPhone());

            objectNode.put("messageId", maapMessage.getMessageId());
            objectNode.put("conversationId", maapMessage.getConversationId());
            objectNode.put("contributionId", maapMessage.getContributionId());
            objectNode.put("isMessageBack", maapMessage.getMessageBack());

            mSendToCspService.sendToCsp(objectNode);
            return;
        }

        // 得到了结果
        if ("redirect".equals(action)) {
            JsonNode results = data.get("results");
            if (results.getNodeType() != JsonNodeType.ARRAY) {
                return;
            }

            ArrayNode arrayNode = (ArrayNode) results;
            StringBuilder sb = new StringBuilder();
            for (JsonNode json : arrayNode) {
                String id = json.get("id").asText(); // 症状
                sb.append(id);
                sb.append("|");
            }
            String disease = sb.substring(0, sb.length() - 1);

            ObjectNode objectNode = sObjectMapper.createObjectNode();
            objectNode.put("type", 1);
            objectNode.put("chatBotId", maapMessage.getDestinationAddress());
            objectNode.put("content", "通过诊断您可能患有以下疾病：" + disease + "，并为您推荐如下医生：");
            objectNode.put("userPhone", maapMessage.getUserPhone());
            objectNode.put("messageId", UUID.randomUUID().toString());
            objectNode.put("conversationId", maapMessage.getConversationId());
            objectNode.put("contributionId", maapMessage.getContributionId());
            objectNode.put("isMessageBack", maapMessage.getMessageBack());

            mSendToCspService.sendToCsp(objectNode);

            // 触发recommend
            recommend(sb.toString(), "", maapMessage);
            return;
        }

        // 显示科室
        if ("display".equals(action)) {

            ObjectNode objectNode = sObjectMapper.createObjectNode();
            objectNode.put("type", 1);
            objectNode.put("chatBotId", maapMessage.getDestinationAddress());
            objectNode.put("content", "为您推荐如下科室");
            objectNode.put("userPhone", maapMessage.getUserPhone());
            objectNode.put("messageId", UUID.randomUUID().toString());
            objectNode.put("conversationId", maapMessage.getConversationId());
            objectNode.put("contributionId", maapMessage.getContributionId());
            objectNode.put("isMessageBack", maapMessage.getMessageBack());

            mSendToCspService.sendToCsp(objectNode);

            // 生成推送的单卡片
            JsonNode results = data.get("results");

            String name = results.get("name").asText();
            String fileUrl = "骨科".equals(name) ?
                    cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), orthopedicsDepartmentId, maapMessage.getMessageBack())
                    : cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), commonDepartmentId, maapMessage.getMessageBack());

            MaterialFile file = new MaterialFile(fileUrl);
            Card card = new Card(name, results.get("brief").asText(), file, null);

            ArrayNode arrayNode = sObjectMapper.createArrayNode();
            arrayNode.add(sObjectMapper.readTree(sObjectMapper.writeValueAsString(card)));

            ObjectNode root = sObjectMapper.createObjectNode();
            root.put("type", 2);
            root.put("chatBotId", maapMessage.getDestinationAddress());
            root.set("content", arrayNode);
            root.put("userPhone", maapMessage.getUserPhone());
            root.set("suggestions", buildDefaultInputButton());

            root.put("messageId", UUID.randomUUID().toString());
            root.put("conversationId", maapMessage.getConversationId());
            root.put("contributionId", maapMessage.getContributionId());
            root.set("layout", defaultStyle());
            root.put("isMessageBack", maapMessage.getMessageBack());

            mSendToCspService.sendToCsp(root);
            departmentMap.put(maapMessage.getUserPhone(), results.get("name").asText());// 存科室，为下一次点击服务
        }
    }


    private ArrayNode buildDefaultInputButton() {

        List<Suggestion> suggestions = new ArrayList<>();

        Suggestion suggestion1 = new Suggestion(Suggestion.REPLY, "继续询问", Constants.ToSeeDoctor.SBC_MENU_GUIDANCE_SYMPTOMS);
        Suggestion suggestion2 = new Suggestion(Suggestion.REPLY, "推荐医生", Constants.ToSeeDoctor.SBC_MENU_GUIDANCE_RECOMMEND);

        suggestions.add(suggestion1);
        suggestions.add(suggestion2);

        try {
            return sObjectMapper.readValue(sObjectMapper.writeValueAsString(suggestions), ArrayNode.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成卡片内默认按钮
     *
     * @param doctorID
     * @return
     */
    private List<Suggestion> buildDefaultAskButton(int doctorID, String userPhone, String chatBotId) {
        List<Suggestion> suggestions = new ArrayList<>();
        Suggestion suggestion1 = new Suggestion(
                Suggestion.ACTION,
                "去问诊",
                guijkUrl + "/uni/#/pages/askDoctor/doctorDetails?doc_id=" + doctorID + "&source=10019&info=" + RSAUtil.getLoginInfo(userPhone, chatBotId));
        suggestions.add(suggestion1);
        return suggestions;
    }


    private void recommend(String disease, String department, MaapMessage maapMessage) throws JsonProcessingException {
        Map map = new HashMap();
        map.put("department", department);
        map.put("devprovince", "贵州");
        map.put("devcity", "");
        map.put("disease", disease);
        map.put("session", sessionMap.get(maapMessage.getUserPhone()));
        JsonNode jsonObject = saasService.recommend(map);

        JsonNode data = jsonObject.get("data");
        JsonNode doctorList = data.get("items");

        if (doctorList.getNodeType() != JsonNodeType.ARRAY) {
            return;
        }

        List<Card> cards = new ArrayList<>();

        for (JsonNode doctor : doctorList) {

            String fileUrl = "";
            int gender = doctor.get("gender").asInt();

            switch (gender) {
                case 0:
                    fileUrl = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), defaultAvatarId, maapMessage.getMessageBack());
                    break;
                case 1:
                    fileUrl = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), manAvatarId, maapMessage.getMessageBack());
                    break;
                case 2:
                    fileUrl = cspService.getAuditMaterialUrl(maapMessage.getDestinationAddress(), womanAvatarId, maapMessage.getMessageBack());
                    break;
            }

            MaterialFile file = new MaterialFile(fileUrl);
            Card card = new Card(
                    doctor.get("doctor_name").asText(),
                    doctor.get("hospital").asText() + "   " + doctor.get("department").asText(),
                    file,
                    buildDefaultAskButton(doctor.get("doctor_id").asInt(), maapMessage.getUserPhone(), maapMessage.getDestinationAddress()));

            cards.add(card);
        }

        // 悬浮按钮
        List<Suggestion> suggestions = new ArrayList<>();

        Suggestion suggestion1 = new Suggestion(Suggestion.REPLY, "返回", Constants.Scene.GUIXJK_RETURN_HOME);
        Suggestion suggestion2 = new Suggestion(Suggestion.REPLY, "再来一次", Constants.ToSeeDoctor.EN_SEE_DOCTOR);
        Suggestion suggestion3 = new Suggestion(Suggestion.ACTION, "更多医生",
                guijkUrl + "/uni/#/" + "?source=10019&info=" + URLEncoder.encode(RSAUtil.getLoginInfo(maapMessage.getUserPhone(), maapMessage.getDestinationAddress())));

        suggestions.add(suggestion1);
        suggestions.add(suggestion2);
        suggestions.add(suggestion3);


        ObjectNode root = sObjectMapper.createObjectNode();
        root.put("type", 3);
        root.put("chatBotId", maapMessage.getDestinationAddress());
        root.set("content", sObjectMapper.readValue(sObjectMapper.writeValueAsString(cards), ArrayNode.class));
        root.put("userPhone", maapMessage.getUserPhone());
        root.set("suggestions", sObjectMapper.readValue(sObjectMapper.writeValueAsString(suggestions), ArrayNode.class));
        root.put("messageId", UUID.randomUUID().toString());
        root.put("conversationId", maapMessage.getConversationId());
        root.put("contributionId", maapMessage.getContributionId());
        root.set("layout", defaultStyle());
        root.put("isMessageBack", maapMessage.getMessageBack());

        mSendToCspService.sendToCsp(root);

        // 清空本次会话内容
        saasService.triageNewSession(sessionMap.get(maapMessage.getUserPhone()));
        // 清除本次会话科室
        departmentMap.remove(maapMessage.getUserPhone());
        // 请除当前动作值
        mTalkHistoryService.removeTalk(maapMessage.getUserPhone());
    }


    private void sendChat(MaapMessage maapMessage) {

        try {
            // 从39获取返回值
            Map params = new HashMap();
            params.put("text", maapMessage.getBodyText());
            JsonNode jsonObject = saasService.triageChat(params, sessionMap.get(maapMessage.getUserPhone()));
            resolveTriage(jsonObject, maapMessage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 点击select时
    private void sendSelect(MaapMessage message) {
        try {
            Map params = new HashMap();
            params.put("option", message.getPostBackBodyText());
            JsonNode jsonObject = saasService.triageSelect(params, sessionMap.get(message.getUserPhone()));
            resolveTriage(jsonObject, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 继续问诊
    private void sendContinue(MaapMessage maapMessage) {
        try {
            JsonNode jsonObject = saasService.triageContinue(sessionMap.get(maapMessage.getUserPhone()));
            resolveTriage(jsonObject, maapMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JsonNode defaultStyle() {
        ObjectNode layout = sObjectMapper.createObjectNode();
        layout.put("width", "MEDIUM_WIDTH");
        layout.put("orientation", "VERTICAL");
        return layout;
    }
}
