package com.longmaster.platform.service;


import com.longmaster.platform.dto.message.MaapMessage;

/**
 * author zk
 * date 2021/3/15 15:28
 * description 自定义服务service
 */
public interface CustomService {

    /***
     * @return
     */
    void sendToCustom(MaapMessage message);

}
