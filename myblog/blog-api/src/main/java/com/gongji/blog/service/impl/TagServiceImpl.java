package com.gongji.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gongji.blog.dao.mapper.TagMapper;
import com.gongji.blog.dao.pojo.Tag;
import com.gongji.blog.service.TagService;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;


    @Override
    public List<TagVo> findTagsByArticleId(long articleId) {
        //plus无法进行多表查询
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);

        return copyList(tags);
    }

    @Override
    public Result hots(int limit) {
        /*
        查询最热标签
        标签所拥有的文章数量最多即为最热标签
        查询，根据tag_id,分组 计数
         */
        List<Long> tagIds = tagMapper.findHotsTagIds(limit);
        if (CollectionUtils.isEmpty(tagIds)){
            return Result.success(Collections.emptyList());
        }
        //select * from tag where id in (1,2,3,4)
        List<Tag> hotsTagIds = tagMapper.findTagsByTagIds(tagIds);
        return Result.success(hotsTagIds);
    }

    @Override
    public Result findAll() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getTagName);
        List<Tag> tags = tagMapper.selectList(queryWrapper);
        return Result.success(copyList(tags));

    }

    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        List<Tag> tags = tagMapper.selectList(queryWrapper);
        return Result.success(copyList(tags));


    }

    private List<TagVo> copyList(List<Tag> tags) {
        ArrayList<TagVo> list = new ArrayList<>();
        for (Tag tag:tags
             ) {
            list.add(copy(tag));
        }
        return list;
    }

    private TagVo copy(Tag tag) {
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        tagVo.setId(String.valueOf(tag.getId()));
        return tagVo;
    }
}
