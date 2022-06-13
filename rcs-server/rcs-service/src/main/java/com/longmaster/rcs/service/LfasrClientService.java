package com.longmaster.rcs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.longmaster.core.bean.maap.MaapMessage;

import java.util.Map;

public interface LfasrClientService {

    /**
     * @param path
     * @return
     * @throws InterruptedException
     */
    Map<String, Object> transformText(String path) throws InterruptedException, JsonProcessingException;


    Map<String, Object> transformText(byte[] bytes) throws JsonProcessingException, InterruptedException;


    Map<String, Object> transformText(MaapMessage message) throws JsonProcessingException, InterruptedException;


    void dictation(MaapMessage message, String destination);

}

