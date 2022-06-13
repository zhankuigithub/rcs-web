package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.platform.entity.Chatbot;
import com.longmaster.platform.entity.PhoneNumberAppBlacklist;
import com.longmaster.platform.entity.PhoneNumberBook;
import com.longmaster.platform.mapper.PhoneNumberAppBlacklistMapper;
import com.longmaster.platform.service.ChatbotService;
import com.longmaster.platform.service.IPhoneNumberAppBlacklistService;
import com.longmaster.platform.service.PhoneNumberBookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PhoneNumberAppBlacklistServiceImpl extends ServiceImpl<PhoneNumberAppBlacklistMapper, PhoneNumberAppBlacklist> implements IPhoneNumberAppBlacklistService {

    @Resource
    private IPhoneNumberAppBlacklistService phoneNumberAppBlacklistService;

    @Resource
    private PhoneNumberBookService phoneNumberBookService;

    @Resource
    private ChatbotService chatbotService;

    private static Map<String, List<String>> sMap; // chatbotid->[136****4564, 187****2321]

    @Override
    public void reload() {
        synchronized (this) {
            List<PhoneNumberAppBlacklist> numberAppBlacklists = phoneNumberAppBlacklistService.list();

            if (numberAppBlacklists != null && !numberAppBlacklists.isEmpty()) {

                Map<Long, List<String>> map = new HashMap();

                for (PhoneNumberAppBlacklist number : numberAppBlacklists) {
                    Long applicationId = number.getApplicationId();
                    Long phoneId = number.getPhoneId();
                    PhoneNumberBook book = phoneNumberBookService.getById(phoneId);
                    String phoneNum = book.getPhoneNum();
                    List<String> list = map.computeIfAbsent(applicationId, k -> new ArrayList<>());

                    list.add(phoneNum);
                }

                if (!map.isEmpty()) {
                    Map<String, List<String>> hashMap = new HashMap<>();

                    for (Map.Entry<Long, List<String>> entry : map.entrySet()) {
                        Long appId = entry.getKey();
                        List<String> phones = entry.getValue();
                        List<Chatbot> list = chatbotService.list(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAppId, appId).select(Chatbot::getChatBotId));

                        for (Chatbot chatbot : list) {
                            hashMap.put(chatbot.getChatBotId(), phones);
                        }
                    }
                    sMap = hashMap;
                }
            } else {
                sMap = new HashMap<>();
            }
        }
    }

    @Override
    public boolean isContainsBlackList(String chatBotId, String phone) {
        if (sMap == null) {
            reload();
        }

        List<String> list = sMap.get(chatBotId);
        if (list != null) {
            return list.contains(phone);
        }
        return false;
    }


}
