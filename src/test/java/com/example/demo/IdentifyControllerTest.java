package com.example.demo;

import com.example.demo.models.Contact;
import com.example.demo.models.dto.IdentifyRequestDto;
import com.example.demo.models.dto.IdentifyResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class IdentifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IdentifyService identifyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testIdentifyContact_withValidPhoneAndEmail() throws Exception {
        IdentifyRequestDto request = new IdentifyRequestDto("test@example.com", "1234567890");

        Contact primary = new Contact();
        primary.setId(1);
        primary.setEmail("test@example.com");
        primary.setPhoneNumber("1234567890");

        Contact secondary = new Contact();
        secondary.setId(2);
        secondary.setEmail("alt@example.com");
        secondary.setPhoneNumber("9999999999");

        Mockito.when(identifyService.identify(any()))
                .thenReturn(List.of(primary, secondary));

        mockMvc.perform(post("/identify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contact.primaryContactId").value(1))
                .andExpect(jsonPath("$.contact.emails[0]").value("test@example.com"))
                .andExpect(jsonPath("$.contact.phoneNumbers[0]").value("1234567890"))
                .andExpect(jsonPath("$.contact.secondaryContactIds[0]").value(2));
    }

    @Test
    void testIdentifyContact_withEmptyJson() throws Exception {
        IdentifyResponseDto defaultResponse = IdentifyResponseDto.DEFAULT;

        mockMvc.perform(post("/identify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(defaultResponse)));
    }

}