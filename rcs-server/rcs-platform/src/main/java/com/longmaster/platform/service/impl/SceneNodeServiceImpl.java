package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.platform.cache.CacheHelper;
import com.longmaster.platform.cache.GlobalCacheKeyDefine;
import com.longmaster.platform.entity.LogSceneMessage;
import com.longmaster.platform.entity.SceneInfo;
import com.longmaster.platform.entity.SceneNode;
import com.longmaster.platform.mapper.SceneNodeMapper;
import com.longmaster.platform.service.LogSceneMessageService;
import com.longmaster.platform.service.RedisService;
import com.longmaster.platform.service.SceneInfoService;
import com.longmaster.platform.service.SceneNodeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author zk
 * date 2021/4/27 18:18
 */
@Component
public class SceneNodeServiceImpl extends ServiceImpl<SceneNodeMapper, SceneNode> implements SceneNodeService {

    @Resource
    private RedisService redisService;

    @Resource
    private LogSceneMessageService logSceneMessageService;

    @Resource
    private SceneInfoService sceneInfoService;

    // 1 消息模板 2 建议回复 3场景 4关键词 5逻辑判断 6答案
    private static final int TYPE_MSG_TMP = 1;
    private static final int TYPE_SUG = 2;
    private static final int TYPE_SCENE = 3;
    private static final int TYPE_KEYWORD = 4;
    private static final int TYPE_JUDGE = 5;
    private static final int TYPE_ANSWER = 6;

    @Override
    public Long getMessageTemplateInScene(Long appId, Long carrierId, String phone, String payload, Long sceneId) {

        int status = baseMapper.getSceneStatusById(sceneId);
        if (status == 1) {
            exitScene(phone);
            return null;
        }

        SceneNode scene = baseMapper.getLatestSceneByKeyWord(payload, sceneId);

        if (scene != null) { // 重复进入当前场景
            return getMsgTmpIdByPreviousNode(appId, carrierId, phone, scene.getId(), scene.getSceneId());
        }

        Long currentNodeId = this.getCurrentNodeId(phone);

        if (currentNodeId == null) {
            return null;
        }

        Long sonId = this.matchNode(currentNodeId, payload);

        if (sonId == null) {
            return null;
        }

        return getMsgTmpIdByPreviousNode(appId, carrierId, phone, sonId, sceneId);
    }

    @Override
    public Long getMsgTmpIdByPreviousNode(Long appId, Long carrierId, String phone, Long nodeId, Long sceneId) {
        SceneNode sonNode = baseMapper.getLatestSonNode(nodeId, sceneId);
        if (sonNode == null) {
            return null;
        }
        Integer type = sonNode.getType();
        if (type == TYPE_MSG_TMP) { // 消息模板
            return resolveMsgTmpNode(appId, carrierId, phone, sonNode);
        }

        if (type == TYPE_SCENE) {
            return resolveSonNode(appId, carrierId, phone, sonNode);
        }
        return null;
    }

    @Override
    public Long findSceneToGetMsgTmpId(Long appId, Long carrierId, String payload, String userPhone) {
        SceneNode node = baseMapper.getLatestSceneByPayLoad(payload, appId);
        if (node == null) {
            return null;
        }
        int status = baseMapper.getSceneStatusById(node.getSceneId());

        if (status == 1) {
            exitScene(userPhone);
            return null;
        }

        Integer type = node.getType();
        switch (type) {
            case TYPE_SUG:
                SceneNode fatherNode = baseMapper.getNodeById(node.getPId());
                if (fatherNode != null) {
                    if (fatherNode.getPId() == 0) {
                        if (fatherNode.getType() == TYPE_MSG_TMP) {   // 当父节点为根节点并且为消息模板时
                            return getMsgTmpIdByPreviousNode(appId, carrierId, userPhone, node.getId(), node.getSceneId());
                        }
                    }
                }
                return null;

            case TYPE_KEYWORD:
                return getMsgTmpIdByPreviousNode(appId, carrierId, userPhone, node.getId(), node.getSceneId());
            default:
                return null;
        }
    }

    @Override
    public boolean intoScene(Long appId, Long carrierId, String phone, Long sceneId, Long nodeId) {

        String sceneKey = CacheHelper.buildSceneCacheKey(GlobalCacheKeyDefine.SCENE_PHONE, phone);
        if (!redisService.isExist(sceneKey)) {
            SceneInfo sceneInfo = sceneInfoService.getOne(new LambdaQueryWrapper<SceneInfo>().eq(SceneInfo::getId, sceneId).select(SceneInfo::getName));
            LogSceneMessage logSceneMessage = new LogSceneMessage();
            logSceneMessage.setAppId(appId);
            logSceneMessage.setCarrierId(carrierId);
            logSceneMessage.setSceneId(sceneId);
            logSceneMessage.setSceneName(sceneInfo.getName());
            logSceneMessage.setPhoneNum(phone);
            logSceneMessageService.save(logSceneMessage);
        }

        redisService.set(CacheHelper.buildSceneCacheKey(GlobalCacheKeyDefine.NODE_PHONE, phone), nodeId, 3600L);
        redisService.set(sceneKey, sceneId, 3600L);
        return true;
    }

    @Override
    public boolean exitScene(String phone) {
        redisService.delete(CacheHelper.buildSceneCacheKey(GlobalCacheKeyDefine.SCENE_PHONE, phone));
        redisService.delete(CacheHelper.buildSceneCacheKey(GlobalCacheKeyDefine.NODE_PHONE, phone));
        return true;
    }

    @Override
    public Long getCurrentSceneId(String phone) {
        return redisService.get(CacheHelper.buildSceneCacheKey(GlobalCacheKeyDefine.SCENE_PHONE, phone));
    }

    @Override
    public Long getCurrentNodeId(String phone) {
        return redisService.get(CacheHelper.buildSceneCacheKey(GlobalCacheKeyDefine.NODE_PHONE, phone));
    }

    @Override
    public Long resolveSonNode(Long appId, Long carrierId, String phone, SceneNode sceneNode) {
        if (sceneNode.getType() != TYPE_SCENE) {
            return null;
        }
        Long payload = Long.parseLong(sceneNode.getPayload()); // 此时，payload为场景id
        SceneNode byScene = baseMapper.getLatestNodeBySceneId(payload); // 获取场景下的第一个节点

        if (byScene.getType() == TYPE_MSG_TMP) {
            return resolveMsgTmpNode(appId, carrierId, phone, byScene);
        }
        return null;
    }

    @Override
    public Long resolveMsgTmpNode(Long appId, Long carrierId, String phone, SceneNode sceneNode) {
        if (sceneNode.getType() != TYPE_MSG_TMP) {
            return null;
        }
        // 先更新当前节点，再判断无子节点的时，退出
        intoScene(appId, carrierId, phone, sceneNode.getSceneId(), sceneNode.getId());
        if (baseMapper.countSonNodes(sceneNode.getId()) == 0) {
            exitScene(phone);
        }
        return Long.parseLong(sceneNode.getPayload());
    }


    @Override
    public Long matchNode(Long nodeId, String payload) {

        List<SceneNode> sonNodes = baseMapper.getSonNodes(nodeId);
        if (sonNodes == null || sonNodes.size() == 0) {
            return null;
        }

        // 获取所有按钮型节点
        List<SceneNode> suggestions = sonNodes.stream().filter(a -> a.getType() == TYPE_SUG).collect(Collectors.toList());
        if (suggestions.size() > 0) {
            for (SceneNode item : sonNodes) {
                if (payload.equals(item.getPayload())) {
                    return item.getId();
                }
            }
        }

        // 获取所有判断型节点
        List<SceneNode> judges = sonNodes.stream().filter(a -> a.getType() == TYPE_JUDGE).collect(Collectors.toList());
        if (judges.size() > 0) {
            for (SceneNode judge : judges) {
                List<SceneNode> answerNodes = baseMapper.getSonNodes(judge.getId()); // 获取答案节点
                if (answerNodes.size() > 0) {
                    for (SceneNode answerNode : answerNodes) {
                        if (payload.equals(answerNode.getPayload()) && answerNode.getType() == TYPE_ANSWER) { // 判断节点下只能存在答案节点
                            return answerNode.getId();
                        }
                    }
                }
            }
        }
        return null;
    }

}
