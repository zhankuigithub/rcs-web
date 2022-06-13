package org.iflytek.msp.lfasr.process.task;


import org.iflytek.msp.lfasr.connect.Connector;
import org.iflytek.msp.lfasr.model.Message;

import java.util.concurrent.Callable;

public interface Task extends Callable<Message> {
    String BASE_URL = "https://raasr.xfyun.cn/api/";
    String PREPARE = "https://raasr.xfyun.cn/api/prepare";
    String UPLOAD = "https://raasr.xfyun.cn/api/upload";
    String MERGE = "https://raasr.xfyun.cn/api/merge";
    String RESULT = "https://raasr.xfyun.cn/api/getResult";
    String PROGRESS = "https://raasr.xfyun.cn/api/getProgress";
    int RETRY_LIMIT = 3;

    void setConnector(Connector var1);

    Connector getConnector();

    int getRetryCnt();

    void setRetryCnt(int var1);

    String getIntro();
}
