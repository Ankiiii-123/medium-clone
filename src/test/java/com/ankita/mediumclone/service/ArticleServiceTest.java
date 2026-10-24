
package com.ankita.mediumclone.service;

import com.ankita.mediumclone.dto.ArticleResponse;
import com.ankita.mediumclone.entity.Article;
import com.ankita.mediumclone.entity.User;
import com.ankita.mediumclone.repository.ArticleRepository;
import com.ankita.mediumclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Sort;
import com.ankita.mediumclone.dto.ArticleRequest;
import com.ankita.mediumclone.exception.ArticleNotFoundException;

import org.springframework.security.core.context.SecurityContextHolder;


@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        articleService = new ArticleService(
                articleRepository,
                userRepository
        );
    }

    @Test
    void getArticleById_shouldReturnArticleWhenFound() {
        User author = new User();
        author.setUsername("Ankita");

        Article article = new Article(
                "Learning Spring Boot",
                "My first backend project",
                author
        );

        ReflectionTestUtils.setField(article, "id", 1L);

        when(articleRepository.findById(1L))
                .thenReturn(Optional.of(article));

        ArticleResponse response =
                articleService.getArticleById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Learning Spring Boot", response.getTitle());
        assertEquals("My first backend project", response.getContent());
        assertEquals("Ankita", response.getAuthorName());

        verify(articleRepository).findById(1L);
    }


    @Test
    void getArticleById_shouldThrowExceptionWhenArticleNotFound() {

        when(articleRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                com.ankita.mediumclone.exception.ArticleNotFoundException.class,
                () -> articleService.getArticleById(99L)
        );

        verify(articleRepository).findById(99L);
    }


    @Test
    void updateArticle_shouldThrowExceptionWhenUserIsNotOwner() {
        User author = new User();
        author.setUsername("Rahul");
        author.setEmail("rahul@gmail.com");

        Article article = new Article(
                "Original title",
                "Original content",
                author
        );

        ReflectionTestUtils.setField(article, "id", 1L);

        when(articleRepository.findById(1L))
                .thenReturn(Optional.of(article));

        org.springframework.security.core.Authentication authentication =
                org.mockito.Mockito.mock(
                        org.springframework.security.core.Authentication.class
                );

        when(authentication.getName()).thenReturn("ankita@gmail.com");

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        try {
            com.ankita.mediumclone.dto.ArticleRequest request =
                    new com.ankita.mediumclone.dto.ArticleRequest();

            request.setTitle("Modified title");
            request.setContent("Modified content");

            assertThrows(
                    com.ankita.mediumclone.exception.ForbiddenException.class,
                    () -> articleService.updateArticle(1L, request)
            );

            verify(articleRepository, never()).save(any(Article.class));
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }


    @Test
    void deleteArticle_shouldThrowExceptionWhenUserIsNotOwner() {
        User author = new User();
        author.setUsername("Rahul");
        author.setEmail("rahul@gmail.com");

        Article article = new Article(
                "Rahul's article",
                "Some content",
                author
        );

        ReflectionTestUtils.setField(article, "id", 1L);

        when(articleRepository.findById(1L))
                .thenReturn(Optional.of(article));

        org.springframework.security.core.Authentication authentication =
                org.mockito.Mockito.mock(
                        org.springframework.security.core.Authentication.class
                );

        when(authentication.getName()).thenReturn("ankita@gmail.com");

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        try {
            assertThrows(
                    com.ankita.mediumclone.exception.ForbiddenException.class,
                    () -> articleService.deleteArticle(1L)
            );

            verify(articleRepository, never()).delete(any(Article.class));
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }


    @Test
    void getAllArticles_shouldReturnPaginatedArticles() {
        User author = new User();
        author.setUsername("Ankita");

        Article article1 = new Article(
                "Java Basics",
                "Learn Java",
                author
        );

        Article article2 = new Article(
                "Spring Boot",
                "Learn Spring Boot",
                author
        );

        ReflectionTestUtils.setField(article1, "id", 1L);
        ReflectionTestUtils.setField(article2, "id", 2L);

        Pageable pageable = PageRequest.of(0, 2);

        Page<Article> articlePage = new PageImpl<>(
                List.of(article1, article2),
                pageable,
                5
        );

        when(articleRepository.findAll(pageable))
                .thenReturn(articlePage);

        Page<ArticleResponse> response =
                articleService.getAllArticles(pageable);

        assertEquals(2, response.getContent().size());
        assertEquals(5, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertEquals("Java Basics",
                response.getContent().get(0).getTitle());
        assertEquals("Spring Boot",
                response.getContent().get(1).getTitle());

        verify(articleRepository).findAll(pageable);
    }


    @Test
    void updateArticle_shouldUpdateArticleWhenUserIsOwner() {
        User author = new User();
        author.setUsername("Ankita");
        author.setEmail("ankita@gmail.com");

        Article article = new Article(
                "Old title",
                "Old content",
                author
        );

        ReflectionTestUtils.setField(article, "id", 1L);

        when(articleRepository.findById(1L))
                .thenReturn(Optional.of(article));

        when(articleRepository.save(any(Article.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        org.springframework.security.core.Authentication authentication =
                org.mockito.Mockito.mock(
                        org.springframework.security.core.Authentication.class
                );

        when(authentication.getName()).thenReturn("ankita@gmail.com");

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        try {
            com.ankita.mediumclone.dto.ArticleRequest request =
                    new com.ankita.mediumclone.dto.ArticleRequest();

            request.setTitle("Updated title");
            request.setContent("Updated content");

            ArticleResponse response =
                    articleService.updateArticle(1L, request);

            assertEquals("Updated title", response.getTitle());
            assertEquals("Updated content", response.getContent());
            assertEquals("Ankita", response.getAuthorName());

            verify(articleRepository).save(article);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }


    @Test
    void deleteArticle_shouldDeleteArticleWhenUserIsOwner() {
        User author = new User();
        author.setUsername("Ankita");
        author.setEmail("ankita@gmail.com");

        Article article = new Article(
                "Article to delete",
                "Some content",
                author
        );

        ReflectionTestUtils.setField(article, "id", 1L);

        when(articleRepository.findById(1L))
                .thenReturn(Optional.of(article));

        org.springframework.security.core.Authentication authentication =
                org.mockito.Mockito.mock(
                        org.springframework.security.core.Authentication.class
                );

        when(authentication.getName()).thenReturn("ankita@gmail.com");

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        try {
            articleService.deleteArticle(1L);

            verify(articleRepository).delete(article);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }


    @Test
    void deleteArticle_shouldThrowExceptionWhenArticleNotFound() {
        // Arrange
        when(articleRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ArticleNotFoundException.class,
                () -> articleService.deleteArticle(99L)
        );

        // Verify no deletion occurs
        verify(articleRepository).findById(99L);
        verify(articleRepository, never()).delete(any(Article.class));
    }



    @Test
    void createArticle_shouldCreateArticleForLoggedInUser() {
        User author = new User();
        author.setUsername("Ankita");
        author.setEmail("ankita@gmail.com");

        when(userRepository.findByEmail("ankita@gmail.com"))
                .thenReturn(Optional.of(author));

        when(articleRepository.save(any(Article.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        org.springframework.security.core.Authentication authentication =
                org.mockito.Mockito.mock(
                        org.springframework.security.core.Authentication.class
                );

        when(authentication.getName()).thenReturn("ankita@gmail.com");

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        try {
            com.ankita.mediumclone.dto.ArticleRequest request =
                    new com.ankita.mediumclone.dto.ArticleRequest();

            request.setTitle("My First Article");
            request.setContent("Learning Spring Boot step by step.");

            ArticleResponse response = articleService.createArticle(request);

            assertEquals("My First Article", response.getTitle());
            assertEquals(
                    "Learning Spring Boot step by step.",
                    response.getContent()
            );
            assertEquals("Ankita", response.getAuthorName());

            verify(userRepository).findByEmail("ankita@gmail.com");
            verify(articleRepository).save(any(Article.class));
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }


    @Test
    void getAllArticles_shouldPassSortingToRepository() {
        Pageable pageable = PageRequest.of(
                0,
                2,
                Sort.by(Sort.Direction.ASC, "title")
        );

        when(articleRepository.findAll(pageable))
                .thenReturn(Page.empty(pageable));

        Page<ArticleResponse> response =
                articleService.getAllArticles(pageable);

        assertTrue(response.getContent().isEmpty());
        assertEquals(Sort.Direction.ASC,
                pageable.getSort().getOrderFor("title").getDirection());

        verify(articleRepository).findAll(pageable);
    }


    @Test
    void updateArticle_shouldThrowExceptionWhenArticleNotFound() {
        when(articleRepository.findById(99L))
                .thenReturn(Optional.empty());

        ArticleRequest request = new ArticleRequest();
        request.setTitle("Updated title");
        request.setContent("Updated content");

        assertThrows(
                ArticleNotFoundException.class,
                () -> articleService.updateArticle(99L, request)
        );

        verify(articleRepository).findById(99L);
        verify(articleRepository, never()).save(any(Article.class));
    }


    @Test
    void createArticle_shouldThrowExceptionWhenUserNotFound() {
        // Arrange: simulate an authenticated user who isn't in the database
        org.springframework.security.core.Authentication authentication =
                mock(org.springframework.security.core.Authentication.class);

        when(authentication.getName()).thenReturn("missing@example.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        ArticleRequest request = new ArticleRequest();
        request.setTitle("Test article");
        request.setContent("Test content");

        try {
            // Act & Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> articleService.createArticle(request)
            );

            assertEquals("User not found", exception.getMessage());

            // No article should be saved if the author doesn't exist
            verify(articleRepository, never()).save(any(Article.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }










}
