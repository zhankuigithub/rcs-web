package com.longmaster.admin.cache;

import cn.hutool.core.util.ArrayUtil;
import com.longmaster.admin.dto.auth.AuthAdminDto;

import java.util.Optional;

/**
 * 用户会话 结合 @Auth 注解使用
 */
public class Session {

    private static ThreadLocal<AuthAdminDto> CACHE = new ThreadLocal<>();

    public static void cache(AuthAdminDto adminDto) {
        if (CACHE.get() == null) {
            CACHE.set(adminDto);
        }
    }

    public static AuthAdminDto get() {
        return CACHE.get();
    }

    public static boolean isRole(String[] codes) {
        return Optional.of(CACHE.get()
                .getRoles()
                .stream()
                .filter(role -> ArrayUtil.contains(codes, role.getCode()))
        ).isPresent();
    }
}
