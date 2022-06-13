package com.longmaster.platform.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.platform.dto.phoneBook.*;
import com.longmaster.platform.entity.*;
import com.longmaster.platform.mapper.PhoneNumberBookMapper;
import com.longmaster.platform.service.*;
import com.longmaster.core.exception.ServerException;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PhoneNumberBookServiceImpl extends ServiceImpl<PhoneNumberBookMapper, PhoneNumberBook> implements PhoneNumberBookService {

    @Resource
    private ApplicationService applicationService;

    @Resource
    private LabelsService labelsService;

    @Resource
    private IPhoneNumberLabelService phoneNumberLabelService;

    @Resource
    private IPhoneNumberAppBlacklistService phoneNumberAppBlacklistService;

    @Override
    @Transactional
    public boolean addPhone(PhoneNumberModifyDTO phoneNumber) {

        PhoneNumberBook phoneNumberBook = new PhoneNumberBook();
        phoneNumberBook.setPhoneNum(phoneNumber.getPhoneNum());
        phoneNumberBook.setCustomerId(phoneNumber.getCustomerId());
        phoneNumberBook.setName(phoneNumber.getName());
        phoneNumberBook.setSex(phoneNumber.getSex());
        phoneNumberBook.setRemark(phoneNumber.getRemark());

        // 检查号码是否存在
        long count = this.count(new LambdaQueryWrapper<PhoneNumberBook>().eq(PhoneNumberBook::getPhoneNum, phoneNumber.getPhoneNum()).eq(PhoneNumberBook::getCustomerId, phoneNumber.getCustomerId()));
        Assert.isTrue(count == 0, new ServerException("号码%s，在当前客户下已经存在了~", phoneNumber.getPhoneNum()));

        boolean save = this.save(phoneNumberBook);
        if (save) {
            List<Long> labelIds = phoneNumber.getLabelIds();
            if (labelIds != null && !labelIds.isEmpty()) {
                for (Long labelId : labelIds) {
                    PhoneNumberLabel phoneNumberLabel = new PhoneNumberLabel();
                    phoneNumberLabel.setPhoneId(phoneNumberBook.getId());
                    phoneNumberLabel.setLabelId(labelId);
                    phoneNumberLabelService.save(phoneNumberLabel);
                }
            }
            List<Long> applicationIds = phoneNumber.getApplicationIds();
            if (applicationIds != null && !applicationIds.isEmpty()) {
                for (Long applicationId : applicationIds) {
                    PhoneNumberAppBlacklist numberAppBlacklist = new PhoneNumberAppBlacklist();
                    numberAppBlacklist.setPhoneId(phoneNumberBook.getId());
                    numberAppBlacklist.setApplicationId(applicationId);
                    phoneNumberAppBlacklistService.save(numberAppBlacklist);
                }
                phoneNumberAppBlacklistService.reload();
            }
        }
        return save;
    }

    @Override
    @Transactional
    public boolean updPhone(PhoneNumberModifyDTO phoneNumber) {

        PhoneNumberBook phoneNumberBook = new PhoneNumberBook();
        phoneNumberBook.setId(phoneNumber.getId());
        phoneNumberBook.setPhoneNum(phoneNumber.getPhoneNum());
        phoneNumberBook.setCustomerId(phoneNumber.getCustomerId());
        phoneNumberBook.setName(phoneNumber.getName());
        phoneNumberBook.setSex(phoneNumber.getSex());
        phoneNumberBook.setRemark(phoneNumber.getRemark());

        PhoneNumberBook one = this.getOne(new LambdaQueryWrapper<PhoneNumberBook>().eq(PhoneNumberBook::getPhoneNum, phoneNumber.getPhoneNum()).eq(PhoneNumberBook::getCustomerId, phoneNumber.getCustomerId()));
        Assert.isTrue(one == null || one.getId().equals(phoneNumber.getId()), new ServiceException("号码%s，在当前客户下已经存在了~", phoneNumber.getPhoneNum()));

        boolean upd = this.updateById(phoneNumberBook);


        List<Long> labelIds = phoneNumber.getLabelIds();
        // 删除旧数据
        phoneNumberLabelService.remove(new LambdaQueryWrapper<PhoneNumberLabel>().eq(PhoneNumberLabel::getPhoneId, phoneNumber.getId()));
        if (labelIds != null && !labelIds.isEmpty()) {

            for (Long labelId : labelIds) {
                PhoneNumberLabel phoneNumberLabel = new PhoneNumberLabel();
                phoneNumberLabel.setPhoneId(phoneNumber.getId());
                phoneNumberLabel.setLabelId(labelId);
                phoneNumberLabelService.save(phoneNumberLabel);
            }
        }

        List<Long> applicationIds = phoneNumber.getApplicationIds();
        // 删除旧数据
        phoneNumberAppBlacklistService.remove(new LambdaQueryWrapper<PhoneNumberAppBlacklist>().eq(PhoneNumberAppBlacklist::getPhoneId, phoneNumber.getId()));

        if (applicationIds != null && !applicationIds.isEmpty()) {

            for (Long applicationId : applicationIds) {
                PhoneNumberAppBlacklist numberAppBlacklist = new PhoneNumberAppBlacklist();
                numberAppBlacklist.setPhoneId(phoneNumberBook.getId());
                numberAppBlacklist.setApplicationId(applicationId);
                phoneNumberAppBlacklistService.save(numberAppBlacklist);
            }
        }
        // 重新加载黑名单
        phoneNumberAppBlacklistService.reload();

        return upd;
    }

    @Override
    @Transactional
    public boolean deletePhone(Long id) {
        phoneNumberAppBlacklistService.remove(new LambdaQueryWrapper<PhoneNumberAppBlacklist>().eq(PhoneNumberAppBlacklist::getPhoneId, id));
        phoneNumberLabelService.remove(new LambdaQueryWrapper<PhoneNumberLabel>().eq(PhoneNumberLabel::getPhoneId, id));
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean deleteByCid(Long customerId) {
        List<PhoneNumberBook> list = this.list(new LambdaQueryWrapper<PhoneNumberBook>().eq(PhoneNumberBook::getCustomerId, customerId));
        if (list != null && !list.isEmpty()) {
            for (PhoneNumberBook phoneNumberBook : list) {
                phoneNumberAppBlacklistService.remove(new LambdaQueryWrapper<PhoneNumberAppBlacklist>().eq(PhoneNumberAppBlacklist::getPhoneId, phoneNumberBook.getId()));
                phoneNumberLabelService.remove(new LambdaQueryWrapper<PhoneNumberLabel>().eq(PhoneNumberLabel::getPhoneId, phoneNumberBook.getId()));
            }
        }
        phoneNumberAppBlacklistService.reload();
        return this.remove(new LambdaQueryWrapper<PhoneNumberBook>().eq(PhoneNumberBook::getCustomerId, customerId));
    }

    @Override
    public PageResult<PhoneNumberDetailDTO> pageQuery(PageParam<PhoneNumberBookQueryDTO> pageParam) {
        IPage<PhoneNumberDetailDTO> iPage = baseMapper.pageSelect(pageParam.getPage(), pageParam.getParams());
        List<PhoneNumberDetailDTO> records = iPage.getRecords();
        for (PhoneNumberDetailDTO record : records) {
            Long id = record.getId();

            List<PhoneNumberAppBlacklist> blacklists = phoneNumberAppBlacklistService.list(new LambdaQueryWrapper<PhoneNumberAppBlacklist>().eq(PhoneNumberAppBlacklist::getPhoneId, id).select(PhoneNumberAppBlacklist::getApplicationId));

            if (blacklists != null && !blacklists.isEmpty()) {
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < blacklists.size(); i++) {

                    Application one = applicationService.getOne(new LambdaQueryWrapper<Application>().eq(Application::getId, blacklists.get(i).getApplicationId()).select(Application::getName));
                    String name = one.getName();

                    builder.append(name);
                    if (i != blacklists.size() - 1) {
                        builder.append(",");
                    }
                }

                record.setAppBlacklistIds(blacklists.stream().map(item -> {
                    Long applicationId = item.getApplicationId();
                    return String.valueOf(applicationId);
                }).collect(Collectors.toList()));
                record.setAppBlacklistNames(builder.toString());
            }

            List<PhoneNumberLabel> list = phoneNumberLabelService.list(new LambdaQueryWrapper<PhoneNumberLabel>().eq(PhoneNumberLabel::getPhoneId, id).select(PhoneNumberLabel::getLabelId));
            if (list != null && !list.isEmpty()) {
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < list.size(); i++) {
                    Labels one = labelsService.getOne(new LambdaQueryWrapper<Labels>().eq(Labels::getId, list.get(i).getLabelId()).select(Labels::getName));

                    builder.append(one.getName());
                    if (i != list.size() - 1) {
                        builder.append(",");
                    }
                }
                record.setLabelIds(list.stream().map(item -> {
                    Long labelId = item.getLabelId();
                    return String.valueOf(labelId);
                }).collect(Collectors.toList()));

                record.setLabelNames(builder.toString());
            }
        }
        return new PageResult<>(iPage.getTotal(), iPage.getRecords());
    }

    @Override
    public PageResult<PhoneNumberDTO> selectPhoneNumberBook(PageParam<Map<String, Object>> pageParam) {
        IPage<PhoneNumberDTO> iPage = baseMapper.selectPhoneNumberBook(pageParam.getPage(), pageParam.getParams());
        return new PageResult<>(iPage.getTotal(), iPage.getRecords());
    }

    @Override
    public List<String> getPhonesByLabel(List<String> ids) {
        return baseMapper.getPhonesByLabel(ids);
    }

    @Override
    @Transactional
    public List<String> batchAdd(BatchAddPhDTO batchAddPh) {
        List<String> failedPhones = new ArrayList<>();

        Long customerId = batchAddPh.getCustomerId();
        List<PhoneNumberBook> phoneNumberBooks = batchAddPh.getPhoneNumberBooks();
        List<Long> labelIds = batchAddPh.getLabelIds();
        List<Long> applicationIds = batchAddPh.getApplicationIds();

        for (PhoneNumberBook phoneNumberBook : phoneNumberBooks) {
            String phone = phoneNumberBook.getPhoneNum();
            try {
                long count = this.count(new LambdaQueryWrapper<PhoneNumberBook>().eq(PhoneNumberBook::getPhoneNum, phone)
                        .eq(PhoneNumberBook::getCustomerId, customerId));
                Assert.isTrue(count == 0, new ServerException("在当前客户下已经存在此号码了~", phone));

                boolean batch = this.save(phoneNumberBook);

                if (batch) {
                    // 加标签表
                    long aLong = phoneNumberBook.getId();

                    if (labelIds != null && !labelIds.isEmpty()) {
                        for (Long labelId : labelIds) {
                            PhoneNumberLabel phoneNumberLabel = new PhoneNumberLabel();
                            phoneNumberLabel.setPhoneId(aLong);
                            phoneNumberLabel.setLabelId(labelId);
                            phoneNumberLabelService.save(phoneNumberLabel);
                        }
                    }

                    if (applicationIds != null && !applicationIds.isEmpty()) {
                        for (Long applicationId : applicationIds) {
                            PhoneNumberAppBlacklist numberAppBlacklist = new PhoneNumberAppBlacklist();
                            numberAppBlacklist.setPhoneId(aLong);
                            numberAppBlacklist.setApplicationId(applicationId);
                            phoneNumberAppBlacklistService.save(numberAppBlacklist);
                        }
                    }
                }
            } catch (ServerException e) {
                failedPhones.add(phone);
            }
        }
        phoneNumberAppBlacklistService.reload();
        return failedPhones;
    }

    @Override
    public List<String> batchAddByExcel(BatchAddExcelPhDTO batchAddExcelPh) throws IOException {
        MultipartFile multipartFile = batchAddExcelPh.getFile();

        ExcelReaderBuilder builder = EasyExcel.read(multipartFile.getInputStream());

        List<PhoneNumberBook> entities = new ArrayList<>();

        builder.autoCloseStream(true);
        builder.excelType(ExcelTypeEnum.XLSX);
        builder.registerReadListener(new AnalysisEventListener<ExcelData>() {
            @Override
            public void invoke(ExcelData data, AnalysisContext context) {
                PhoneNumberBook entity = new PhoneNumberBook();
                entity.setCustomerId(batchAddExcelPh.getCustomerId());
                entity.setRemark(batchAddExcelPh.getRemark());

                entity.setSex((data.getSex() == null) ? 0 : ("男".equals(data.getSex()) ? 1 : 2));
                entity.setName(data.getName());
                entity.setPhoneNum(data.getPhone());
                entities.add(entity);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }
        });

        builder.head(ExcelData.class);
        builder.doReadAll();

        BatchAddPhDTO batchAddPh = new BatchAddPhDTO();

        batchAddPh.setCustomerId(batchAddExcelPh.getCustomerId());
        batchAddPh.setLabelIds(batchAddExcelPh.getLabelIds());
        batchAddPh.setApplicationIds(batchAddExcelPh.getApplicationIds());
        batchAddPh.setRemark(batchAddExcelPh.getRemark());
        batchAddPh.setPhoneNumberBooks(entities);

        return this.batchAdd(batchAddPh);
    }

    @Override
    public List<String> readExcel(MultipartFile file) throws IOException {
        ExcelReaderBuilder builder = EasyExcel.read(file.getInputStream());

        List<String> entities = new ArrayList<>();

        builder.autoCloseStream(true);
        builder.excelType(ExcelTypeEnum.XLSX);
        builder.registerReadListener(new AnalysisEventListener<ExcelData>() {
            @Override
            public void invoke(ExcelData data, AnalysisContext context) {
                entities.add(data.getPhone());
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }
        });

        builder.head(ExcelData.class);
        builder.doReadAll();

        return entities;
    }
}
