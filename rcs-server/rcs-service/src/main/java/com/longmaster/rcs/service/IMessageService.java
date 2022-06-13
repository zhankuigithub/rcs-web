package com.longmaster.rcs.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface IMessageService {

    String send(JsonNode request);

    String sendGroup(JsonNode request);
}
