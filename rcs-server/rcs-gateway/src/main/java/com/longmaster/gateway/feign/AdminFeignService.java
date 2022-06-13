package com.longmaster.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@Component
@FeignClient(value = "${serverUrl.nacosAdminUrl}")
public interface AdminFeignService {

    // 获取管理员下的运营商权限
    @GetMapping(value = "/manage/roles/getCarrierIds")
    String getCarrierIds(@RequestHeader("ACCESS-TOKEN") String accessToken);

}
