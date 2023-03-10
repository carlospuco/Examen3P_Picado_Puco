package com.banquito.core.productsaccounts.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.ProductAccount;
import com.banquito.core.productsaccounts.repository.ProductAccountRepository;
import com.banquito.core.productsaccounts.service.ProductAccountService;

public class ProductsAccountsServiceTest {
    @Mock
    private ProductAccountRepository productAccountRepository;

    @InjectMocks
    private ProductAccountService productAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAllActives() {
        // Given
        ProductAccount account1 = new ProductAccount();
        account1.setId("asd123");
        account1.setName("account1");
        account1.setDescription("description1");
        account1.setMinimunBalance(new BigDecimal("150.00"));
        account1.setPayInterest("Y");
        account1.setAcceptsChecks("Y");
        account1.setState("ACT");
        account1.setCreationDate(new Date());

        ProductAccount account2 = new ProductAccount();
        account2.setId("zxc123");
        account2.setName("account2");
        account2.setDescription("description2");
        account2.setMinimunBalance(new BigDecimal("400.00"));
        account2.setPayInterest("N");
        account2.setAcceptsChecks("N");
        account2.setState("ACT");
        account2.setCreationDate(new Date());

        List<ProductAccount> expectedAccounts = Arrays.asList(account1, account2);
        when(productAccountRepository.findByState("ACT")).thenReturn(expectedAccounts);

        // When
        List<ProductAccount> actualAccounts = productAccountService.listAllActives();

        // Then
        verify(productAccountRepository, times(1)).findByState("ACT");
        assertEquals(expectedAccounts, actualAccounts);
    }

    @Test
    void testObtainById() {
        // Given
        String id = "asd123";
        ProductAccount expectedAccount = new ProductAccount();
        expectedAccount.setId(id);
        expectedAccount.setName("account1");
        expectedAccount.setDescription("description1");
        expectedAccount.setMinimunBalance(new BigDecimal("800.00"));
        expectedAccount.setPayInterest("Y");
        expectedAccount.setAcceptsChecks("Y");
        expectedAccount.setState("ACT");
        expectedAccount.setCreationDate(new Date());

        when(productAccountRepository.findById(id)).thenReturn(Optional.of(expectedAccount));

        // When
        ProductAccount actualAccount = productAccountService.obtainById(id);

        // Then
        verify(productAccountRepository, times(1)).findById(id);
        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void testObtainByIdThrowsException() {
        // Given
        String id = "asd123";
        when(productAccountRepository.findById(id)).thenReturn(Optional.empty());

        // When
        CRUDException exception = assertThrows(CRUDException.class, () -> productAccountService.obtainById(id));

        // Then
        verify(productAccountRepository, times(1)).findById(id);
        assertEquals("Product Account with id: {asd123} does not exist", exception.getMessage());

    }

    @Test
    void testCreate() {
        // Given
        ProductAccount productAccount = new ProductAccount();
        productAccount.setId("asd123");
        productAccount.setName("account1");
        productAccount.setDescription("description1");
        productAccount.setMinimunBalance(new BigDecimal("150.00"));
        productAccount.setPayInterest("Y");
        productAccount.setAcceptsChecks("Y");
        productAccount.setState("ACT");

        // When
        productAccountService.create(productAccount);

        // Then
        verify(productAccountRepository, times(1)).save(productAccount);
        assertNotNull(productAccount.getCreationDate());
    }

    @Test
    void testCreateThrowsException() {
        // Given
        ProductAccount productAccount = new ProductAccount();
        productAccount.setId("asd123");
        productAccount.setName("account1");
        productAccount.setDescription("description1");
        productAccount.setMinimunBalance(new BigDecimal("150.00"));
        productAccount.setPayInterest("Y");
        productAccount.setAcceptsChecks("Y");
        productAccount.setState("ACT");

        doThrow(new RuntimeException("Database connection error")).when(productAccountRepository).save(productAccount);

        // When
        CRUDException exception = assertThrows(CRUDException.class, () -> productAccountService.create(productAccount));

        // Then
        verify(productAccountRepository, times(1)).save(productAccount);
        assertEquals("Product Account cannot be created, error:Database connection error", exception.getMessage());

    }

}