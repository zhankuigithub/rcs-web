package com.longmaster.platform.dto.phoneBook;

import com.longmaster.platform.entity.PhoneNumberBook;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchAddPhDTO {

    @ApiModelProperty(value = "客户id")
    private Long customerId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "号码")
    private List<String> phones;

    @ApiModelProperty(value = "标签ids")
    private List<Long> labelIds;

    @ApiModelProperty(value = "黑名单应用")
    private List<Long> applicationIds;

    @ApiModelProperty(value = "号码2")
    private List<PhoneNumberBook> phoneNumberBooks;

    public List<PhoneNumberBook> getPhoneNumberBooks() {
        // 特殊处理
        if (phoneNumberBooks == null && !phones.isEmpty()) {
            phoneNumberBooks = new ArrayList<>();
            for (String phone : phones) {
                PhoneNumberBook entity = new PhoneNumberBook();
                entity.setPhoneNum(phone);
                entity.setCustomerId(customerId);
                entity.setRemark(remark);
                phoneNumberBooks.add(entity);
            }
        }
        return phoneNumberBooks;
    }
}
