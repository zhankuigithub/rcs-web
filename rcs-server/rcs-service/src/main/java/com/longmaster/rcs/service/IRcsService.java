package com.longmaster.rcs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.bean.maap.MaapMessage;

public interface IRcsService {

    /**
     * 中国移动、广西移动
     */
    MaapMessage parseCm(String xml);

    /**
     * 电信、朗玛
     *
     * @param json
     */
    MaapMessage parseCt(String json) throws JsonProcessingException;


    /**
     * langma
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    MaapMessage parseLm(String json) throws JsonProcessingException;

}
