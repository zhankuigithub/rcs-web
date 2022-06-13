package com.longmaster.guixjks.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface IHealthInformationService {

    JsonNode getArticles(int pageIndex, int pageSize);

    JsonNode getVideos(int pageIndex, int pageSize);

    JsonNode getRecommendsNew(String uuid, int newsTake, int topicTake, int videoTake);
}
