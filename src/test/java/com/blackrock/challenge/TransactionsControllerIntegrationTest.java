package com.blackrock.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Test type: Integration
 * Validation: Health, root, transactions:parse, transactions:validator (wage validation)
 * Command: mvn test -Dtest=TransactionsControllerIntegrationTest
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionsControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void healthReturnsOk() throws Exception {
        mvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void rootReturnsApiInfo() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Blackrock Challenge API"))
                .andExpect(jsonPath("$.basePath").value("/blackrock/challenge/v1"));
    }

    @Test
    void parseAcceptsExpenses() throws Exception {
        String body = "{\"expenses\":[{\"date\":\"2023-10-12 20:15:00\",\"amount\":250}]}";
        mvc.perform(post("/blackrock/challenge/v1/transactions:parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].ceiling").value(300))
                .andExpect(jsonPath("$.transactions[0].remanent").value(50));
    }

    @Test
    void validatorRequiresValidWage() throws Exception {
        String body = "{\"wage\":-1,\"transactions\":[]}";
        mvc.perform(post("/blackrock/challenge/v1/transactions:validator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
