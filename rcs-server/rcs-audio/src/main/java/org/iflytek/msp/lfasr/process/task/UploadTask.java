package org.iflytek.msp.lfasr.process.task;


import org.iflytek.msp.lfasr.exception.LfasrException;
import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.model.Signature;

public class UploadTask extends AbstractTask {
    private byte[] slice;
    private String taskId;
    private String sliceId;

    public UploadTask(Signature signature, String taskId, String sliceId, byte[] slice) {
        super(signature);
        this.param.put("task_id", taskId);
        this.param.put("slice_id", sliceId);
        this.slice = slice;
        this.taskId = taskId;
        this.sliceId = sliceId;
    }

    public Message call() {
        Message message = new Message();

        try {
            message = this.resolveMessage(this.connector.post("https://raasr.xfyun.cn/api/upload", this.param, this.slice));
        } catch (LfasrException var3) {
            message.setOk(1);
        }

        return message;
    }

    public String getIntro() {
        return "upload task: " + this.taskId + ", sliceId: " + this.sliceId;
    }
}
