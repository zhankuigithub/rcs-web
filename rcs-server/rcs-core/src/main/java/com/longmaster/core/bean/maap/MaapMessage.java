package com.longmaster.core.bean.maap;

import lombok.Data;

import java.util.List;


/**
 * 用于rcs发往业务服务器之间的实体类
 */
@Data
public class MaapMessage {

    // XML带来的
    private String destinationAddress; // chatbotid

    private String senderAddress;

    private String origUser;

    private String resourceURL;

    private String link;

    private String messageId;

    private String contentType;

    private String dateTime;

    private MaapCapability maapCapability;

    private String bodyText;

    private String conversationId;

    private String contributionId;

    private String userPhone;

    private List<MaapFile> maapFile;// 文件对象（电信会存在2个，移动只有1个）

    // 自定义的
    private String scene;// 用户场景值

    private String preScene;// 上一个场景值

    private Boolean belongPostBack;// 是否为点击菜单消息

    private String postBackBodyText;// 菜单中文名称

    private Boolean messageBack = false;

}
