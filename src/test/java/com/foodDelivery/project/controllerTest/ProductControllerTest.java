package com.foodDelivery.project.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodDelivery.project.domen.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
public class ProductControllerTest {
    private String token;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        token = getToken();
    }
    //andExpect()
//    @Test
    private String getToken() throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new LoginRequest("ADMIN", "password123")))
                ).andReturn().getResponse().getContentAsString();
    }
}
