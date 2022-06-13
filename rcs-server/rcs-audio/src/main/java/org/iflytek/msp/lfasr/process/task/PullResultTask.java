package org.iflytek.msp.lfasr.process.task;


import org.iflytek.msp.lfasr.exception.LfasrException;
import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.model.Signature;

public class PullResultTask extends AbstractTask {
    private String taskId;

    public PullResultTask(Signature signature, String taskId) {
        super(signature);
        this.param.put("task_id", taskId);
        this.taskId = taskId;
    }

    public Message call() {
        Message message = new Message();

        try {
            message = this.resolveMessage(this.connector.post("https://raasr.xfyun.cn/api/getResult", this.param));
        } catch (LfasrException var3) {
            message.setOk(1);
        }

        return message;
    }

    public String getIntro() {
        return "pull result task: " + this.taskId;
    }
}
