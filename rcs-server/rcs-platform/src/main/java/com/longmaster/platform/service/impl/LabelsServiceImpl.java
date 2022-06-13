package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.platform.dto.label.LabelQueryDTO;
import com.longmaster.platform.dto.phoneBook.BatchUpdLabelDTO;
import com.longmaster.platform.entity.Labels;
import com.longmaster.platform.entity.PhoneNumberLabel;
import com.longmaster.platform.mapper.LabelsMapper;
import com.longmaster.platform.service.IPhoneNumberLabelService;
import com.longmaster.platform.service.LabelsService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class LabelsServiceImpl extends ServiceImpl<LabelsMapper, Labels> implements LabelsService {

    @Resource
    private IPhoneNumberLabelService phoneNumberLabelService;

    @Override
    public boolean addLabel(Labels label) {
        Labels one = this.getOne(new LambdaQueryWrapper<Labels>().eq(Labels::getName, label.getName()).eq(Labels::getCustomerId, label.getCustomerId()));
        Assert.isNull(one, new ServiceException("当前标签：%s 已经存在~", label.getName()));
        return this.save(label);
    }

    @Override
    public boolean updLabel(Labels label) {
        Labels one = this.getOne(new LambdaQueryWrapper<Labels>().eq(Labels::getName, label.getName()).eq(Labels::getCustomerId, label.getCustomerId()));

        Assert.isTrue(one == null || one.getId().equals(label.getId()), new ServiceException("标签：%s 已经存在了", label.getName()));

        return this.updateById(label);
    }

    @Override
    @Transactional
    public boolean deleteLabel(Long id) {
        boolean b = this.removeById(id);
        if (b) {
            phoneNumberLabelService.remove(new LambdaQueryWrapper<PhoneNumberLabel>().eq(PhoneNumberLabel::getLabelId, id));
        }
        return b;
    }

    @Override
    public IPage<Labels> pageQuery(PageParam<LabelQueryDTO> pageParam) {
        IPage<LabelQueryDTO> page = pageParam.getPage();
        LabelQueryDTO params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));
        return baseMapper.pageSelect(page, params);
    }

    @Override
    public List<Map<String, Object>> group(Long id) {
        return baseMapper.selectGroup(id);
    }

    @Override
    @Transactional
    public boolean batchUpdLabel(BatchUpdLabelDTO batchUpdLabel) {
        List<Long> labelIds = batchUpdLabel.getLabelIds();
        List<Long> phoneIds = batchUpdLabel.getPhoneIds();

        for (Long phoneId : phoneIds) {
            // 先清空对应人的标签
            phoneNumberLabelService.remove(new LambdaQueryWrapper<PhoneNumberLabel>().eq(PhoneNumberLabel::getPhoneId, phoneId));

            for (Long labelId : labelIds) {
                PhoneNumberLabel phoneNumberLabel = new PhoneNumberLabel();
                phoneNumberLabel.setLabelId(labelId);
                phoneNumberLabel.setPhoneId(phoneId);
                boolean b = phoneNumberLabelService.save(phoneNumberLabel);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

}
