package com.longmaster.rcs.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.longmaster.rcs.entity.ChatBotInfo;
import com.longmaster.rcs.service.ICspService;
import com.longmaster.rcs.service.IMessageService;
import com.longmaster.rcs.service.channel.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageServiceImpl implements IMessageService {

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

    @Resource
    private IRcsMaapService mRCSMaapService;


    @Override
    public String send(JsonNode request) {
        String chatBotId = request.get("chatBotId").asText();

        if (request.has("isMessageBack") && request.get("isMessageBack").asBoolean()) {
            mRCSMaapService.sendMessage(request, chatBotId);
            return null;
        }

        ChatBotInfo chatBotInfo = cspService.getChatBotInfo(chatBotId);

        if (chatBotInfo == null) {
            return null;
        }
        int carrierId = Integer.parseInt(String.valueOf(chatBotInfo.getCarrierId()));

        switch (carrierId) {
            case 1:
                return mCMCCService.sendMessage(request, chatBotInfo.getAppId(), chatBotInfo.getAppKey());
            case 2:
                return mCUCCService.sendMessage(request);
            case 3:
                return mCTCCService.sendMessage(request);
            case 4:
                mRCSMaapService.sendMessage(request, chatBotId);
                return null;
            case 5:
                return mGXCMCCService.sendMessage(request, chatBotInfo.getAppId(), chatBotInfo.getAppKey());
        }
        return null;
    }

    @Override
    public String sendGroup(JsonNode request) {
        String chatBotId = request.get("chatBotId").asText();
        int carrierId = Integer.parseInt(String.valueOf(request.get("carrierId").asLong()));
        switch (carrierId) {
            case 1:
                ChatBotInfo chatBotInfo = cspService.getChatBotInfo(chatBotId);
                if (chatBotInfo == null) {
                    return null;
                }
                return mCMCCService.sendGroupMessage(request, chatBotInfo.getAppId(), chatBotInfo.getAppKey());
            case 2:
                return mCUCCService.sendGroupMessage(request);

            case 3:
                return mCTCCService.sendGroupMessage(request);
            case 4:
                return mRCSMaapService.sendGroupMessage(request, chatBotId);
            case 5:
                ChatBotInfo cbt = cspService.getChatBotInfo(chatBotId);
                if (cbt == null) {
                    return null;
                }
                return mGXCMCCService.sendGroupMessage(request, cbt.getAppId(), cbt.getAppKey());
        }
        return null;
    }

}
