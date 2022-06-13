package com.longmaster.platform.controller.manage;

import com.longmaster.core.vo.Result;
import com.longmaster.platform.entity.ContactInformation;
import com.longmaster.platform.service.ContactInformationService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/manage/contactInformation")
@Api(value = "ContactInformationController", tags = "联系方式")
public class ContactInformationController {

    @Resource
    private ContactInformationService contactInformationService;

    @PostMapping("save")
    public Result saveContactInformation(@Validated @RequestBody ContactInformation contactInformation) {
        return Result.SUCCESS(contactInformationService.save(contactInformation));
    }

}
