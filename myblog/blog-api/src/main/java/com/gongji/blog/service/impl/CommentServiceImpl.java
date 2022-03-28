package com.gongji.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gongji.blog.dao.mapper.CommentMapper;
import com.gongji.blog.dao.pojo.Comment;
import com.gongji.blog.dao.pojo.SysUser;
import com.gongji.blog.service.CommentService;
import com.gongji.blog.service.SysUserService;
import com.gongji.blog.utils.UserThreadLocal;
import com.gongji.blog.vo.CommentVo;
import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.UserVo;
import com.gongji.blog.vo.params.CommentParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService sysUserService;

    @Override
    public Result commentsByArticleId(Long articleId) {
        /**
         * 根据文章id查询 查询评论列表 从comment 表中查询
         * 根据作者id 查询作者信息
         * 判断 如果level = 1 要去查询它有没有子评论
         * 如果有 进行查询
         */
        LambdaQueryWrapper<Comment> commentsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentsLambdaQueryWrapper.eq(Comment::getArticleId,articleId);
        commentsLambdaQueryWrapper.eq(Comment::getLevel,1);
        commentsLambdaQueryWrapper.orderByAsc(Comment::getCreateDate);
        List<Comment> comments = commentMapper.selectList(commentsLambdaQueryWrapper);
        List<CommentVo> commentVos = copyList(comments);
        return Result.success(commentVos);
    }

    @Override
    public Result addcomment(CommentParams commentParams) {

        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParams.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParams.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParams.getParent();
        if (parent == null || parent == 0) {
            comment.setLevel(1);
        } else {
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParams.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        commentMapper.insert(comment);
        return Result.success(null);
    }

    private List<CommentVo> copyList(List<Comment> comments) {
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment:comments
             ) {
            commentVos.add(copy(comment));

        }
        return commentVos;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        commentVo.setId(String.valueOf(comment.getId()));
        //添加作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = sysUserService.findUserVoByAuthorId(authorId);
        commentVo.setAuthor(userVo);
        //子评论
        Integer level = comment.getLevel();
        //父评论需要设置子评论数组
        if (level == 1) {
            Long id = comment.getId();
            List<CommentVo> commentVoList = findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //子评论需要添加评论对象 是给谁评论
        //添加toUser 给谁评论
        if (level > 1) {
            Long toUid = comment.getToUid();
            //通过回复id去查询评论人信息
            UserVo toUserVo = sysUserService.findUserVoByAuthorId(toUid);
            commentVo.setToUser(toUserVo);
        }

        //添加评论数
        QueryWrapper<Comment> countWrapper = new QueryWrapper<>();
        countWrapper.eq("article_id", comment.getArticleId());
        Integer count = commentMapper.selectCount(countWrapper);
        commentVo.setCommentCounts(count);

        return commentVo;
    }


    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getParentId,id).eq(Comment::getLevel, 2);
        List<Comment> comments = commentMapper.selectList(commentLambdaQueryWrapper);
        List<CommentVo> commentVos = copyList(comments);
        return commentVos;
    }
}
