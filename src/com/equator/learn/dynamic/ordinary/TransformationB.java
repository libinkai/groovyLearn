package com.equator.learn.dynamic.ordinary;

import com.equator.learn.dynamic.base.MessageQueue;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class TransformationB implements Runnable {
    private Jedis jedis;

    public TransformationB() {
        this.jedis = MessageQueue.getMQ().getClient();
    }

    private void start() {
        log.info("转换B启动，有点慢...");
        try {
            Thread.sleep(1000 * 15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("转换B终于启动了！");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            transform();
        }
    }

    private String getSourceData(String topic) {
        String data = jedis.rpop(topic);
        log.info("TransformA getSourceData --- topic: {}, data: {}", topic, data);
        return data;
    }

    private void setTargetData(String topic, String data) {
        log.info("TransformA setTargetData --- topic: {}, data: {}", topic, data);
        jedis.lpush(topic, data);
    }

    private void transform() {
        String source = "Topic2";
        String sourceDataStr = getSourceData(source);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(sourceDataStr)) {
            com.google.gson.JsonObject logData = com.equator.learn.dynamic.base.GsonUtils.parseString(sourceDataStr).getAsJsonObject();
            log.info("data: {}", logData);
            if ((logData.get("logTime").getAsInt() & 1) == 0) {
                logData.addProperty("value", new java.lang.Integer(logData.get("value").getAsInt() + 666));
                setTargetData("Topic4", logData.toString());
            }
            if ((logData.get("logTime").getAsInt() & 1) == 1) {
                setTargetData("Topic3", logData.toString());
            }
        }
    }

    @Override
    public void run() {
        start();
    }

    public static void main(String[] args) {
        new Thread(new TransformationB()).start();
    }
}
