package com.longmaster.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.longmaster.platform.dto.phoneBook.PhoneNumberBookQueryDTO;
import com.longmaster.platform.dto.phoneBook.PhoneNumberDTO;
import com.longmaster.platform.dto.phoneBook.PhoneNumberDetailDTO;
import com.longmaster.platform.entity.PhoneNumberBook;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PhoneNumberBookMapper extends BaseMapper<PhoneNumberBook> {

    IPage<PhoneNumberDetailDTO> pageSelect(IPage<PhoneNumberBookQueryDTO> page, @Param("params") PhoneNumberBookQueryDTO params);

    IPage<PhoneNumberDTO> selectPhoneNumberBook(IPage<Map<String, Object>> page, @Param("params") Map<String, Object> params);

    List<String> getPhonesByLabel(@Param("ids") List<String> ids);
}
