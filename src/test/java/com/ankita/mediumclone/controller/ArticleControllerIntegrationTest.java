
package com.ankita.mediumclone.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.JsonNode;

import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRejectRequestWithoutJwtToken() throws Exception {

        int responseStatus = mockMvc
                .perform(get("/articles"))
                .andReturn()
                .getResponse()
                .getStatus();

        assertTrue(
                responseStatus == 401 || responseStatus == 403,
                "Expected 401 or 403, but received: " + responseStatus
        );
    }


    @Test
    void shouldCreateArticleWithValidJwt() throws Exception {

        String uniqueId = UUID.randomUUID().toString()
                .substring(0, 8);

        String email = "author" + uniqueId + "@example.com";
        String password = "TestPassword123!";

        // Register a user
        String registrationJson = objectMapper.writeValueAsString(
                Map.of(
                        "username", "author" + uniqueId,
                        "email", email,
                        "password", password
                )
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isOk());

        // Log in and obtain the JWT
        String loginJson = objectMapper.writeValueAsString(
                Map.of(
                        "email", email,
                        "password", password
                )
        );

        MvcResult loginResult = mockMvc.perform(
                        post("/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse)
                .get("token")
                .asString();

        // Create an article using the JWT
        String articleJson = objectMapper.writeValueAsString(
                Map.of(
                        "title", "Integration Test Article",
                        "content", "Testing article creation through HTTP."
                )
        );

        mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Integration Test Article"))
                .andExpect(jsonPath("$.content")
                        .value("Testing article creation through HTTP."));
    }

    @Test
    void shouldRejectArticleCreationWithoutJwtToken() throws Exception {

        String articleJson = """
            {
                "title": "Unauthorized Article",
                "content": "This article should not be created."
            }
            """;

        int responseStatus = mockMvc.perform(
                        post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(articleJson))
                .andReturn()
                .getResponse()
                .getStatus();

        assertTrue(
                responseStatus == 401 || responseStatus == 403,
                "Expected 401 or 403, but received: " + responseStatus
        );
    }


    @Test
    void shouldRejectArticleWithBlankTitle() throws Exception {

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String email = "validation" + uniqueId + "@example.com";
        String password = "TestPassword123!";

        // Register a user
        String registrationJson = objectMapper.writeValueAsString(
                Map.of(
                        "username", "validation" + uniqueId,
                        "email", email,
                        "password", password
                )
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isOk());

        // Log in and obtain the JWT
        String loginJson = objectMapper.writeValueAsString(
                Map.of(
                        "email", email,
                        "password", password
                )
        );

        MvcResult loginResult = mockMvc.perform(
                        post("/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asString();

        // Attempt to create an article with a blank title
        String articleJson = objectMapper.writeValueAsString(
                Map.of(
                        "title", "   ",
                        "content", "This article has a valid content."
                )
        );

        mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRejectArticleWithBlankContent() throws Exception {

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String email = "validation" + uniqueId + "@example.com";
        String password = "TestPassword123!";

        String registrationJson = objectMapper.writeValueAsString(
                Map.of(
                        "username", "validation" + uniqueId,
                        "email", email,
                        "password", password
                )
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isOk());

        String loginJson = objectMapper.writeValueAsString(
                Map.of("email", email, "password", password)
        );

        MvcResult loginResult = mockMvc.perform(
                        post("/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asString();

        String articleJson = objectMapper.writeValueAsString(
                Map.of(
                        "title", "Valid Article Title",
                        "content", "   "
                )
        );

        mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnPaginatedArticles() throws Exception {

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String email = "pagination" + uniqueId + "@example.com";
        String password = "TestPassword123!";

        // Register a user
        String registrationJson = objectMapper.writeValueAsString(
                Map.of(
                        "username", "pagination" + uniqueId,
                        "email", email,
                        "password", password
                )
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isOk());

        // Log in and obtain the JWT
        String loginJson = objectMapper.writeValueAsString(
                Map.of("email", email, "password", password)
        );

        MvcResult loginResult = mockMvc.perform(
                        post("/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asString();

        // Request the first page of articles
        mockMvc.perform(get("/articles")
                        .param("page", "0")
                        .param("size", "2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(
                        org.hamcrest.Matchers.lessThanOrEqualTo(2)
                ))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }


    @Test
    void shouldReturnArticlesSortedByCreatedAtDescending() throws Exception {

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String email = "sorting" + uniqueId + "@example.com";
        String password = "TestPassword123!";

        // Register a user
        String registrationJson = objectMapper.writeValueAsString(
                Map.of(
                        "username", "sorting" + uniqueId,
                        "email", email,
                        "password", password
                )
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isOk());

        // Log in
        String loginJson = objectMapper.writeValueAsString(
                Map.of("email", email, "password", password)
        );

        MvcResult loginResult = mockMvc.perform(
                        post("/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asString();

        // Create the first article
        String firstArticleJson = objectMapper.writeValueAsString(
                Map.of(
                        "title", "Older Sorting Article",
                        "content", "This article is created first."
                )
        );

        mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstArticleJson))
                .andExpect(status().isOk());

        // Create the second article
        String secondArticleJson = objectMapper.writeValueAsString(
                Map.of(
                        "title", "Newer Sorting Article",
                        "content", "This article is created second."
                )
        );

        mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondArticleJson))
                .andExpect(status().isOk());

        // Request articles sorted newest first
        mockMvc.perform(get("/articles")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(
                        org.hamcrest.Matchers.greaterThanOrEqualTo(2)
                ))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Newer Sorting Article"))
                .andExpect(jsonPath("$.content[1].title")
                        .value("Older Sorting Article"));
    }





}
