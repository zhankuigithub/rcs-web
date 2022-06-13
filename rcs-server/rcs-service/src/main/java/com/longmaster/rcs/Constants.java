package com.longmaster.rcs;

public interface Constants {

    interface Maap {
        String TYPE_REPLY = "application/vnd.gsma.botsuggestion.response.v1.0+json";//基于建议回复消息的回复
        String TYPE_SHARED = "application/vnd.gsma.botsharedclientdata.v1.0+json";//终端共享配置数据
        String TYPE_FILE = "application/vnd.gsma.rcs-ft-http+xml";//上传文件，图片等信息

        String SERVICE_CAPABILITY = "serviceCapability";
        String CAPABILITY_ID = "capabilityId";
        String VERSION = "version";
        String BODY_TEXT = "bodyText";
        String DESTINATION_ADDRESS = "destinationAddress";
        String SENDER_ADDRESS = "senderAddress";
        String ORIG_USER = "origUser";
        String RESOURCE_URL = "resourceURL";
        String CONTENT_TYPE = "contentType";
        String MESSAGE_ID = "messageId";
        String CONVERSATION_ID = "conversationID";
        String CONTRIBUTION_ID = "contributionID";
        String DATE_TIME = "dateTime";
        String CONTENT_ENCODING = "contentEncoding";
    }

}
