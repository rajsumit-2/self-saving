package com.blackrock.challenge;

import com.blackrock.challenge.dto.ExpenseDto;
import com.blackrock.challenge.dto.ParseResponse;
import com.blackrock.challenge.service.ParseService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Test type: Unit
 * Validation: ParseService ceiling/remanent calculation; total remanent; multiple expenses
 * Command: mvn test -Dtest=ParseServiceTest
 */
class ParseServiceTest {

    private final ParseService parseService = new ParseService();

    @Test
    void parseReturnsFourTransactions() {
        List<ExpenseDto> expenses = List.of(
                new ExpenseDto(null, "2023-10-12 20:15:00", 250.0),
                new ExpenseDto(null, "2023-02-28 15:49:00", 375.0),
                new ExpenseDto(null, "2023-07-01 21:59:00", 620.0),
                new ExpenseDto(null, "2023-12-17 08:09:00", 480.0)
        );
        ParseResponse res = parseService.parse(expenses);
        assertThat(res.transactions()).hasSize(4);
        assertThat(res.totalRemanent()).isEqualTo(175);
    }

    @Test
    void ceiling100AndRemanent() {
        List<ExpenseDto> expenses = List.of(new ExpenseDto(null, "2023-01-01 00:00:00", 199.0));
        ParseResponse res = parseService.parse(expenses);
        assertThat(res.transactions()).hasSize(1);
        assertThat(res.transactions().get(0).ceiling()).isEqualTo(200);
        assertThat(res.transactions().get(0).remanent()).isEqualTo(1);
    }
}
