package com.rayyan.finance_tracker.service;

import com.rayyan.finance_tracker.entity.Savings;
import com.rayyan.finance_tracker.entity.User;
import com.rayyan.finance_tracker.exceptions.InsufficientFundsException;
import com.rayyan.finance_tracker.exceptions.InvalidAmountException;
import com.rayyan.finance_tracker.exceptions.SavingsException;
import com.rayyan.finance_tracker.exceptions.ValidationException;
import com.rayyan.finance_tracker.repository.SavingsRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.rayyan.finance_tracker.TestConstants.VALID_PASSWORD;
import static com.rayyan.finance_tracker.TestConstants.VALID_USERNAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensures tests run in a specific order
@DisplayName("Savings Service Tests")
public class SavingsServiceTest {

    @Mock
    private SavingsRepository savingsRepository;

    @InjectMocks
    private SavingsService savingsService;

    private User user;
    private Savings validSavings;
    private Savings invalidSavings;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .role(User.Role.USER)
                .build();

        validSavings = Savings.builder()
                .id(1L)
                .savingsName("Dream Car")
                .savingsDescription("Saving to buy a Dream Car")
                .currentAmount(BigDecimal.valueOf(5000))
                .targetAmount(BigDecimal.valueOf(100000))
                .user(user)
                .status(Savings.SavingsStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();

        // their values will change's in individual tests
        invalidSavings = Savings.builder()
                .id(2L)
                .savingsName("Dream Car")
                .savingsDescription("Valid description")
                .currentAmount(BigDecimal.valueOf(100))
                .targetAmount(BigDecimal.valueOf(1000))
                .user(user)
                .status(Savings.SavingsStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Create Savings Tests")
    class CreateSavingsTests {

        @Test
        @DisplayName("Should create savings successfully with valid data")
        void createSavings_Valid_Success() {
            when(savingsRepository.save(any(Savings.class))).thenReturn(validSavings);

            String result = savingsService.createSavings(validSavings);

            assertEquals("Savings created successfully!", result);
            verify(savingsRepository, times(1)).save(validSavings);
        }

        @Test
        @DisplayName("Should throw ValidationException when savings name is empty")
        void createSavings_EmptyName_ThrowsValidationException() {
            invalidSavings.setSavingsName(""); // Empty name -> Error

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> savingsService.createSavings(invalidSavings));

            verify(savingsRepository, never()).save(any(Savings.class));
        }

        @Test
        @DisplayName("Should throw ValidationException when savings description is empty")
        void createSavings_EmptyDescription_ThrowsValidationException() {
            invalidSavings.setSavingsDescription(" "); // Empty description -> Error

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> savingsService.createSavings(invalidSavings), "Savings description cannot be empty");

            verify(savingsRepository, never()).save(any(Savings.class));
        }

        @Test
        @DisplayName("Should throw ValidationException when current amount is negative")
        void createSavings_NegativeCurrentAmount_ThrowsValidationException() {
            invalidSavings.setCurrentAmount(BigDecimal.valueOf(-100)); // Negative current amount -> Error

            assertThrows(ValidationException.class,
                    () -> savingsService.createSavings(invalidSavings),"Current amount cannot be negative");

            verify(savingsRepository, never()).save(any(Savings.class));
        }

        @Test
        @DisplayName("Should throw ValidationException when target amount is zero or negative")
        void createSavings_ZeroOrNegativeTargetAmount_ThrowsValidationException() {
            invalidSavings.setTargetAmount(BigDecimal.valueOf(0));

            assertThrows(ValidationException.class,
                    () -> savingsService.createSavings(invalidSavings), "Target must be a positive digit");

            verify(savingsRepository, never()).save(any(Savings.class));
        }

    }

    @Nested
    @DisplayName("Find Savings Tests")
    class FindSavingsTests {

        @Test
        @DisplayName("Finds all savings for a user")
        void findAllSavings_Success() {
            // Given
            when(savingsRepository.findByUser(user)).thenReturn(List.of(validSavings));

            // when
            List<Savings> result = savingsService.findAllSavings(user);

            // assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validSavings.getSavingsName(), result.get(0).getSavingsName());

            // verify
            verify(savingsRepository, times(1)).findByUser(user);
        }

        @Test
        @DisplayName("Finds all savings for a user - No Savings Found")
        void findAllSavings_NoSavingsFound() {
            // given
            when(savingsRepository.findByUser(user)).thenReturn(List.of());

            // when
            List<Savings> result = savingsService.findAllSavings(user);

            // assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // verify
            verify(savingsRepository, times(1)).findByUser(user);
        }

        @Test
        @DisplayName("Finds a Saving by its ID and User - Success")
        void findBySavingId_And_User_Success(){
            // given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));

            // when
            Savings savings = savingsService.findSavingsByIdAndUser(1L, user);

            // assert
            assertNotNull(savings);
            assertEquals(validSavings.getSavingsName(), savings.getSavingsName());
            assertEquals(validSavings.getId(), savings.getId());

            // verify
            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
        }

        @Test
        @DisplayName("Finds a Saving by its ID and User - Not Found")
        void findBySavingId_And_User_NotFound(){
            // given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

            // when & assert
            assertThrows(SavingsException.class, () -> savingsService.findSavingsByIdAndUser(1L, user),
                    "Savings not found for user: " + user.getUsername());

            // verify
            verify(savingsRepository, times(1)).findByIdAndUser(1L,user);
        }
    }

    @Nested
    @DisplayName("Delete Savings Tests")
    class DeleteSavingsTests {
        @Test
        @DisplayName("Should Deelete savings successfully")
        void deleteSavings_Success() {
            // Given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));
            doNothing().when(savingsRepository).delete(validSavings); // mock delete method

            // when
            String result = savingsService.deleteSavings(1L, user);

            // assert and verify
            assertEquals("Savings deleted successfully!", result);
            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, times(1)).delete(validSavings);
        }

        @Test
        @DisplayName("Should throw SavingsException when trying to delete non-existing savings")
        void deleteSavings_NonExistingSavings_ThrowsSavingsException() {
            // Given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

            // assert and verify
            assertThrows(SavingsException.class, () -> savingsService.deleteSavings(1L, user),
                    "Savings not found for user: " + user.getUsername());

            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, never()).delete(validSavings);
        }
    }

    @Nested
    @DisplayName("Update Savings Tests")
    class UpdateSavingsTests {

        @Test
        @DisplayName("Should Update savings successfully with valid data")
        void updateSavings_Valid_Success() {
            // given
            Savings updatedSavings = Savings.builder()
                    .savingsName("Updated Car")
                    .savingsDescription("Updated Description")
                    .targetAmount(BigDecimal.valueOf(150000))
                    .build();

            // mock the repository to return the existing savings
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(updatedSavings));
            when(savingsRepository.save(updatedSavings)).thenReturn(updatedSavings);

            // when
            String result = savingsService.updateSavings(1L, updatedSavings, user);

            // assert and verify
            assertEquals("Savings updated successfully!", result);
            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, times(1)).save(updatedSavings);
        }

        @Test
        @DisplayName("Should throw ValidationException when updating with empty name")
        void updateSavings_EmptyName_ThrowsValidationException() {
            Savings invalidUpdate = Savings.builder()
                    .savingsName("") // Empty name -> Error
                    .build();

            // mock the repository to return existing savings
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));

            // assert and verify
            assertThrows(ValidationException.class, () -> savingsService.updateSavings(1L, invalidUpdate, user),
                    "Savings name cannot be empty");
            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, never()).save(invalidUpdate);
        }

        @Test
        @DisplayName("Should throw ValidationException when updating with empty description")
        void updateSavings_EmptyDescription_ThrowsValidationException() {
            Savings InvalidSavings = Savings.builder()
                    .savingsDescription("") // Empty description -> Error
                    .build();

            // mock the repository to return existing savings
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));

            // assert and verify
            assertThrows(ValidationException.class, () -> savingsService.updateSavings(1L, InvalidSavings, user),
                    "Savings description cannot be empty");

            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, never()).save(InvalidSavings);
        }

        @Test
        @DisplayName("Should throw ValidationException when updating with zero or negative target amount")
        void updateSavings_ZeroOrNegativeTargetAmount_ThrowsValidationException() {
            Savings invalidSavings = Savings.builder()
                    .targetAmount(BigDecimal.valueOf(-300)) // Negative target amount -> Error
                    .build();

            // mock the repository to return existing savings
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));

            // assert and verify
            assertThrows(ValidationException.class, () -> savingsService.updateSavings(1L, invalidSavings, user),
                    "Target must be a positive digit");

            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, never()).save(invalidSavings);
        }

        @Test
        @DisplayName("Should throw ValidationException when no fields are provided to update")
        void updateSavings_NoFieldsToUpdate_ThrowsValidationException() {
            Savings invalidSavings = Savings.builder().build(); // No fields set -> Validation error

            // mcok the repository to return existing savings
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));

            // assert and verify
            assertThrows(ValidationException.class, () -> savingsService.updateSavings(1L, invalidSavings, user),
                    "No fields provided to update");

            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, never()).save(invalidSavings);
        }

        @Test
        @DisplayName("Should throw SavingsException when trying to update non-existing savings")
        void updateSavings_NonExistingSavings_ThrowsSavingsException() {
            Savings invalidSavings = Savings.builder().build();

            // mock the repository to return empty
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

            // assert and verify
            assertThrows(SavingsException.class, () -> savingsService.updateSavings(1L, invalidSavings, user),
                    "Savings not found for user: " + user.getUsername());

            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, never()).save(invalidSavings);
        }
    }

    @Nested
    @DisplayName("Deposit Into Savings Tests")
    class DepositIntoSavingsTests {

        @Test
        @DisplayName("Should deposit into Savings with valid data")
        void addToSavings_Valid_Success(){
            // Given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));
            when(savingsRepository.save(any(Savings.class))).thenReturn(validSavings);

            BigDecimal depositAmount = BigDecimal.valueOf(100);
            BigDecimal expectedAmount = validSavings.getCurrentAmount().add(depositAmount); // 5000 + 100 = 5100 (expected)

            String result = savingsService.depositToSavings(1L, depositAmount, user);

            // assert and verify
            assertEquals("Deposited amount to Saving",result);
            assertEquals(expectedAmount,validSavings.getCurrentAmount());

            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, times(1)).save(validSavings);
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when depositing negative amount")
        void addToSavings_NegativeAmount_ThrowsInvalidAmountException(){
            // Given
            BigDecimal negativeAmount = BigDecimal.valueOf(-100); // Negative amount -> Error
            when(savingsRepository.findByIdAndUser(1L, user)).thenThrow(new InvalidAmountException(("Amount must be greater than zero")));

            // assert and verify
            assertThrows(InvalidAmountException.class, () -> savingsService.depositToSavings(1L, negativeAmount, user),
                    "Amount must be greater than zero");

            verify(savingsRepository, times(1)).findByIdAndUser(1L, user);
            verify(savingsRepository, never()).save(validSavings);
        }

        @Test
        @DisplayName("Should throw SavingsException when trying to deposit to non-existing savings")
        void addToSavings_NonExistingSavings_ThrowsSavingsException(){
            // Given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());
            BigDecimal depositAmount = BigDecimal.valueOf(100);

            // assert and verify
            assertThrows(SavingsException.class, () -> savingsService.depositToSavings(1L,depositAmount, user),
                    "Savings not found for user: " + user.getUsername());

            verify(savingsRepository, times(1)).findByIdAndUser(1L,user);
            verify(savingsRepository, never()).save(any(Savings.class));
        }

    }

    @Nested
    @DisplayName("Withdraw From Savings Tests")
    class WithdrawFromSavingsTests {

        @Test
        @DisplayName("Should withdraw successfully with valid amount")
        void withdrawFromSavings_ValidAmount_Success() {
            // Given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(validSavings));
            when(savingsRepository.save(any(Savings.class))).thenReturn(validSavings);
            BigDecimal withdrawalAmount = BigDecimal.valueOf(300); // Valid amount

            // when
            String result = savingsService.withdrawFromSavings(1L, withdrawalAmount, user);

            // assert and verify
            assertEquals("Withdrawal from a Saving!", result);
            verify(savingsRepository, times(1)).findByIdAndUser(1L,user);
            verify(savingsRepository, times(1)).save(validSavings);
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when withdrawing negative amount")
        void withdrawFromSavings_NegativeAmount_ThrowsInvalidAmountException() {
            // Given
            BigDecimal negativeAmout = BigDecimal.valueOf(-300); // Negative amount -> Error
            when(savingsRepository.findByIdAndUser(1L, user)).thenThrow(new InvalidAmountException("Amount must be non-negative"));

            // assert and verify
            assertThrows(InvalidAmountException.class, () -> savingsService.withdrawFromSavings(1L, negativeAmout, user),
                    "Amount must be non-negative");

            verify(savingsRepository, times(1)).findByIdAndUser(1L,user);
            verify(savingsRepository, never()).save(any(Savings.class));
        }

        @Test
        @DisplayName("Should throw InsufficientFundsException when withdrawing more than current amount")
        void withdrawFromSavings_InsufficientFunds_ThrowsInsufficientFundsException() {
            // Given
            validSavings.setCurrentAmount(BigDecimal.valueOf(100)); // Current amount is 100
            when(savingsRepository.findByIdAndUser(1L, user)).thenThrow(new InsufficientFundsException("Withdrawal amount exceeds current savings amount"));

            BigDecimal withdrawalAmount = BigDecimal.valueOf(300); // More than current amount -> Error

            // assert and verify
            assertThrows(InsufficientFundsException.class, () -> savingsService.withdrawFromSavings(1L, withdrawalAmount, user),
                    "Withdrawal amount exceeds current savings amount");

            verify(savingsRepository, times(1)).findByIdAndUser(1L,user);
            verify(savingsRepository, never()).save(validSavings);
        }

        @Test
        @DisplayName("Should throw SavingsException when trying to withdraw from non-existing savings")
        void withdrawFromSavings_NonExistingSavings_ThrowsSavingsException() {
            // Given
            when(savingsRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());
            BigDecimal withdrawalAmount = BigDecimal.valueOf(300); // Valid amount

            // assert and verify
            assertThrows(SavingsException.class, () -> savingsService.withdrawFromSavings(1L, withdrawalAmount, user),
                    "Savings not found for user: " + user.getUsername());

            verify(savingsRepository, times(1)).findByIdAndUser(1L,user);
            verify(savingsRepository, never()).save(any(Savings.class));
        }
    }

    @Nested
    @DisplayName("Get Savings Total Amount Tests")
    class GetSavingsTotalAmountTests {
        @Test
        @DisplayName("Should get total savings amount successfully")
        void getTotalSavings_Success() {
            // Given
            when(savingsRepository.findByUser(user)).thenReturn(List.of(validSavings));

            // when
            BigDecimal result = savingsService.getTotalSavings(user);

            // assert and verify
            assertNotNull(result);
            assertEquals(validSavings.getCurrentAmount(), result);

            verify(savingsRepository, times(1)).findByUser(user);
        }

        // additional test case for two savings
        @Test
        @DisplayName("Should get total savings amount for multiple savings")
        void getTotalSavings_MultipleSavings_Success() {
            // Given
            Savings newSavings = Savings.builder()
                    .id(2L)
                    .savingsName("New House")
                    .savingsDescription("Saving to buy a new house")
                    .currentAmount(BigDecimal.valueOf(20000))
                    .targetAmount(BigDecimal.valueOf(500000))
                    .user(user)
                    .status(Savings.SavingsStatus.IN_PROGRESS)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(savingsRepository.findByUser(user)).thenReturn(List.of(validSavings, newSavings));
            BigDecimal totalAmountResult = validSavings.getCurrentAmount().add(newSavings.getCurrentAmount()); // 5000 + 20000 = 25000

            // when
            BigDecimal actualResult = savingsService.getTotalSavings(user);

            // assert and verify
            assertNotNull(actualResult);
            assertEquals(totalAmountResult, actualResult);

            verify(savingsRepository, times(1)).findByUser(user);
        }

        @Test
        @DisplayName("Should return zero when user has no savings")
        void getTotalSavings_NoSavings_ReturnsZero() {
            // Given
            when(savingsRepository.findByUser(user)).thenReturn(Collections.emptyList());

            // when
            BigDecimal result = savingsService.getTotalSavings(user);

            // assert and verify
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result);

            verify(savingsRepository, times(1)).findByUser(user);
        }
    }

    @Nested
    @DisplayName("Get Savings By Status Tests")
    class GetSavingsByStatusTests {

        @Test
        @DisplayName("Should get savings with Status IN_PROGRESS")
        void getSavingsInProgress_Success() {
            // Given
            validSavings.setStatus(Savings.SavingsStatus.IN_PROGRESS); // ensure status is IN_PROGRESS
            when(savingsRepository.findByUserAndStatus(user, Savings.SavingsStatus.IN_PROGRESS)).thenReturn(List.of(validSavings));

            // when
            List<Savings> result = savingsService.getSavingsInProgress(user);

            // assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validSavings.getStatus(), result.get(0).getStatus()); // should be IN_PROGRESS

            verify(savingsRepository, times(1)).findByUserAndStatus(user, Savings.SavingsStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should get savings with Status COMPLETED")
        void getSavingsCompleted_Success() {
            // Given
            validSavings.setStatus(Savings.SavingsStatus.COMPLETED); // set status to COMPLETED
            when(savingsRepository.findByUserAndStatus(user, Savings.SavingsStatus.COMPLETED)).thenReturn(List.of(validSavings));

            // when
            List<Savings> result = savingsService.getSavingsCompleted(user);

            // assert and verify
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validSavings.getStatus(), result.get(0).getStatus());

            verify(savingsRepository, times(1)).findByUserAndStatus(user, Savings.SavingsStatus.COMPLETED);
        }

        @Test
        @DisplayName("Should throw SavingsException when no savings found with Status IN_PROGRESS")
        void getSavingsInProgress_NoSavingsFound_ThrowsSavingsException() {
            // Given
            when(savingsRepository.findByUserAndStatus(user, Savings.SavingsStatus.IN_PROGRESS)).thenReturn(List.of()); // empty list

            // assert and verify
            assertThrows(SavingsException.class, () -> savingsService.getSavingsInProgress(user),
                    "No savings found for user: " + user.getUsername());

            verify(savingsRepository, times(1)).findByUserAndStatus(user, Savings.SavingsStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should throw SavingsException when no savings found with Status COMPLETED")
        void getSavingsCompleted_NoSavingsFound_ThrowsSavingsException() {
            // Given
            when(savingsRepository.findByUserAndStatus(user, Savings.SavingsStatus.COMPLETED)).thenReturn(List.of()); // empty list

            // assert and Verify
            assertThrows(SavingsException.class, () -> savingsService.getSavingsCompleted(user),
                    "No savings found for user: " + user.getUsername());

            verify(savingsRepository, times(1)).findByUserAndStatus(user, Savings.SavingsStatus.COMPLETED);
        }
    }

}
