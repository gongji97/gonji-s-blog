package com.gongji.blog.controller;

import com.gongji.blog.common.aop.LogAnnotation;
import com.gongji.blog.common.cache.Cache;
import com.gongji.blog.service.ArticleService;
import com.gongji.blog.service.TagService;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.params.ArticleParam;
import com.gongji.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private TagService tagService;

    @PostMapping
    @LogAnnotation(module = "文章", operator = "获取文章列表")
    public Result listArticles(@RequestBody PageParams pageParams){

        return articleService.listArticle(pageParams);
    }

    /**
     * 首页最热标签
     * @return
     */
    @PostMapping("hot")
    @Cache(expire = 5 * 60 * 1000, name = "hot_article")
    public Result hotArticle(){
        int limit =5 ;
        return  articleService.hotArticle(limit);
    }

    /**
     * 最新文章
     * @return
     */
    @PostMapping("new")
    @Cache(expire = 5 * 60 * 1000, name = "new_article")
    public Result newArticle(){
        int limit = 5;
        return articleService.newArticle(limit);
    }
    /**
     * 文章归档
     */
    @PostMapping("listArchives")

    public Result listArchives(){
        return articleService.listArchives();
    }
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long id){
        return articleService.findArticleById(id);
    }
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }
}
