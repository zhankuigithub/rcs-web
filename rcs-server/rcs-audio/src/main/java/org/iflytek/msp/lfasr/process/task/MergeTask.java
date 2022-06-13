package org.iflytek.msp.lfasr.process.task;


import org.iflytek.msp.lfasr.exception.LfasrException;
import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.model.Signature;

public class MergeTask extends AbstractTask {

    private String taskId;

    public MergeTask(Signature signature, String taskId) {
        super(signature);
        this.param.put("task_id", taskId);
        this.taskId = taskId;
    }

    public Message call() {
        Message message = new Message();

        try {
            message = this.resolveMessage(this.connector.post("https://raasr.xfyun.cn/api/merge", this.param));
        } catch (LfasrException var3) {
            message.setOk(1);
        }

        return message;
    }

    public String getIntro() {
        return "merge task: " + this.taskId;
    }
}
