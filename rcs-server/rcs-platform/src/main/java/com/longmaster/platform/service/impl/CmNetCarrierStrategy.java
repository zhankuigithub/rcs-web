package com.longmaster.platform.service.impl;

import com.longmaster.platform.dto.chatbot.ChatBotWrapperDTO;
import com.longmaster.platform.enums.UploadType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Validation;
import javax.validation.Validator;

@Slf4j
@Service
public class CmNetCarrierStrategy extends AbstractCarrierAdapter {

    private static final Long CM_CARRIER_ID = 6L;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public Long getCarrier() {
        return CM_CARRIER_ID;
    }

    @Override
    public Object createChatBot(ChatBotWrapperDTO wrapper) {
        wrapper.getChatbot().setAuditStatus(1); // 无需审核
        super.updateChatBot(wrapper);
        return true;
    }

    @Override
    public Object uploadCustomerFile(String fileUrl, UploadType uploadType, Object... args) {
        return null;
    }

    @Override
    public String uploadChatBotFile(String fileUrl) {
        return null;
    }
}
