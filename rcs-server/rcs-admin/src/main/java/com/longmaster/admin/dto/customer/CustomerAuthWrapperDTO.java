package com.longmaster.admin.dto.customer;

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
public class CustomerAuthWrapperDTO {

    @Valid
    @ApiModelProperty("企业信息")
    @NotNull(message = "企业信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerExpandDTO customer;

    @Valid
    @ApiModelProperty(value = "联系人信息")
    @NotNull(message = "联系人信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerContactsExpandDTO contacts;

    @Valid
    @ApiModelProperty("法人信息")
    @NotNull(message = "法人信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerLegalPersonBaseDTO legalPerson;

    @Valid
    @ApiModelProperty("合同信息")
    @NotNull(message = "法人信息不允许为空", groups = {SuperEntity.Create.class, AduitCT.class})
    private CustomerContractBaseDTO contract;

    public interface AduitCT {

    }
}
