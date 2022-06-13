package com.longmaster.guixjks.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IWebApiService {

    // 搜索医生
    JsonNode doctorList(String keyword, int currPage) throws JsonProcessingException;

    // 挂号订单列表
    JsonNode orderList(String phone, Long timeStamp) throws JsonProcessingException;

    // 图文问诊记录列表
    JsonNode recordList(String phone, int currPage) throws JsonProcessingException;

    // 获取userId
    int getUserId(String phone);

    // 获取家庭成员
    JsonNode patientList(String phone) throws JsonProcessingException;

    // 问医券
    JsonNode voucherList(String phone, int index, int size) throws JsonProcessingException;
}
