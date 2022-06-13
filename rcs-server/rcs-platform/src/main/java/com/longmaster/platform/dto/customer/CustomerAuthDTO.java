package com.longmaster.platform.dto.customer;

import com.longmaster.platform.entity.CustomerContract;
import com.longmaster.core.base.SuperEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 客户认证基础信息
 *
 * @author dengshuihong
 * @since 2021-01-22
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAuthDTO {

    @Valid
    @ApiModelProperty("企业信息")
    @NotNull(message = "企业信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerDTO customer;

    @Valid
    @ApiModelProperty(value = "联系人信息")
    @NotNull(message = "联系人信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerContactsDTO contacts;

    @Valid
    @ApiModelProperty("法人信息")
    @NotNull(message = "法人信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerLegalPersonDTO legalPerson;

    @Valid
    @ApiModelProperty("合同信息")
    @NotNull(message = "合同信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerContract contract;

    public interface AduitCT {

    }
}
