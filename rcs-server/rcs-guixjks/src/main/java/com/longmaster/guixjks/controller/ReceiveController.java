package com.longmaster.guixjks.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.core.util.TextUtil;
import com.longmaster.core.vo.Result;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.handler.SceneHandler;
import com.longmaster.guixjks.service.TalkHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

@RequestMapping("receive")
@RestController
@Api(value = "ReceiveController", tags = "消息接收")
public class ReceiveController {

    @Resource
    private SceneHandler sceneHandler;
    @Resource
    private TalkHistoryService talkHistoryService;

    private static ObjectMapper sObjectMapper = new ObjectMapper();

    @PostMapping("resolve")
    @ApiOperation(value = "场景处理", notes = "场景处理")
    public Result resolve(@RequestBody MaapMessage maapMessage) throws IllegalAccessException, InvocationTargetException, JsonProcessingException {
        if (parseScene(maapMessage)) {  //不存在的场景
            sceneHandler.start(maapMessage);
        }
        return Result.SUCCESS();
    }

    private boolean parseScene(MaapMessage maapMessage) throws JsonProcessingException {

        //取出老的场景值
        String preScene = talkHistoryService.queryTalkHistory(maapMessage.getUserPhone());
        if (!TextUtil.isEmpty(preScene)) {
            maapMessage.setPreScene(preScene);
        } else {
            maapMessage.setPreScene("");
        }

        //更新新的场景值
        if (maapMessage.getContentType().trim().equals(Constants.Maap.TYPE_REPLY)) {

            JsonNode jsonNode = sObjectMapper.readTree(maapMessage.getBodyText());

            String action = "";
            String postDisplayText = "";

            if (jsonNode.get(Constants.Maap.PostBack.RESPONSE).has(Constants.Maap.PostBack.REPLY)) { //reply类型的postback

                action = sObjectMapper.readTree(
                        jsonNode.get(Constants.Maap.PostBack.RESPONSE)
                                .get(Constants.Maap.PostBack.REPLY)
                                .get(Constants.Maap.PostBack.POST_BACK)
                                .get(Constants.Maap.PostBack.DATA).asText()).get("action").asText();

                postDisplayText = jsonNode.get(Constants.Maap.PostBack.RESPONSE).get(Constants.Maap.PostBack.REPLY).get(Constants.Maap.PostBack.POST_BACK_TEXT).asText();


                // 智能导诊
                if (action.contains(Constants.ToSeeDoctor.EN_SEE_DOCTOR)) {
                    maapMessage.setScene(action);
                    maapMessage.setPostBackBodyText(postDisplayText);
                    maapMessage.setBelongPostBack(true);
                }

            } else if (jsonNode.get(Constants.Maap.PostBack.RESPONSE).has(Constants.Maap.PostBack.ACTION)) { //action类型的postback

                action = sObjectMapper.readTree(
                        jsonNode.get(Constants.Maap.PostBack.RESPONSE)
                                .get(Constants.Maap.PostBack.ACTION).
                                get(Constants.Maap.PostBack.POST_BACK)
                                .get(Constants.Maap.PostBack.DATA).asText()).get("action").asText();

                postDisplayText = jsonNode.get(Constants.Maap.PostBack.RESPONSE).get(Constants.Maap.PostBack.REPLY).get(Constants.Maap.PostBack.POST_BACK_TEXT).asText();
            } else {
                return false;
            }

            switch (action) {
                case Constants.Scene.NOT_DEVELOP:
                case Constants.Scene.NO_RESPONSE:
                    return false;
                default:
                    talkHistoryService.addTalk(maapMessage.getUserPhone(), action);
                    maapMessage.setScene(action);
                    maapMessage.setPostBackBodyText(postDisplayText);
                    maapMessage.setBelongPostBack(true);
                    break;
            }

        } else {
            maapMessage.setBelongPostBack(false);
            String oldScene = talkHistoryService.queryTalkHistory(maapMessage.getUserPhone());
            if (!TextUtil.isEmpty(oldScene) && oldScene.contains(Constants.ToSeeDoctor.EN_SEE_DOCTOR)) {
                maapMessage.setScene(oldScene);
            } else {
                // 匹配
                boolean b = Pattern.compile("医生$").matcher(maapMessage.getBodyText()).find();
                if (b) {
                    maapMessage.setScene(Constants.Scene.SBC_SEARCH_DOCTOR);
                } else {
                    maapMessage.setScene(Constants.Scene.EN_WELCOME);//默认使用欢迎场景
                }
            }
        }
        return true;
    }

}
