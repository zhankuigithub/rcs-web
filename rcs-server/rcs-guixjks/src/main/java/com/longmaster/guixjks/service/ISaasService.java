package com.longmaster.guixjks.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface ISaasService {

    JsonNode triageGetSession(Map params);

    JsonNode triageChat(Map params, String session);

    JsonNode triageSelect(Map params, String session);

    JsonNode triageContinue(String session);

    JsonNode triageNewSession(String session);

    JsonNode recommend(Map params);

    JsonNode access(Map params);

}
