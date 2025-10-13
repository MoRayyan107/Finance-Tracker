package com.rayyan.finance_tracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayyan.finance_tracker.entity.Savings;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rayyan.finance_tracker.service.SavingsService;
import com.rayyan.finance_tracker.service.UserDetailService;

import java.math.BigDecimal;
import java.util.List;

import static com.rayyan.finance_tracker.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Unit tests for SavingsController.
 * Tests CRUD operations, validation, and business logic for savings management.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = VALID_USERNAME, password = VALID_PASSWORD)
@DisplayName("SavingsController Unit Tests")
public class SavingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SavingsService savingsService;

    @MockitoBean
    private UserDetailService userDetailService;

    private User user;
    private Savings validSavings;
    private Savings invalidSavings;
    private Savings updatingSavings;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();

        // Mock the userDetailService to return the user when getUserByUsername is called
        when(userDetailService.getUserByUsername(VALID_USERNAME)).thenReturn(user);

        // make valid savings object
        validSavings = Savings.builder()
                .id(1L)
                .savingsName("Vacation Fund")
                .savingsDescription("Savings for a trip to Hawaii")
                .targetAmount(BigDecimal.valueOf(2000.00))
                .currentAmount(BigDecimal.valueOf(2000.00))
                .status(Savings.SavingsStatus.COMPLETED)
                .user(user)
                .build();

        // Make Invalid Savings object, fields can be changed on each test
        invalidSavings = Savings.builder()
                .id(2L)
                .savingsName("Vacation Fund")
                .savingsDescription("Savings for a trip to Hawaii")
                .targetAmount(BigDecimal.valueOf(2000.00))
                .currentAmount(BigDecimal.valueOf(2000.00))
                .user(user)
                .build();
    }

    @Nested
    @DisplayName("Create Savings Tests")
    class CreateSavingsTests {

        @Test
        @DisplayName("Create Savings - Success")
        void createSavings_Success() throws Exception {
            // when
            when(savingsService.createSavings(validSavings)).thenReturn("Savings created successfully!");

            // then
            mockMvc.perform(post(CREATE_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validSavings)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Savings created successfully!"));

            // verify
            verify(savingsService, times(1)).createSavings(validSavings);
        }

        @Test
        @DisplayName("Create Savings - Validation Failure - Empty Name")
        void createSavings_ValidationFailure_EmptySavingsName() throws Exception {
            // given
            Savings invalidSavings = Savings.builder()
                    .id(2L)
                    .savingsName(" ") // empty name -> Validation fails
                    .savingsDescription("Savings for a trip to Hawaii")
                    .targetAmount(BigDecimal.valueOf(2000.00))
                    .currentAmount(BigDecimal.valueOf(2000.00))
                    .user(user)
                    .build();

            // when
            when(savingsService.createSavings(invalidSavings))
                    .thenThrow(new ValidationException("Savings name cannot be empty"));

            // then
            mockMvc.perform(post(CREATE_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidSavings)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Savings name cannot be empty"));

            // verify
            verify(savingsService, times(1)).createSavings(invalidSavings);
        }

        @Test
        @DisplayName("Create Savings - Validation Failure - Empty Description")
        void createSavings_ValidationFailure_EmptySavingsDescription() throws Exception {
            // when
            invalidSavings.setSavingsDescription(" "); // empty description -> Validation fails
            when(savingsService.createSavings(invalidSavings))
                    .thenThrow(new ValidationException("Savings description cannot be empty"));

            // perform
            mockMvc.perform(post(CREATE_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Savings description cannot be empty")
                    );

            // verify
            verify(savingsService, times(1)).createSavings(invalidSavings);

        }

        @Test
        @DisplayName("Create Savings - Validation Failure - Invalid Target Amount")
        void createSavings_ValidationFailure_InvalidTargetAmount() throws Exception {
            // when
            invalidSavings.setTargetAmount(BigDecimal.valueOf(-200.00)); // Negative Target Amount -> Validation fails
            when(savingsService.createSavings(invalidSavings))
                    .thenThrow(new ValidationException("Target must be a positive digit"));

            // perform
            mockMvc.perform(post(CREATE_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Target must be a positive digit")
                    );

            // verify
            verify(savingsService, times(1)).createSavings(invalidSavings);
        }

        @Test
        @DisplayName("Create Savings - Validation Failure - Invalid Current Amount")
        void createSavings_ValidationFailure_InvalidCurrentAmount() throws Exception {
            // when
            invalidSavings.setCurrentAmount(BigDecimal.valueOf(-200.00)); // Negative Current Amount -> Validation fails
            when(savingsService.createSavings(invalidSavings))
                    .thenThrow(new ValidationException("Current amount must be a positive number"));

            // perform
            mockMvc.perform(post(CREATE_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Current amount must be a positive number")
                    );

            // verify
            verify(savingsService, times(1)).createSavings(invalidSavings);
        }

    }

    @Nested
    @DisplayName("Fetch Savings Tests")
    class FetchSavingsTest {

        @Test
        @DisplayName("Fetch All Savings - Success")
        void fetchAllSavings_Success() throws Exception {
            // when
            when(savingsService.findAllSavings(user)).thenReturn(List.of(validSavings));

            // then
            mockMvc.perform(get(GET_ALL_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$[0].savingsName").value("Vacation Fund"),
                            jsonPath("$[0].savingsDescription").value("Savings for a trip to Hawaii"),
                            jsonPath("$[0].targetAmount").value(2000.00),
                            jsonPath("$[0].currentAmount").value(2000.00)
                    );

            // verify
            verify(savingsService, times(1)).findAllSavings(user);
        }

        @Test
        @DisplayName("Fetch All Savings - No Savings Found")
        void fetchAllSavings_NoSavingsFound() throws Exception {
            // when
            when(savingsService.findAllSavings(user)).thenReturn(List.of()); // empty list

            // perform
            mockMvc.perform(get(GET_ALL_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]")); // expect empty array

            // verify
            verify(savingsService, times(1)).findAllSavings(user);
        }

        @Test
        @DisplayName("Fetch Savings By ID - Success")
        void fetchSavingsById_Success() throws Exception {
            // when
            when(savingsService.findSavingsByIdAndUser(1L, user)).thenReturn(validSavings);

            // perform
            mockMvc.perform(get(GET_SAVING_BY_ID_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$.savingsName").value("Vacation Fund"),
                            jsonPath("$.savingsDescription").value("Savings for a trip to Hawaii"),
                            jsonPath("$.targetAmount").value(2000.00),
                            jsonPath("$.currentAmount").value(2000.00)
                    );

            // verify
            verify(savingsService, times(1)).findSavingsByIdAndUser(1L, user);
        }

        @Test
        @DisplayName("Fetch Savings By ID - Savings Not Found")
        void fetchSavingsById_SavingsNotFound() throws Exception {
            // when
            when(savingsService.findSavingsByIdAndUser(1L, user))
                    .thenThrow(new ValidationException("Savings not found for user: " + user.getUsername()));

            // perform
            mockMvc.perform(get(GET_SAVING_BY_ID_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Savings not found for user: " + user.getUsername()));

            // verify
            verify(savingsService, times(1)).findSavingsByIdAndUser(1L, user);
        }
    }

    @Nested
    @DisplayName("Update Savings Tests")
    class UpdateSavingsTests {

        @Test
        @DisplayName("Update Savings - Success")
        void updateSavings_Success() throws Exception {
            // given
            updatingSavings = Savings.builder()
                    .savingsName("New Vacation Fund")
                    .savingsDescription("New Savings for a trip to Bali")
                    .currentAmount(BigDecimal.valueOf(200.00)) // current amount should be ignored during update
                    .targetAmount(BigDecimal.valueOf(3000.00))
                    .build();

            // when
            when(savingsService.updateSavings(1L, updatingSavings, user))
                    .thenReturn("Savings updated successfully!");

            // then
            mockMvc.perform(put(UPDATE_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatingSavings)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Savings updated successfully!"));

            // verify
            verify(savingsService, times(1)).updateSavings(1L, updatingSavings, user);
        }

        @Test
        @DisplayName("Update Savings - Validation Failure - Empty Name")
        void updateSavings_ValidationFailure_EmptySavingsName() throws Exception {
            // given
            updatingSavings = Savings.builder()
                    .savingsName(" ") // empty name -> Validation fails

                    .savingsDescription("New Savings for a trip to Bali")
                    .currentAmount(BigDecimal.valueOf(200.00)) // current amount should be ignored during update
                    .targetAmount(BigDecimal.valueOf(3000.00))
                    .build();

            // When
            when(savingsService.updateSavings(1L, updatingSavings, user))
                    .thenThrow(new ValidationException("Savings name cannot be empty"));

            // perform
            mockMvc.perform(put(UPDATE_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatingSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Savings name cannot be empty")
                    );

            // verify
            verify(savingsService, times(1)).updateSavings(1L, updatingSavings, user);
        }

        @Test
        @DisplayName("Update Savings - Validation Failure - Empty Description")
        void updateSavings_ValidationFailure_EmptySavingsDescription() throws Exception {
            // given
            updatingSavings = Savings.builder()
                    .savingsName("New Vacation Fund")
                    .savingsDescription(" ") // empty description -> Validation fails

                    .currentAmount(BigDecimal.valueOf(200.00)) // current amount should be ignored during update
                    .targetAmount(BigDecimal.valueOf(3000.00))
                    .build();

            // when
            when(savingsService.updateSavings(1L, updatingSavings, user))
                    .thenThrow(new ValidationException("Savings description cannot be empty"));

            // perform
            mockMvc.perform(put(UPDATE_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatingSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Savings description cannot be empty")
                    );

            // verify
            verify(savingsService, times(1)).updateSavings(1L, updatingSavings, user);
        }

        @Test
        @DisplayName("Update Savings - Validation Failure - Invalid Target Amount")
        void updateSavings_ValidationFailure_InvalidTargetAmount() throws Exception {
            // given
            updatingSavings = Savings.builder()
                    .savingsName("New Vacation Fund")
                    .savingsDescription("New Savings for a trip to Bali")
                    .currentAmount(BigDecimal.valueOf(200.00)) // current amount should be ignored during update
                    .targetAmount(BigDecimal.valueOf(-3000.00)) // Negative Target Amount -> Validation fails
                    .build();

            // when
            when(savingsService.updateSavings(1L, updatingSavings, user))
                    .thenThrow(new ValidationException("Savings Target Amount cannot have Negative amount"));

            // perform
            mockMvc.perform(put(UPDATE_SAVINGS_API, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatingSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Savings Target Amount cannot have Negative amount")
                    );

            // verify
            verify(savingsService, times(1)).updateSavings(1L, updatingSavings, user);
        }

        @Test
        @DisplayName("Update Savings - Validation Failure - No updates made")
        void updateSavings_ValidationFailure_NoUpdatesMade() throws Exception {
            // when
            updatingSavings = Savings.builder().build(); // empty object

            when(savingsService.updateSavings(1L, updatingSavings, user))
                    .thenThrow(new ValidationException("No fields provided to update"));

            // perform
            mockMvc.perform(put(UPDATE_SAVINGS_API, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatingSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("No fields provided to update")
                    );

            // verify
            verify(savingsService, times(1)).updateSavings(1L, updatingSavings, user);
        }

        @Test
        @DisplayName("Update Savings - Savings Not Found")
        void updateSavings_SavingsNotFound() throws Exception {
            // when
            updatingSavings = Savings.builder().build(); // empty object

            when(savingsService.updateSavings(1L, updatingSavings, user))
                    .thenThrow(new ValidationException("Savings Not Found"));

            // perform
            mockMvc.perform(put(UPDATE_SAVINGS_API, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatingSavings)))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").value("Savings Not Found")
                    );

            // verify
            verify(savingsService, times(1)).updateSavings(1L, updatingSavings, user);
        }
    }

    @Nested
    @DisplayName("Delete Savings Tests")
    class DeleteSavingsTests {

        @Test
        @DisplayName("Delete Savings - Success")
        void deleteSavings_Success() throws Exception {
            // when
            when(savingsService.deleteSavings(1L, user)).thenReturn("Savings deleted successfully!");

            // perform
            mockMvc.perform(delete(DELETE_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Savings deleted successfully!"));

            // verify
            verify(savingsService, times(1)).deleteSavings(1L, user);
        }

        @Test
        @DisplayName("Delete Savings - Savings Not Found")
        void deleteSavings_SavingsNotFound() throws Exception {
            // when
            when(savingsService.deleteSavings(1L, user))
                    .thenThrow(new ValidationException("Savings not found for user: " + user.getUsername()));

            // perform
            mockMvc.perform(delete(DELETE_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Savings not found for user: " + user.getUsername()));

            // verify
            verify(savingsService, times(1)).deleteSavings(1L, user);
        }
    }

    @Nested
    @DisplayName("Add Funds and Withdraw Funds from a Saving Tests")
    class AddWithdrawFundsTests {

        @Test
        @DisplayName("Deposit Funds - Success")
        void depositFunds_Success() throws Exception {
            // when
            when(savingsService.depositToSavings(eq(1L), any(BigDecimal.class), eq(user)))
                    .thenReturn("Deposited amount to Saving");

            // perform
            mockMvc.perform(post(DEPOSIT_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\": 500.00}"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Deposited amount to Saving"));

            // verify
            verify(savingsService, times(1)).depositToSavings(eq(1L), any(BigDecimal.class), eq(user));
        }

        @Test
        @DisplayName("Deposit Funds - Validation Failure - Invalid Amount")
        void depositFunds_ValidationFailure_InvalidAmount() throws Exception {
            // given - mock to throw exception for any negative amount
            when(savingsService.depositToSavings(eq(1L), any(BigDecimal.class), eq(user)))
                    .thenThrow(new ValidationException("Amount must be greater than zero"));

            // perform
            mockMvc.perform(post(DEPOSIT_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\": -500.00}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Amount must be greater than zero"));

            // verify
            verify(savingsService, times(1)).depositToSavings(eq(1L), any(BigDecimal.class), eq(user));
        }

        @Test
        @DisplayName("Withdraw Funds - Success")
        void withdrawFunds_Success() throws Exception {
            // when
            when(savingsService.withdrawFromSavings(eq(1L), any(BigDecimal.class), eq(user)))
                    .thenReturn("Withdrawal from a Saving!");

            // perform
            mockMvc.perform(post(WITHDRAW_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\": 300.00}"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Withdrawal from a Saving!"));

            // verify
            verify(savingsService, times(1)).withdrawFromSavings(eq(1L), any(BigDecimal.class), eq(user));
        }

        @Test
        @DisplayName("Withdraw Funds - Validation Failure - Negative Amount")
        void withdrawFunds_ValidationFailure_NegativeAmount() throws Exception {
            // given - mock to throw exception for any negative amount
            when(savingsService.withdrawFromSavings(eq(1L), any(BigDecimal.class), eq(user)))
                    .thenThrow(new ValidationException("Amount must be greater than zero"));

            // perform
            mockMvc.perform(post(WITHDRAW_SAVINGS_API, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\": -300.00}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Amount must be greater than zero"));

            // verify
            verify(savingsService, times(1)).withdrawFromSavings(eq(1L), any(BigDecimal.class), eq(user));
        }

        @Test
        @DisplayName("Withdraw Funds - Validation Failure - Exceeds Current Amount")
        void withdrawFunds_ValidationFailure_ExceedsCurrentAmount() throws Exception {
            // given - mock to throw exception for excessive withdrawal
            when(savingsService.withdrawFromSavings(eq(1L), any(BigDecimal.class), eq(user)))
                    .thenThrow(new ValidationException("Withdrawal amount exceeds current savings amount"));

            // perform
            mockMvc.perform(post(WITHDRAW_SAVINGS_API, 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"amount\": 3000.00}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Withdrawal amount exceeds current savings amount"));

            // verify
            verify(savingsService, times(1)).withdrawFromSavings(eq(1L), any(BigDecimal.class), eq(user));
        }
    }

    @Nested
    @DisplayName("Get Savings Progress Tests")
    class GetSavingsProgressTests {

        @Test
        @DisplayName("Get Savings In-Progress - Success")
        void getInProgressSavings_Success() throws Exception {
            // when
            validSavings.setCurrentAmount(BigDecimal.valueOf(200.00)); // set current amount less than target amount to be in-progress
            validSavings.setStatus(Savings.SavingsStatus.IN_PROGRESS); // set status to IN_PROGRESS
            when(savingsService.getSavingsInProgress(user)).thenReturn(List.of(validSavings));

            // perform
            mockMvc.perform(get(GET_INPROGRESS_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$[0].savingsName").value("Vacation Fund"),
                            jsonPath("$[0].savingsDescription").value("Savings for a trip to Hawaii"),
                            jsonPath("$[0].targetAmount").value(2000.00),
                            jsonPath("$[0].currentAmount").value(200.00),
                            jsonPath("$[0].status").value("IN_PROGRESS") // should be in-progress
                    );

            // verify
            verify(savingsService, times(1)).getSavingsInProgress(user);
        }

        @Test
        @DisplayName("Get Savings In-Progress - No Savings Found")
        void getInProgressSavings_NoSavingsFound() throws Exception {
            // when
            when(savingsService.getSavingsInProgress(user))
                    .thenThrow(new ValidationException("No savings found for user: " + user.getUsername()));

            // perform
            mockMvc.perform(get(GET_INPROGRESS_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("No savings found for user: " + user.getUsername()));

            // verify
            verify(savingsService, times(1)).getSavingsInProgress(user);
        }

        @Test
        @DisplayName("Get Savings Completed - Success")
        void getCompletedSavings_Success() throws Exception {
            // when
            when(savingsService.getSavingsCompleted(user)).thenReturn(List.of(validSavings));

            // perform
            mockMvc.perform(get(GET_COMPLETED_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$[0].savingsName").value("Vacation Fund"),
                            jsonPath("$[0].savingsDescription").value("Savings for a trip to Hawaii"),
                            jsonPath("$[0].targetAmount").value(2000.00),
                            jsonPath("$[0].currentAmount").value(2000.00),
                            jsonPath("$[0].status").value("COMPLETED") // should be completed
                    );

            // verify
            verify(savingsService, times(1)).getSavingsCompleted(user);
        }

        @Test
        @DisplayName("Get Savings Completed - No Savings Found")
        void getCompletedSavings_NoSavingsFound() throws Exception {
            // when
            when(savingsService.getSavingsCompleted(user))
                    .thenThrow(new ValidationException("No savings found for user: " + user.getUsername()));

            // perform
            mockMvc.perform(get(GET_COMPLETED_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("No savings found for user: " + user.getUsername()));

            // verify
            verify(savingsService, times(1)).getSavingsCompleted(user);
        }
    }

    @Nested
    @DisplayName("Get Total Savings Amount Tests")
    class GetTotalSavingsAmountTests {

        @Test
        @DisplayName("Get Total Savings Amount - Success")
        void getTotalSavingsAmount_Success() throws Exception {
            // when
            var additionalSavings = Savings.builder()
                    .id(2L)
                    .savingsName("Emergency Fund")
                    .savingsDescription("Savings for emergencies")
                    .targetAmount(BigDecimal.valueOf(3000.00))
                    .currentAmount(BigDecimal.valueOf(3000.00))
                    .status(Savings.SavingsStatus.COMPLETED)
                    .user(user)
                    .build();

            when(savingsService.getTotalSavings(user)).thenReturn(BigDecimal.valueOf(8000.00)); // 2000 + 3000 = 5000

            // perform
            mockMvc.perform(get(GET_TOTAL_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("8000.0"));

            // verify
            verify(savingsService, times(1)).getTotalSavings(user);
        }

        @Test
        @DisplayName("Get Total Savings Amount - No Savings Found")
        void getTotalSavingsAmount_NoSavingsFound() throws Exception {
            // when
            when(savingsService.getTotalSavings(user)).thenReturn(BigDecimal.valueOf(0.00));

            // perform
            mockMvc.perform(get(GET_TOTAL_SAVINGS_API)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0.0"));

            // verify
            verify(savingsService, times(1)).getTotalSavings(user);
        }
    }
}
