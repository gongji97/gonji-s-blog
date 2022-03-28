package com.gongji.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gongji.blog.dao.dos.Archives;
import com.gongji.blog.dao.mapper.ArticleBodyMapper;
import com.gongji.blog.dao.mapper.ArticleMapper;
import com.gongji.blog.dao.mapper.ArticleTagMapper;
import com.gongji.blog.dao.pojo.Article;
import com.gongji.blog.dao.pojo.ArticleBody;
import com.gongji.blog.dao.pojo.ArticleTag;
import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.service.*;
import com.gongji.blog.utils.UserThreadLocal;
import com.gongji.blog.vo.ArticleBodyVo;
import com.gongji.blog.vo.ArticleVo;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.TagVo;
import com.gongji.blog.vo.params.ArticleParam;
import com.gongji.blog.vo.params.PageParams;



import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ArticleTagMapper articleTagMapper;

    /**
     * 通过文章获取文章信息
     *
     * @param id
     * @return
     */

    @Override
    public Result findArticleById(Long id) {
        /**
         * 根据id查询文章 信息
         * 根据bodyid和categorivo 进行关联查询
         */
        Article article = articleMapper.selectById(id);
        ArticleVo articleVo = copy(article, true, true,true,true);
        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
        //查看完文章 新增阅读量
        //看完文章 本应该直接返回数据 这是做了一个更新操作 更新时加写锁 阻塞其它的读操作 性能会比较低
        //更新 增加了此次接口的耗时 如果更新出问题 不能影响 查看文章操作
        //线程池 可以把更新操作扔到线程池中去执行 和主线程就不相关了
    }

    @Override
    public Result listArticle(PageParams pageParams) {
        /**
         * 分页查询数据库表
         */
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(page, pageParams.getCategoryId(), pageParams.getTagId(), pageParams.getYear(), pageParams.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
//        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//        if (pageParams.getCategoryId()!=null){
//            //查询对应的文章列表
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//        }
//        //是否置顶排序
//        queryWrapper.orderByDesc(Article::getCreateDate);
//        //order by creat_data desc
//        queryWrapper.orderByDesc(Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = articlePage.getRecords();
//        List<ArticleVo> articleVo = copyList(records, true, true);
//        return Result.success(articleVo);

    }

    @Override
    public Result hotArticle(int limit) {
        //select id,title from article by view_counts desc limit 5
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result newArticle(int limit) {
        //select id,title from article by creat_date desc limit 5
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));

    }

    @Override
    public Result listArchives() {
        List<Archives> archives = articleMapper.listArchices();
        return Result.success(archives);
    }
    @Transactional
    @Override
    public Result publish(ArticleParam articleParam) {
        /**
         *  发布文章 目的是 构建Article对象
         *  作者id 当前的登录用户  此接口要加入到登录拦截中去
         *  标签  要将标签加入到关联列表中
         *  body 内容储存 article body id
         */
        SysUser sysUser = UserThreadLocal.get();
        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        article.setBodyId(-1L);
        //插入之后 会生成一个文章id
        articleMapper.insert(article);
        //tags
        List<TagVo> tags = articleParam.getTags();
        if (tags !=null){
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        //?
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        HashMap<String, String> map = new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        ArrayList<ArticleVo> articleVoArrayList = new ArrayList<>();
        for (Article record : records
        ) {
            articleVoArrayList.add(copy(record, isTag, isAuthor, false, false));
        }
        return articleVoArrayList;
    }

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategorys(categoryService.findCategoryById(categoryId));

        }
        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }
}
