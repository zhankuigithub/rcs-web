package org.iflytek.msp.lfasr.process;



import com.alibaba.fastjson.JSON;
import org.iflytek.msp.lfasr.connect.Connector;
import org.iflytek.msp.lfasr.exception.LfasrException;
import org.iflytek.msp.lfasr.model.Message;
import org.iflytek.msp.lfasr.process.task.Task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Processor{

    private ExecutorService executor;
    private Connector connector;
    private int thresholdTimeout;

    private Processor() {
        this.thresholdTimeout = 60000;
    }

    public static Processor build(int coreThreads, int maxThreads, int maxConnections, int connTimeout, int soTimeout, String proxyUrl) {
        Processor processor = ProcessorBuilder.processor;
        processor.executor = new ThreadPoolExecutor(coreThreads, maxThreads, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque());
        processor.connector = Connector.build(maxConnections, connTimeout, soTimeout, proxyUrl);
        processor.thresholdTimeout = connTimeout + soTimeout + 10000;
        return processor;
    }

    public Message exec(Task task) {
        task.setConnector(this.connector);
        Future<Message> future = this.executor.submit(task);
        return this.getFutureAndRetry(future, task);
    }

    public Future<Message> submit(Task task) {
        task.setConnector(this.connector);
        return this.executor.submit(task);
    }

    public void exec(List<Task> taskList) {
        Map<Task, Future<Message>> futures = new HashMap(taskList.size());
        Iterator var3 = taskList.iterator();

        while(var3.hasNext()) {
            Task task = (Task)var3.next();
            task.setConnector(this.connector);
            Future<Message> future = this.executor.submit(task);
            futures.put(task, future);
        }

        var3 = futures.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<Task, Future<Message>> entry = (Map.Entry)var3.next();
            this.getFutureAndRetry((Future)entry.getValue(), (Task)entry.getKey());
        }

    }

    private Message getFutureAndRetry(Future<Message> future, Task task) {
        Message message;
        try {
            message = (Message)future.get((long)this.thresholdTimeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException var5) {
            message = (Message) JSON.parseObject("{\"ok\":\"-1\", \"errNo\":\"26000\", \"failed\":\"转写sdk通用错误!\", \"data\":\"\"}", Message.class);
            message = this.retry(task, message);
        } catch (TimeoutException var6) {
            message = (Message)JSON.parseObject("{\"ok\":\"-1\", \"errNo\":\"26640\", \"failed\":\"连接超时!请检查您的网络\", \"data\":\"\"}", Message.class);
            message = this.retry(task, message);
        } catch (InterruptedException var7) {
            message = (Message)JSON.parseObject("{\"ok\":\"-1\", \"errNo\":\"26000\", \"failed\":\"转写sdk通用错误!\", \"data\":\"\"}", Message.class);
            message = this.retry(task, message);
            Thread.currentThread().interrupt();
        }

        if (message.getOk() == 1) {
            message = this.retry(task, message);
        }

        return message;
    }

    private Message retry(Task task, Message message) {
        int retryCnt = task.getRetryCnt();
        ++retryCnt;
        if (retryCnt > 3) {
            message.setOk(-1);
            this.connector.release();
            throw new LfasrException(message.toString());
        } else {
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException var5) {
                Thread.currentThread().interrupt();
            }

            task.setRetryCnt(retryCnt);
            return this.exec(task);
        }
    }

    private static class ProcessorBuilder {
        private static Processor processor = new Processor();

        private ProcessorBuilder() {
        }
    }

}
