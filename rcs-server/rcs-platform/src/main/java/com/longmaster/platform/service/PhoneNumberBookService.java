package com.longmaster.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.phoneBook.*;
import com.longmaster.platform.entity.PhoneNumberBook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PhoneNumberBookService extends IService<PhoneNumberBook> {


    boolean addPhone(PhoneNumberModifyDTO phoneNumber);


    boolean updPhone(PhoneNumberModifyDTO phoneNumber);


    boolean deletePhone(Long id);


    boolean deleteByCid(Long customerId);


    PageResult<PhoneNumberDetailDTO> pageQuery(PageParam<PhoneNumberBookQueryDTO> pageParam);


    PageResult<PhoneNumberDTO> selectPhoneNumberBook(PageParam<Map<String, Object>> pageParam);


    List<String> getPhonesByLabel(List<String> ids);


    List<String> batchAdd(BatchAddPhDTO batchAddPh);


    List<String> batchAddByExcel(BatchAddExcelPhDTO batchAddExcelPh) throws IOException;


    List<String> readExcel(MultipartFile file) throws IOException;
}
