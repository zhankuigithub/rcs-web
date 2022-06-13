package com.longmaster.platform.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.Result;
import com.longmaster.platform.dto.menu.MenuAuditRecordDTO;
import com.longmaster.platform.dto.menu.MenuGroupsDTO;
import com.longmaster.platform.dto.menu.MenuWrapperWithCtDTO;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.mapper.*;
import com.longmaster.platform.service.ApplicationService;
import com.longmaster.platform.service.MenuGroupsService;
import com.longmaster.platform.service.PayloadDataService;
import com.longmaster.platform.service.RcsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Stream;

/**
 * <p>
 * 固定菜单组 服务实现类
 * </p>
 *
 * @author dengshuihong
 * @since 2021-02-22
 */
@Slf4j
@Service
@RefreshScope
public class MenuGroupsServiceImpl extends ServiceImpl<MenuGroupsMapper, MenuGroups> implements MenuGroupsService {

    @Resource
    private MenuGroupsMapper menuGroupsMapper;

    @Resource
    private MenuItemMapper menuItemMapper;

    @Resource
    private ChatbotMapper chatbotMapper;

    @Resource
    private ApplicationService applicationService;

    @Resource
    private RestTemplate balancedRestTemplate;

    @Resource
    private RcsService rcsService;

    @Resource
    private MenuAuditRecordMapper menuAuditRecordMapper;

    @Resource
    private CarrierMapper carrierMapper;

    @Resource
    private PayloadDataService payloadDataService;

    @Value("${system.rcsMaap.server}")
    private String serverUrl;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();


    @Override
    public JsonNode getMenu(Long appId, boolean isSubmit) throws JsonProcessingException {
        ObjectNode root = sObjectMapper.createObjectNode();

        MenuGroups menuGroups = menuGroupsMapper.selectOne(new LambdaQueryWrapper<MenuGroups>()
                .eq(MenuGroups::getAppId, appId)
                .eq(MenuGroups::getStatus, 0)
                .eq(MenuGroups::getLogicDel, 0));

        Application app = applicationService.getById(appId);
        Assert.notNull(app, new ServiceException("找不到应用信息（NOT FIND）"));
        root.put("customerId", String.valueOf(app.getCustomerId()));

        if (menuGroups == null) {
            root.set("menu", sObjectMapper.createObjectNode());
            root.put("id", "");
            root.put("appId", appId + "");
            root.put("displayText", "");
            return root;
        }

        String menuIds = menuGroups.getMenuIds();

        if (StrUtil.isEmpty(menuIds)) {
            root.set("menu", sObjectMapper.createObjectNode());
            root.put("id", menuGroups.getId() + "");
            root.put("appId", appId + "");
            root.put("displayText", menuGroups.getName());
            return root;
        }

        List<MenuItem> menus = menuItemMapper.getMenusByStr(menuIds);

        if (menus == null || menus.isEmpty()) {
            root.set("menu", sObjectMapper.createObjectNode());
            root.put("id", menuGroups.getId() + "");
            root.put("appId", appId + "");
            root.put("displayText", menuGroups.getName());
            return root;
        }

        ObjectNode menu = sObjectMapper.createObjectNode();
        ArrayNode entries = sObjectMapper.createArrayNode();

        for (int i = 0; i < menus.size(); i++) {

            Integer type = menus.get(i).getType();
            Long pid = menus.get(i).getPid();

            // 目前菜单只有可能为一级
            if (type == 0 && pid == 0) {
                List<MenuItem> menuItems = menuItemMapper.selectList(new LambdaQueryWrapper<MenuItem>().eq(MenuItem::getPid, menus.get(i).getId()));
                ObjectNode objectNode = sObjectMapper.createObjectNode();
                objectNode.put("displayText", menus.get(i).getName());

                ArrayNode arrayNode = sObjectMapper.createArrayNode();
                // 构建entries  目前只考虑到2级
                for (MenuItem menuItem : menuItems) {
                    if (menuItem.getType() == 1) {
                        if (!isSubmit) {// 不是提交模式，则拼接完整json
                            String payload = menuItem.getPayload();
                            // {"reply":{"displayText":"子菜单1","postback":{"data":"1455785665680216066"}}}  +  {"type":"menu","id":"1051528335084900","mid":"1408321612316459009"}
                            // ->
                            // {"reply":{"displayText":"子菜单1","postback":{"data":"{"type":"menu","id":"1051528335084900","mid":"1408321612316459009"}"}}}
                            JsonNode jsonNode = sObjectMapper.readTree(payload);
                            long payDataId = jsonNode.get("reply").get("postback").get("data").asLong();
                            PayloadData payloadData = payloadDataService.getById(payDataId);

                            ObjectNode node = (ObjectNode) jsonNode.get("reply").get("postback");
                            node.put("data", payloadData.getData());
                            arrayNode.add(jsonNode);
                        } else {
                            arrayNode.add(sObjectMapper.readTree(menuItem.getPayload()));
                        }
                    } else {
                        arrayNode.add(sObjectMapper.readTree(menuItem.getPayload()));
                    }
                }
                objectNode.set("entries", arrayNode);
                ObjectNode sonMenu = sObjectMapper.createObjectNode();
                sonMenu.set("menu", objectNode);
                entries.add(sonMenu);
            }

            // 单独的子级
            if (type > 0) {
                if (pid > 0) {
                    continue;
                }
                MenuItem item = menus.get(i);
                if (item.getType() == 1) {
                    if (!isSubmit) {
                        JsonNode jsonNode = sObjectMapper.readTree(item.getPayload());
                        long payDataId = jsonNode.get("reply").get("postback").get("data").asLong();
                        PayloadData payloadData = payloadDataService.getById(payDataId);
                        ObjectNode node = (ObjectNode) jsonNode.get("reply").get("postback");
                        node.put("data", payloadData.getData());
                        entries.add(jsonNode);

                    } else {

                        entries.add(sObjectMapper.readTree(item.getPayload()));
                    }
                } else {
                    entries.add(sObjectMapper.readTree(item.getPayload()));
                }
            }
        }

        menu.set("entries", entries);
        root.set("menu", menu);

        root.put("id", menuGroups.getId() + "");
        root.put("appId", appId + "");
        root.put("displayText", menuGroups.getName());

        List<MenuAuditRecordDTO> auditRecords = menuAuditRecordMapper.selectAuditItemByGroupId(menuGroups.getId());
        root.set("auditRecords", sObjectMapper.readTree(sObjectMapper.writeValueAsString(auditRecords)));
        return root;
    }

    // 重组json
    private void resolveEntries(JsonNode entries) {
        if (entries.getNodeType() == JsonNodeType.ARRAY) {
            entries.forEach(entry -> {
                if (entry.has("reply")) {
                    JsonNode reply = entry.get("reply");
                    Long id = reply.get("postback").get("data").asLong();
                    PayloadData payloadData = payloadDataService.getById(id);
                    ((ObjectNode) reply.get("postback")).put("data", payloadData.getData());
                }

                if (entry.has("menu")) {
                    JsonNode menu = entry.get("menu");
                    if (menu.has("entries")) {
                        resolveEntries(menu.get("entries"));
                    }
                }
            });
        }
    }

    @Override
    public ArrayNode getCurrentUsed(Long appId, String carrierIds) {
        ArrayNode arrayNode = sObjectMapper.createArrayNode();

        List<Chatbot> chatbots = chatbotMapper.getByCarrierIds(appId, carrierIds);

        for (Chatbot chatbot : chatbots) {
            ObjectNode objectNode = sObjectMapper.createObjectNode();
            String payload = menuAuditRecordMapper.getLatestPass(chatbot.getId(), chatbot.getCarrierId());

            try {
                if (!StrUtil.isEmpty(payload)) {
                    // 格式化成前端识别的菜单
                    JsonNode body = sObjectMapper.readTree(payload);
                    JsonNode entries = body.get("menu").get("entries");
                    resolveEntries(entries);
                    objectNode.set("menus", body);
                } else {
                    continue;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            objectNode.put("carrierId", chatbot.getCarrierId());
            objectNode.put("carrierName", carrierMapper.selectOne(new LambdaQueryWrapper<Carrier>().eq(Carrier::getId, chatbot.getCarrierId()).select(Carrier::getName)).getName());

            MenuGroups menuGroups = menuGroupsMapper.selectLastNewOne(appId);
            if (menuGroups != null) {
                objectNode.put("menuName", menuGroups.getName());
            } else {
                objectNode.put("menuName", "");
            }
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }

    @Override
    public Map<Long, String> submitAudit(Long appId, List<Long> carrierIds) throws JsonProcessingException {

        ObjectNode menu = (ObjectNode) this.getMenu(appId, true);
        menu.remove("id");
        menu.remove("appId");
        menu.remove("customerId");
        menu.remove("displayText");
        menu.remove("auditRecords");

        String json = sObjectMapper.writeValueAsString(menu);
        MenuGroups menuGroups = menuGroupsMapper.selectOne(new LambdaQueryWrapper<MenuGroups>().eq(MenuGroups::getAppId, appId));

        Map<Long, String> failedIds = new HashMap<>();

        try {
            if (carrierIds.contains(Carrier.CMCC)) {
                throw new ServerException("移动暂时不支持提交菜单审核");
            }
        } catch (ServerException e) {
            if (failedIds.containsKey(appId)) {
                failedIds.put(appId, failedIds.get(appId) + "|" + e.getMessage());
            } else {
                failedIds.put(appId, e.getMessage());
            }
        }

        try {
            if (carrierIds.contains(Carrier.CUCC)) {
                Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAppId, appId).eq(Chatbot::getCarrierId, Carrier.CUCC));
                if (chatbot == null) {
                    throw new ServerException("联调（失败）：此应用下未创建机器人");
                }
                // 检查是否存在未审核的菜单
                int record = menuAuditRecordMapper.countNeverAuditMenuAuditRecord(chatbot.getId());
                if (record > 0) {
                    throw new ServerException("联调（失败）：存在未审核的菜单");
                } else {
                    JsonNode jsonNode = rcsService.cuSendMenuAudit(chatbot.getChatBotId(), json);

                    if (jsonNode.get("errorCode").asInt() != 0) {
                        throw new ServerException(jsonNode.get("errorMessage").asText());
                    }

                    if (menuGroups != null) {
                        MenuAuditRecord entity = new MenuAuditRecord();
                        entity.setCarrierId(Carrier.CUCC);
                        entity.setChatBotId(chatbot.getId());
                        entity.setMenuGroupId(menuGroups.getId());
                        entity.setStatus(0);
                        menuAuditRecordMapper.insert(entity);
                    }
                }
            }
        } catch (ServerException e) {
            if (failedIds.containsKey(appId)) {
                failedIds.put(appId, failedIds.get(appId) + "|" + e.getMessage());
            } else {
                failedIds.put(appId, e.getMessage());
            }
        }

        try {
            // 电信
            if (carrierIds.contains(Carrier.CTCC)) {
                Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAppId, appId).eq(Chatbot::getCarrierId, Carrier.CTCC));
                if (chatbot == null) {
                    throw new ServerException("电信（失败）：此应用下未创建机器人");
                }
                // 检查是否存在未审核的菜单
                int record = menuAuditRecordMapper.countNeverAuditMenuAuditRecord(chatbot.getId());
                if (record > 0) {
                    throw new ServerException("电信（失败）：存在未审核的菜单");
                } else {
                    JsonNode jsonNode = rcsService.ctSendMenuAudit(chatbot.getChatBotId(), json);

                    if (jsonNode.get("errorCode").asInt() != 0) {
                        throw new ServerException(jsonNode.get("errorMessage").asText());
                    }

                    if (menuGroups != null) {
                        MenuAuditRecord entity = new MenuAuditRecord();
                        entity.setCarrierId(Carrier.CTCC);
                        entity.setChatBotId(chatbot.getId());
                        entity.setMenuGroupId(menuGroups.getId());
                        entity.setStatus(0);
                        menuAuditRecordMapper.insert(entity);
                    }
                }
            }

        } catch (ServerException e) {
            if (failedIds.containsKey(appId)) {
                failedIds.put(appId, failedIds.get(appId) + "|" + e.getMessage());
            } else {
                failedIds.put(appId, e.getMessage());
            }
        }

        try {
            if (carrierIds.contains(Carrier.LONGMASTER)) {

                Chatbot chatbot = chatbotMapper.selectOne(new LambdaQueryWrapper<Chatbot>().eq(Chatbot::getAppId, appId).eq(Chatbot::getCarrierId, Carrier.LONGMASTER));
                if (chatbot == null) {
                    throw new ServerException("朗玛信息（失败）：此应用下未创建机器人");
                }

                ObjectNode objectNode = sObjectMapper.createObjectNode();
                objectNode.put("chatBotId", chatbot.getChatBotId());
                objectNode.put("payload", json);
                HttpEntity httpEntity = new HttpEntity(objectNode);
                ResponseEntity<Result> result = balancedRestTemplate.exchange(serverUrl + "menu/save", HttpMethod.POST, httpEntity, Result.class);
                if (result.getBody().getCode() != 200) {
                    throw new ServerException("朗玛信息（失败）：提交失败");
                }
                if (menuGroups != null) {
                    MenuAuditRecord entity = new MenuAuditRecord();
                    entity.setCarrierId(Carrier.LONGMASTER);
                    entity.setChatBotId(chatbot.getId());
                    entity.setMenuGroupId(menuGroups.getId());
                    entity.setPayload(json);
                    entity.setStatus(1);
                    menuAuditRecordMapper.insert(entity);
                }
            }
        } catch (ServerException e) {
            if (failedIds.containsKey(appId)) {
                failedIds.put(appId, failedIds.get(appId) + "|" + e.getMessage());
            } else {
                failedIds.put(appId, e.getMessage());
            }
        }

        return failedIds;
    }

    /**
     * 分页查询
     *
     * @param pageParam 分页参数
     * @return
     */
    @Override
    public IPage<MenuGroupsDTO> pageQuery(PageParam<MenuGroupsDTO> pageParam, String carrierIds) {
        IPage<MenuGroupsDTO> page = pageParam.getPage(MenuGroupsDTO.class);
        MenuGroupsDTO params = pageParam.getParams();
        Assert.notNull(params, new ServerException("分页参数不允许为空！"));
        IPage<MenuGroupsDTO> iPage = baseMapper.pageSelect(page, params, carrierIds);
        List<MenuGroupsDTO> records = iPage.getRecords();
        for (MenuGroupsDTO record : records) {
            record.setAuditRecords(menuAuditRecordMapper.getAuditStatusFormMenuGroupId(record.getId(), carrierIds));
        }
        return iPage;
    }


    /**
     * 保存菜单配置
     *
     * @param wrapper 菜单配置数据包装类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenuOfCt(MenuWrapperWithCtDTO wrapper) {

        MenuGroups menuConfig = this.getOne(new LambdaQueryWrapper<MenuGroups>().eq(MenuGroups::getAppId, wrapper.getAppId()));
        List<MenuItem> items = new ArrayList<>();
        wrapper.getMenuItem(items, wrapper.getMenu(), wrapper.getAppId());

        //删除旧的menu
        Optional.ofNullable(menuConfig).ifPresent(
                group -> {
                    Stream.of(group.getMenuIds().split(",")).forEach(menuItemMapper::deleteById);
                    //payloadDataService.deleteByMenuIds(group.getMenuIds());
                }
        );
        items.forEach((item) -> {
            menuItemMapper.insert(item);
            if (item.getType() == 1) {
                this.updatePayload(item);
            }
        });

        //menus groups save
        MenuGroups groups = new MenuGroups();
        groups.setAppId(wrapper.getAppId());
        groups.setMenuIds(ArrayUtil.join(items.stream().map(MenuItem::getId).toArray(), ","));
        groups.setName(wrapper.getDisplayText());

        this.remove(new LambdaQueryWrapper<MenuGroups>().eq(MenuGroups::getAppId, wrapper.getAppId()));
        this.save(groups);
    }

    /**
     * 删除菜单配置
     *
     * @param id 固定菜单ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delMenuConfig(String id) {
        Assert.isTrue(StrUtil.isNotBlank(id), new ServiceException("固定菜单ID不允许为空"));
        MenuGroups menuGroups = this.getById(id);
        Optional.ofNullable(menuGroups).ifPresent(group -> {
            if (StrUtil.isNotBlank(group.getMenuIds())) {
                menuItemMapper.deleteBatchIds(Arrays.asList(group.getMenuIds().split(",")));
            }
        });
        return this.removeById(id);
    }

    @Override
    public void updatePayload(MenuItem menuItem) {
        try {
            String payload = menuItem.getPayload();
            String data = payloadDataService.updateOrSaveDataOfMenu(payload, menuItem);
            menuItemMapper.updatePayload(data, menuItem.getId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
