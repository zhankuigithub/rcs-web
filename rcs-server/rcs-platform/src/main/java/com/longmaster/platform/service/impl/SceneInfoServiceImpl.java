package com.longmaster.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.scene.SceneNodeModifyDTO;
import com.longmaster.platform.dto.scene.SceneQueryDTO;
import com.longmaster.platform.entity.SceneInfo;
import com.longmaster.platform.entity.SceneNode;
import com.longmaster.platform.mapper.SceneInfoMapper;
import com.longmaster.platform.service.SceneInfoService;
import com.longmaster.platform.service.SceneNodeService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author zk
 * date 2021/4/30 14:00
 * description 场景处理类
 */
@Component
public class SceneInfoServiceImpl extends ServiceImpl<SceneInfoMapper, SceneInfo> implements SceneInfoService {

    @Resource
    private SceneNodeService sceneNodeService;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();

    private static final String KEY_SOURCE_NODE = "sourceNode";
    private static final String KEY_TARGET_NODE = "targetNode";
    private static final String KEY_LABEL = "label";
    private static final String KEY_DATA = "data";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID = "id";

    @Override
    public IPage<SceneInfo> pageQuery(PageParam<SceneQueryDTO> pageParam) {
        IPage<SceneQueryDTO> page = pageParam.getPage();
        SceneQueryDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        IPage<SceneInfo> records = baseMapper.pageSelect(page, params);
        return records;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteScene(Long id) {
        baseMapper.deleteById(id);
        // 删除对应的节点
        return sceneNodeService.remove(new LambdaQueryWrapper<SceneNode>().eq(SceneNode::getSceneId, id));
    }

    @Override
    public boolean openOrClose(Long id, Integer status) {
        baseMapper.updateScene(id, status);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyScene(SceneNodeModifyDTO request) throws JsonProcessingException {
        String id = request.getId();

        SceneInfo entity = new SceneInfo();
        entity.setName(request.getName());
        entity.setAppId(Long.parseLong(request.getAppId()));
        entity.setPayload(buildJson(request));

        if (!StrUtil.isEmpty(id)) {
            entity.setId(Long.parseLong(id));
            String json = buildJson(request);

            SceneInfo one = baseMapper.selectOne(new LambdaQueryWrapper<SceneInfo>().eq(SceneInfo::getId, Long.parseLong(request.getId())).select(SceneInfo::getPayload));
            if (one != null && !StrUtil.isEmpty(one.getPayload())) {
                if (json.equals(one.getPayload())) {
                    this.saveOrUpdate(entity);
                    return true;  // 未发生变化，则后续不再执行
                }
                // 删除旧节点
                sceneNodeService.remove(new LambdaQueryWrapper<SceneNode>().eq(SceneNode::getSceneId, Long.parseLong(request.getId())));
            }
        }

        this.saveOrUpdate(entity);
        Long sceneId = entity.getId();

        List<JsonNode> edges = request.getEdges();
        List<String> sources = edges.stream().map(item -> item.get(KEY_SOURCE_NODE).asText()).collect(Collectors.toList());
        List<String> targets = edges.stream().map(item -> item.get(KEY_TARGET_NODE).asText()).collect(Collectors.toList());

        List<String> roots = sources.stream().filter(a -> !targets.contains(a)).collect(Collectors.toList());

        Map<String, List<String>> map = new HashMap<>();
        for (JsonNode edge : edges) {
            List<String> list = map.get(edge.get(KEY_SOURCE_NODE).asText());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(edge.get(KEY_TARGET_NODE).asText());
            map.put(edge.get(KEY_SOURCE_NODE).asText(), list);
        }

        for (String rootKey : roots) {
            // 根节点（开始）不需要入库
            List<String> list = map.get(rootKey);
            if (list != null && list.size() > 0) {
                for (String key : list) {
                    saveSceneNode(key, map, sceneId, 0L, request.getNodes());
                }
            }
        }
        return true;
    }

    private void saveSceneNode(String key, Map<String, List<String>> map, Long sceneId, Long pId, List<JsonNode> nodes) {
        JsonNode nodeDTO = getNodeDTO(nodes, key);

        int type = nodeDTO.get(KEY_TYPE).asInt();

        SceneNode sceneNode = new SceneNode();
        sceneNode.setName(nodeDTO.get(KEY_LABEL).asText());
        if (type == 4 || type == 6) {
            sceneNode.setPayload(nodeDTO.get(KEY_LABEL).asText());
        } else {
            sceneNode.setPayload(nodeDTO.get(KEY_DATA).asText());
        }
        sceneNode.setSceneId(sceneId);
        sceneNode.setType(type);
        sceneNode.setPId(pId);
        sceneNodeService.saveOrUpdate(sceneNode);

        // 获取子key集合
        List<String> list = map.get(key);

        if (list != null && list.size() > 0) {
            for (String sonKey : list) {
                this.saveSceneNode(sonKey, map, sceneId, sceneNode.getId(), nodes);
            }
        }
    }

    private JsonNode getNodeDTO(List<JsonNode> nodes, String id) {
        List<JsonNode> collect = nodes.stream().filter(a -> a.get(KEY_ID).asText().equals(id)).collect(Collectors.toList());
        if (collect != null && collect.size() > 0) {
            return collect.get(0);
        }
        return null;
    }

    // 去除多余的key之后入库
    private String buildJson(SceneNodeModifyDTO request) throws JsonProcessingException {
        ObjectNode objectNode = (ObjectNode) sObjectMapper.readTree(sObjectMapper.writeValueAsString(request));
        objectNode.remove("sceneId");
        objectNode.remove("sceneName");
        objectNode.remove("appId");
        return objectNode.toString();
    }

}
