
package com.ankita.mediumclone.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterLoginAndAccessProtectedProfile()
            throws Exception {

        String uniqueId = UUID.randomUUID().toString()
                .substring(0, 8);

        String username = "testuser" + uniqueId;
        String email = "test" + uniqueId + "@example.com";
        String password = "TestPassword123!";

        // Step 1: Register a new user
        String registrationJson = objectMapper.writeValueAsString(
                Map.of(
                        "username", username,
                        "email", email,
                        "password", password
                )
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        // Step 2: Log in and obtain the JWT token
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
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String loginResponse = loginResult.getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse)
                .get("token")
                .asString();

        // Step 3: Access the protected profile using the JWT
        mockMvc.perform(get("/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "You are authenticated!"
                ));
    }


    @Test
    void shouldRejectLoginWithIncorrectPassword() throws Exception {

        String uniqueId = UUID.randomUUID().toString()
                .substring(0, 8);

        String email = "wrongpass" + uniqueId + "@example.com";
        String password = "CorrectPassword123!";

        // Register a user
        String registrationJson = objectMapper.writeValueAsString(
                Map.of(
                        "username", "user" + uniqueId,
                        "email", email,
                        "password", password
                )
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isOk());

        // Attempt login with an incorrect password
        String loginJson = objectMapper.writeValueAsString(
                Map.of(
                        "email", email,
                        "password", "WrongPassword123!"
                )
        );

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().is4xxClientError());
    }

}
