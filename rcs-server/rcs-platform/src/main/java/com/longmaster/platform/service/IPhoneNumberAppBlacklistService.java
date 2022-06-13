package com.longmaster.platform.service;

import com.longmaster.platform.entity.PhoneNumberAppBlacklist;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IPhoneNumberAppBlacklistService extends IService<PhoneNumberAppBlacklist> {

    void reload();

    boolean isContainsBlackList(String chatBotId, String phone);

}
