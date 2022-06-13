package org.iflytek.msp.lfasr.process.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.iflytek.msp.lfasr.connect.Connector;
import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.model.Signature;

import java.util.HashMap;
import java.util.Map;

public class AbstractTask implements Task {
    protected int retryCnt = 0;
    protected Map<String, String> param = new HashMap();
    protected Connector connector;

    protected AbstractTask(Signature signature) {
        this.param.put("app_id", signature.getAppId());
        this.param.put("signa", signature.getSigna());
        this.param.put("ts", signature.getTs());
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public Connector getConnector() {
        return this.connector;
    }

    public int getRetryCnt() {
        return this.retryCnt;
    }

    public void setRetryCnt(int retryCnt) {
        this.retryCnt = retryCnt;
    }

    @Override
    public String getIntro() {
        return null;
    }

    protected Message resolveMessage(String res) {
        JSONObject jsonObject = JSON.parseObject(res);
        Message message = new Message();
        message.setOk(jsonObject.getInteger("ok"));
        message.setErrNo(jsonObject.getInteger("err_no"));
        message.setFailed(jsonObject.getString("failed"));
        message.setData(jsonObject.getString("data"));
        return message;
    }

    @Override
    public Message call() throws Exception {
        return null;
    }
}
