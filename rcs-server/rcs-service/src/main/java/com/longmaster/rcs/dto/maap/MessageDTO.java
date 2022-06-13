package com.longmaster.rcs.dto.maap;

import com.longmaster.core.util.CommonUtil;
import lombok.Data;

import java.util.List;

@Data
public class MessageDTO {

    private String messageId;

    private List<BodyTextDTO> messageList;

    private String dateTime;

    private String destinationAddress;

    private String senderAddress;

    private String conversationId;

    private String contributionId;

    /**
     * 接收时统一处理为11位手机号
     * sip:+8617346405044@sh.5GMC.ims.mnc011.mcc460.3gppnetwork.org
     * tel:+8617785100435
     *
     * @return
     */
    public String getSenderAddress() {
        return CommonUtil.filterPhone(senderAddress);
    }
}
