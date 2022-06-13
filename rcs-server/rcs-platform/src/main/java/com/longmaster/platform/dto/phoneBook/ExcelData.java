package com.longmaster.platform.dto.phoneBook;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelData {

    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("号码")
    private String phone;
    @ExcelProperty("性别")
    private String sex;


    public ExcelData() {
    }

    public ExcelData(String name, String phone, String sex) {
        this.name = name;
        this.phone = phone;
        this.sex = sex;
    }
}
