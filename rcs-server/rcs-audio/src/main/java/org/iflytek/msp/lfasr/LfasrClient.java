package org.iflytek.msp.lfasr;


import org.iflytek.msp.lfasr.exception.LfasrException;
import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.model.Signature;
import org.iflytek.msp.lfasr.process.Processor;
import org.iflytek.msp.lfasr.process.task.*;
import org.iflytek.msp.lfasr.util.SliceIdGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LfasrClient {

    private static final int FILE_UPLOAD_MAXSIZE = 524288000;
    private static final int SLICE_SIZE = 10485760;
    private Signature signature;
    private volatile Processor processor;

    private LfasrClient() {
    }

    public static LfasrClient getInstance(String appId, String secretKey) {
        return getInstance(appId, secretKey, (String) null);
    }

    public static LfasrClient getInstance(String appId, String secretKey, String proxyUrl) {
        return getInstance(appId, secretKey, 10, 50, proxyUrl);
    }

    public static LfasrClient getInstance(String appId, String secretKey, int coreThreads, int maxThreads, String proxyUrl) {
        return getInstance(appId, secretKey, coreThreads, maxThreads, 50, 10000, 30000, proxyUrl);
    }

    public static LfasrClient getInstance(String appId, String secretKey, int coreThreads, int maxThreads, int maxConnections, int connTimeout, int soTimeout, String proxyUrl) {
        LfasrClient instance = SingletonHolder.singleton;

        try {
            instance.signature = new Signature(appId, secretKey);
        } catch (SignatureException | NoSuchAlgorithmException var12) {
            throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26002\", \"failed\":\"转写参数客户端生成签名错误!\", \"data\":\"\"}");
        }

        if (instance.processor == null) {
            Class var9 = LfasrClient.class;
            synchronized (LfasrClient.class) {
                if (instance.processor == null) {
                    instance.processor = Processor.build(coreThreads, maxThreads, maxConnections, connTimeout, soTimeout, proxyUrl);
                }
            }
        } else {

        }

        return instance;
    }

    public Message upload(String audioFilePath) {
        return this.upload(audioFilePath, new HashMap());
    }

    public Message upload(String audioFilePath, Map<String, String> param) {
        if (audioFilePath == null) {
            throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26001\", \"failed\":\"转写参数上传文件不能为空或文件不存在!\", \"data\":\"\"}");
        } else {
            File audio = new File(audioFilePath);
            if (!audio.exists()) {
                throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26001\", \"failed\":\"转写参数上传文件不能为空或文件不存在!\", \"data\":\"\"}");
            } else {
                long length = audio.length();
                if (length > 524288000L) {
                    throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26003\", \"failed\":\"转写本地文件上传超过限定大小500M!\", \"data\":\"\"}");
                } else {
                    long sliceNum = length / 10485760L + (long) (length % 10485760L == 0L ? 0 : 1);
                    param.put("file_len", length + "");
                    param.put("file_name", audio.getName());
                    param.put("slice_num", sliceNum + "");
                    Message message = this.processor.exec(new PrepareTask(this.signature, param));
                    String taskId = message.getData();

                    try {
                        FileInputStream fis = new FileInputStream(audio);
                        Throwable var11 = null;

                        try {
                            byte[] slice = new byte[10485760];
                            SliceIdGenerator generator = new SliceIdGenerator();

                            int len;
                            while ((len = fis.read(slice)) > 0) {
                                byte[] data = Arrays.copyOfRange(slice, 0, len);
                                Task task = new UploadTask(this.signature, taskId, generator.getNextSliceId(), data);
                                this.processor.exec(task);
                            }
                        } catch (Throwable var25) {
                            var11 = var25;
                            throw var25;
                        } finally {
                            if (fis != null) {
                                if (var11 != null) {
                                    try {
                                        fis.close();
                                    } catch (Throwable var24) {
                                        var11.addSuppressed(var24);
                                    }
                                } else {
                                    fis.close();
                                }
                            }

                        }
                    } catch (IOException var27) {
                        throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26004\", \"failed\":\"转写上传文件读取错误!\", \"data\":\"\"}");
                    }

                    this.processor.exec(new MergeTask(this.signature, taskId));
                    return message;
                }
            }
        }
    }

    public Message upload(byte[] bytes, Map<String, String> param) {
        long length = bytes.length;
        if (length > 524288000L) {
            throw new LfasrException("{\"ok\":\"-1\", \"errNo\":\"26003\", \"failed\":\"转写本地文件上传超过限定大小500M!\", \"data\":\"\"}");
        } else {
            long sliceNum = length / 10485760L + (long) (length % 10485760L == 0L ? 0 : 1);
            param.put("file_len", length + "");
            param.put("file_name", System.currentTimeMillis() + ".wav");
            param.put("slice_num", sliceNum + "");
            Message message = this.processor.exec(new PrepareTask(this.signature, param));
            String taskId = message.getData();
            try {
                SliceIdGenerator generator = new SliceIdGenerator();
                Task task = new UploadTask(this.signature, taskId, generator.getNextSliceId(), bytes);
                this.processor.exec(task);
            } catch (Throwable var25) {
                throw var25;
            }
            this.processor.exec(new MergeTask(this.signature, taskId));
            return message;
        }
    }

    public Message getProgress(String taskId) {
        return this.processor.exec(new QueryProgressTask(this.signature, taskId));
    }

    public Message getResult(String taskId) {
        return this.processor.exec(new PullResultTask(this.signature, taskId));
    }

    private static class SingletonHolder {
        private static LfasrClient singleton = new LfasrClient();

        private SingletonHolder() {
        }
    }
}
