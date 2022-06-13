package com.longmaster.platform.enums;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.longmaster.platform.dto.csp.CallbackAuditDTO;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.CustomerAuditRecord;
import com.longmaster.platform.service.ChatbotService;
import com.longmaster.platform.service.CustomerAuditRecordService;
import com.longmaster.platform.service.impl.ChatbotServiceImpl;
import com.longmaster.platform.service.impl.CustomerAuditRecordServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.function.BiConsumer;

@Slf4j
public enum Ct5GAuditEnum {
    DEFAULT_ENUM(0, (context, data) -> {
    }),
    CUSTOMER_AUDIT(1, Ct5GAuditEnum::syncCustomerAuthStatus),
    CHAT_BOT_AUDIT(2, Ct5GAuditEnum::syncChatBotAuditStatus);

    //审核类型 1：客户，2：chatbot
    @Getter
    private int type;


    //操作
    @Getter
    private BiConsumer<ApplicationContext, Object> consumer;


    Ct5GAuditEnum(int type, BiConsumer<ApplicationContext, Object> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    /**
     * 策略模式
     *
     * @param type
     * @return
     */
    public static Ct5GAuditEnum findIn(int type) {
        for (Ct5GAuditEnum auditEnum : Ct5GAuditEnum.values()) {
            if (type == auditEnum.type) {
                return auditEnum;
            }
        }
        return DEFAULT_ENUM;
    }

    /**
     * 处理customer回调  更新客服审核状态、用户状态
     *
     * @param context
     * @param
     */
    private static void syncCustomerAuthStatus(ApplicationContext context, Object obj) {
        CallbackAuditDTO audit = (CallbackAuditDTO) obj;
        CustomerAuditRecordService auditRecordService = context.getBean(CustomerAuditRecordServiceImpl.class);
        Integer opType = audit.getOpType();

        if (opType == 3) {
            log.info("删除客户审核记录：{}", audit.getAuditNo());
            auditRecordService.remove(new LambdaQueryWrapper<CustomerAuditRecord>().eq(CustomerAuditRecord::getCspEcNo, audit.getAuditNo()));
        } else {
            log.info("更新客户审核状态：{}，状态：{}", audit.getAuditNo(), audit.getAuditStatus());
            auditRecordService.update(new LambdaUpdateWrapper<CustomerAuditRecord>()
                    .set(CustomerAuditRecord::getStatus, audit.getAuditStatus())
                    .set(CustomerAuditRecord::getReviewData, audit.getDescription())
                    .eq(CustomerAuditRecord::getCspEcNo, audit.getAuditNo())
            );
        }
    }

    /**
     * 处理chatbot 审核回调  更新chatbot 状态
     *
     * @param context
     * @param obj
     */
    private static void syncChatBotAuditStatus(ApplicationContext context, Object obj) {
        CallbackAuditDTO audit = (CallbackAuditDTO) obj;
        ChatbotService chatbotService = context.getBean(ChatbotServiceImpl.class);

        int auditStatus = 0; // -2未通过 -1审核中 0下线 1上线
        Integer opType = audit.getOpType();

        switch (opType) {
            case 1:
                auditStatus = (audit.getAuditStatus() == 1) ? 0 : -2; // 新增成功表示‘下线’状态
                break;
            case 2:
            case 4:
                auditStatus = (audit.getAuditStatus() == 1) ? 1 : -2; // 变更、上线成功直接取1，否则判定为未通过
                break;
            case 3:
                log.info("删除机器人：{}", audit.getAuditNo());
                chatbotService.remove(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAccessTagNo, audit.getAuditNo()));
                return;
        }
        log.info("变更机器人：{}，状态：{}", audit.getAuditNo(), auditStatus);
        chatbotService.update(new LambdaUpdateWrapper<Chatbot>()
                .set(Chatbot::getAuditStatus, auditStatus)
                .set(Chatbot::getReviewData, audit.getDescription())
                .eq(Chatbot::getAccessTagNo, audit.getAuditNo())
        );
    }
}
