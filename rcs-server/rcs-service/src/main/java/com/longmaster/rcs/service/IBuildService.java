package com.longmaster.rcs.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface IBuildService {

    String buildMessage(JsonNode message);

    String buildSingleCard(JsonNode message);

    String buildManyCard(JsonNode message);

    String buildFile(JsonNode message);

}
