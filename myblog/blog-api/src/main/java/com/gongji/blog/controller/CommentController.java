package com.gongji.blog.controller;

import com.gongji.blog.service.CommentService;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.params.CommentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/article/{id}")
    public Result comments(@PathVariable("id") Long articleId){
        return commentService.commentsByArticleId(articleId);
    }
    @PostMapping("create/change")
    public  Result comment(@RequestBody CommentParams commentParams){
        return commentService.addcomment(commentParams);
    }
}
