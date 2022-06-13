package com.longmaster.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longmaster.core.vo.PageParam;
import com.longmaster.core.vo.PageResult;
import com.longmaster.core.util.CommonUtil;
import com.longmaster.platform.dto.sensitiveWords.WordInfoDTO;
import com.longmaster.platform.entity.SensitiveWords;
import com.longmaster.platform.mapper.SensitiveWordsMapper;
import com.longmaster.platform.service.SensitiveWordsService;
import com.longmaster.core.exception.ServiceException;
import com.longmaster.core.valid.Assert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SensitiveWordsServiceImpl extends ServiceImpl<SensitiveWordsMapper, SensitiveWords> implements SensitiveWordsService {

    private static List<String> mList;

    private void reload() {
        synchronized (this) {
            List<SensitiveWords> records = baseMapper.selectList(new LambdaQueryWrapper<>());
            if (records != null && !records.isEmpty()) {
                mList = records.stream().map(SensitiveWords::getWord).collect(Collectors.toList());
            } else {
                mList = new ArrayList<>();
            }
        }
    }

    @Override
    public PageResult<SensitiveWords> pageQuery(PageParam<SensitiveWords> pageParam) {
        IPage<SensitiveWords> page = pageParam.getPage();
        SensitiveWords params = pageParam.getParams();
        Assert.notNull(params, new ServiceException("分页参数不允许为空"));

        IPage<SensitiveWords> list = baseMapper.pageSelect(page, params);

        return new PageResult<>(list.getTotal(), list.getRecords());
    }

    @Override
    public boolean add(WordInfoDTO wordInfo) {
        List<String> words = wordInfo.getWords();
        List<SensitiveWords> list = new ArrayList<>();

        for (String word : words) {
            SensitiveWords sensitiveWords = new SensitiveWords();
            sensitiveWords.setWord(CommonUtil.filterSuperChar(word));
            sensitiveWords.setRemark(wordInfo.getRemark());
            list.add(sensitiveWords);
        }
        // 查出所有的词语
        List<String> wordsList = this.list().stream().map(SensitiveWords::getWord).collect(Collectors.toList());

        // 取出尚未添加过的词语
        List<SensitiveWords> collect = list.stream().filter(item -> !wordsList.contains(item.getWord())).collect(Collectors.toList());
        boolean b = this.saveBatch(collect);

        this.reload();

        return b;
    }

    @Override
    public boolean update(SensitiveWords sensitiveWords) {
        String word = CommonUtil.filterSuperChar(sensitiveWords.getWord());
        SensitiveWords words = this.getOne(new LambdaQueryWrapper<SensitiveWords>().eq(SensitiveWords::getWord, word));

        Assert.isTrue(words == null || words.getId().equals(sensitiveWords.getId()), new ServiceException("词语：%s 已经存在了", word));

        boolean b = this.updateById(sensitiveWords);

        this.reload();

        return b;
    }

    @Override
    public boolean delete(List<Integer> ids) {
        boolean b = this.removeByIds(ids);

        this.reload();

        return b;
    }

    @Override
    public List<String> check(String content) {
        content = CommonUtil.filterSuperChar(content);

        if (mList == null || mList.isEmpty()) {
            synchronized (this) {
                if (mList == null || mList.isEmpty()) {
                    List<SensitiveWords> records = baseMapper.selectList(new LambdaQueryWrapper<>());
                    if (records != null && !records.isEmpty()) {
                        mList = records.stream().map(SensitiveWords::getWord).collect(Collectors.toList());
                    } else {
                        mList = new ArrayList<>();
                    }
                }
            }
        }
        String finalContent = content;
        return mList.stream().filter(finalContent::contains).collect(Collectors.toList());
    }

}
