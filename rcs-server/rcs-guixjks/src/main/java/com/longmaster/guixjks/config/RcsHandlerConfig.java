package com.longmaster.guixjks.config;


import com.longmaster.core.handler.BaseRcsHandlerConfig;


public class RcsHandlerConfig extends BaseRcsHandlerConfig {

    private static volatile RcsHandlerConfig instance;

    @Override
    protected String getPkg() {
        return "com.longmaster.guixjks.handler";
    }

    public static RcsHandlerConfig getInstance() {
        if (instance == null) {
            synchronized (RcsHandlerConfig.class) {
                if (instance == null) {
                    instance = new RcsHandlerConfig();
                }
            }
        }
        return instance;
    }
}
