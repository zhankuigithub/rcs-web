package com.longmaster.guixjks.service;

public interface TalkHistoryService {

    String queryTalkHistory(String phone);

    Boolean addTalk(String phone, String sentence);

    Boolean removeTalk(String phone);
}
