package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longmaster.platform.entity.SceneNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * author zk
 * date 2021/4/28 9:00
 */
public interface SceneNodeMapper extends BaseMapper<SceneNode> {

    int getSceneStatusById(@Param("id") Long id);


    // 获取最新的一条关键词场景
    SceneNode getLatestSceneByKeyWord(@Param("payload") String payload, @Param("sceneId") Long sceneId);


    // 获取子节点
    List<SceneNode> getSonNodes(@Param("pid") Long pid);


    // 获取最新的第一个子节点
    SceneNode getLatestSonNode(@Param("pid") Long pid, @Param("sceneId") Long sceneId);


    // 是否结束
    int countSonNodes(@Param("id") Long id);


    SceneNode getLatestSceneByPayLoad(@Param("payload") String payload, @Param("appId") Long appId);


    SceneNode getNodeById(@Param("id") Long id);


    SceneNode getLatestNodeBySceneId(@Param("sceneId") Long sceneId);
}
