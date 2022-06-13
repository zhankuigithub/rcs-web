package org.iflytek.msp.lfasr.process.task;



import org.iflytek.msp.lfasr.exception.LfasrException;
import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.model.Signature;

import java.util.Map;

public class PrepareTask extends AbstractTask {
    private String fileName;

    public PrepareTask(Signature signature, Map<String, String> param) {
        super(signature);
        this.param.putAll(param);
        this.fileName = (String) param.get("file_name");
    }

    public Message call() {
        Message message = new Message();

        try {
            message = this.resolveMessage(this.connector.post("https://raasr.xfyun.cn/api/prepare", this.param));
            if (message.getOk() == -1) {
                message.setOk(1);
            }
        } catch (LfasrException var3) {
            message.setOk(1);
        }

        return message;
    }

    public String getIntro() {
        return "prepare task: " + this.fileName;
    }
}
