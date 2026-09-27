package com.ankita.mediumclone.controller;

import com.ankita.mediumclone.dto.ArticleRequest;
import com.ankita.mediumclone.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.ankita.mediumclone.dto.ArticleResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ArticleResponse createArticle(@Valid @RequestBody ArticleRequest request) {
        return articleService.createArticle(request);
    }

    @GetMapping
    public List<ArticleResponse> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public ArticleResponse getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @PutMapping("/{id}")
    public ArticleResponse updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request) {

        return articleService.updateArticle(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
            articleService.deleteArticle(id);

    }
}