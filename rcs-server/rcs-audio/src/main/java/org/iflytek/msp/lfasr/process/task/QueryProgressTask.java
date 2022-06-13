package org.iflytek.msp.lfasr.process.task;


import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.model.Signature;

public class QueryProgressTask extends AbstractTask {
    private String taskId;

    public QueryProgressTask(Signature signature, String taskId) {
        super(signature);
        this.param.put("task_id", taskId);
        this.taskId = taskId;
    }

    public Message call() {
        return this.resolveMessage(this.connector.post("https://raasr.xfyun.cn/api/getProgress", this.param));
    }

    public String getIntro() {
        return "query progress task: " + this.taskId;
    }
}
