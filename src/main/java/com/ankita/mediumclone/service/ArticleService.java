package com.ankita.mediumclone.service;

import com.ankita.mediumclone.dto.ArticleRequest;
import com.ankita.mediumclone.dto.ArticleRequest;
import com.ankita.mediumclone.dto.ArticleResponse;
import com.ankita.mediumclone.entity.Article;
import com.ankita.mediumclone.entity.User;
import com.ankita.mediumclone.exception.ArticleNotFoundException;
import com.ankita.mediumclone.exception.ForbiddenException;
import com.ankita.mediumclone.repository.ArticleRepository;
import com.ankita.mediumclone.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleService(
            ArticleRepository articleRepository,
            UserRepository userRepository) {

        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public ArticleResponse createArticle(ArticleRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Article article = new Article(
                request.getTitle(),
                request.getContent(),
                author
        );

        Article savedArticle = articleRepository.save(article);

        return new ArticleResponse(
                savedArticle.getId(),
                savedArticle.getTitle(),
                savedArticle.getContent(),
                savedArticle.getAuthor().getUsername(),
                savedArticle.getCreatedAt(),
                savedArticle.getUpdatedAt()
        );
    }

    public Page<ArticleResponse> getAllArticles(Pageable pageable) {

        return articleRepository.findAll(pageable)
                .map(article -> new ArticleResponse(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getAuthor().getUsername(),
                        article.getCreatedAt(),
                        article.getUpdatedAt()
                ));
    }

    public ArticleResponse getArticleById(Long id){

            Article article = articleRepository.findById(id)
                    .orElseThrow(() ->
                            new ArticleNotFoundException("Article not found"));

            return new ArticleResponse(
                    article.getId(),
                    article.getTitle(),
                    article.getContent(),
                    article.getAuthor().getUsername(),
                    article.getCreatedAt(),
                    article.getUpdatedAt()
            );
        }

    public ArticleResponse updateArticle(Long id, ArticleRequest request) {

        // 1. Find the article
        Article article = articleRepository.findById(id)
                .orElseThrow(() ->
                        new ArticleNotFoundException("Article not found"));

        // 2. Find who is currently logged in
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 3. Make sure the logged-in user owns this article
        if (!article.getAuthor().getEmail().equals(email)) {
            throw new ForbiddenException(
                    "You are not allowed to update this article");
        }

        // 4. Update the article
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

        // 5. Save it
        Article updatedArticle = articleRepository.save(article);

        // 6. Return safe response
        return new ArticleResponse(
                updatedArticle.getId(),
                updatedArticle.getTitle(),
                updatedArticle.getContent(),
                updatedArticle.getAuthor().getUsername(),
                updatedArticle.getCreatedAt(),
                updatedArticle.getUpdatedAt()
        );
    }

    public void deleteArticle(Long id) {

        // 1. Find the article
        Article article = articleRepository.findById(id)
                .orElseThrow(() ->
                        new ArticleNotFoundException("Article not found"));

        // 2. Find the logged-in user's email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 3. Make sure the logged-in user owns the article
        if (!article.getAuthor().getEmail().equals(email)) {
            throw new ForbiddenException(
                    "You are not allowed to delete this article");
        }

        // 4. Delete the article
        articleRepository.delete(article);


    }

}

