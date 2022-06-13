package com.longmaster.guixjks.handler.guixjks;

import com.longmaster.core.bean.annotation.RcsAction;
import com.longmaster.core.bean.annotation.RcsScene;
import com.longmaster.core.bean.maap.MaapMessage;
import com.longmaster.guixjks.Constants;
import com.longmaster.guixjks.service.ICspService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RcsScene
public class HomeScene {

    @Resource
    private ICspService mSendToCspService;

    @Value("${csp.messageTmp.guixjkId}")
    private Long guixjkId;

    @RcsAction(Constants.Scene.GUIXJK_RETURN_HOME)
    public void returnHomeOfGuiXjk(MaapMessage maapMessage) {
        mSendToCspService.sendMessageByMessageTmpId(maapMessage.getDestinationAddress(), maapMessage.getUserPhone(), maapMessage.getMessageBack(), guixjkId);
    }

}
