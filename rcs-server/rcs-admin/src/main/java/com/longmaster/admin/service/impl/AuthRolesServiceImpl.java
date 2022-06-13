package com.longmaster.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longmaster.admin.dto.auth.AuthAdminDto;
import com.longmaster.admin.entity.AuthRoles;
import com.longmaster.admin.mapper.AuthRolesMapper;
import com.longmaster.admin.service.AuthRolesService;
import com.longmaster.admin.service.RedisService;
import com.longmaster.core.util.TextUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AuthRolesServiceImpl extends ServiceImpl<AuthRolesMapper, AuthRoles> implements AuthRolesService {

    @Resource
    private RedisService mRedisService;

    private static volatile Map<Long, List<Long>> adminRoleMap;
    private static volatile Map<Long, String> adminCarrierMap;
    private static volatile Map<Long, String> adminClientMap;

    private static final ObjectMapper sObjectMapper = new ObjectMapper();
    private static final String SUPER_VAL = "65535";

    @Override
    public String getAdminCarrierIds(String token) {
        return getAdminCarrierIds(getAdminId(token));
    }

    @Override
    public String getAdminCarrierIds(Long adminId) {
        if (adminCarrierMap == null) {
            synchronized (this) {
                if (adminCarrierMap == null) {
                    loadData();
                }
            }
        }
        return adminCarrierMap.get(adminId);
    }

    @Override
    public String getAdminClientIds(String token) {
        Long adminId = getAdminId(token);

        if (adminRoleMap == null) {
            synchronized (this) {
                if (adminRoleMap == null) {
                    loadData();
                }
            }
        }
        List<Long> list = adminRoleMap.get(adminId);
        if (list.contains(3L)) { // 表示超管
            return SUPER_VAL;
        }
        return getAdminClientIds(adminId);
    }

    @Override
    public String getAdminClientIds(Long adminId) {
        if (adminClientMap == null) {
            synchronized (this) {
                if (adminClientMap == null) {
                    loadData();
                }
            }
        }
        return adminClientMap.get(adminId);
    }


    @Override
    public void reload() {
        synchronized (this) {
            loadData();
        }
    }

    private void loadData() {
        adminRoleMap = new HashMap<>();
        adminCarrierMap = new HashMap<>();
        adminClientMap = new HashMap<>();

        List<Map<String, Object>> maps = baseMapper.getAdminCarrierMap();
        if (maps != null && maps.size() != 0) {
            maps.forEach(map -> {
                Long id = Long.parseLong(String.valueOf(map.get("admin_id")));
                String carrierIds = String.valueOf(map.get("carrier_ids"));

                if (adminCarrierMap.containsKey(id)) {
                    String oldStr = adminCarrierMap.get(id);

                    String[] oldArray = oldStr.split(",");
                    String[] currentArray = carrierIds.split(",");

                    List<String> oldList = new ArrayList<>(Arrays.asList(oldArray));
                    oldList.addAll(Arrays.asList(currentArray));
                    adminCarrierMap.put(id, TextUtil.join(",", oldList.stream().distinct().collect(Collectors.toList())));
                } else {
                    adminCarrierMap.put(id, carrierIds);
                }

                String clientIds = String.valueOf(map.get("client_ids"));

                if (adminClientMap.containsKey(id)) {
                    String oldStr = adminClientMap.get(id);

                    String[] oldArray = oldStr.split(",");
                    String[] currentArray = clientIds.split(",");

                    List<String> oldList = new ArrayList<>(Arrays.asList(oldArray));
                    oldList.addAll(Arrays.asList(currentArray));
                    adminClientMap.put(id, TextUtil.join(",", oldList.stream().distinct().collect(Collectors.toList())));
                } else {
                    adminClientMap.put(id, clientIds);
                }

                // 角色信息
                Long roleId = (Long) map.get("role_id");
                List<Long> list = adminRoleMap.get(id);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(roleId);
                adminRoleMap.put(id, list);
            });
        }
    }

    @Override
    public Long getAdminId(String token) {

        AuthAdminDto authAdminDto = null;
        try {
            authAdminDto = sObjectMapper.readValue((String) mRedisService.get(token), AuthAdminDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (authAdminDto == null) {
            return 0L;
        }
        return authAdminDto.getId();
    }
}
