package com.longmaster.platform.service;

import com.longmaster.platform.entity.SceneNode;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * author zk
 * date 2021/4/27 18:18
 * description 场景节点处理类
 */
public interface SceneNodeService extends IService<SceneNode> {

    /**
     * 在场景中获取场景下需要推送的消息模板
     *
     * @param payload（当为建议回复时为id号 当为普通消息时为displayText）
     */
    Long getMessageTemplateInScene(Long appId, Long carrierId, String phone, String payload, Long sceneId);


    /**
     * 通过按钮节点、或者答案节点获取下一次需要推送的消息模板id
     */
    Long getMsgTmpIdByPreviousNode(Long appId, Long carrierId,String phone, Long nodeId, Long sceneId);

    /**
     * 找场景得到消息模板id（1. 关键词入口匹配   2.消息模板入口匹配）
     */
    Long findSceneToGetMsgTmpId(Long appId, Long carrierId, String payload, String userPhone);


    /**
     * 进入、更新场景和节点
     */
    boolean intoScene(Long appId, Long carrierId, String phone, Long sceneId, Long nodeId);

    /**
     * 退出场景
     */
    boolean exitScene(String phone);


    /**
     * 获取当前处于哪个场景
     */
    Long getCurrentSceneId(String phone);

    /**
     * 获取当前处于哪个节点
     */
    Long getCurrentNodeId(String phone);

    /**
     * 当节点为子场景时，获取消息模板id
     */
    Long resolveSonNode(Long appId, Long carrierId, String phone, SceneNode sceneNode);

    /**
     * 当节点为消息模板时，获取消息模板id
     */
    Long resolveMsgTmpNode(Long appId, Long carrierId, String phone, SceneNode sceneNode);

    /**
     * 找到匹配节点id
     */
    Long matchNode(Long nodeId, String payload);

}
