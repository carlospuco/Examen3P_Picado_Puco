package com.banquito.core.productsaccounts.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
import com.banquito.core.productsaccounts.model.InterestRate;
import com.banquito.core.productsaccounts.repository.InterestRateRepository;
import com.banquito.core.productsaccounts.service.InterestRateService;

public class InterestServiceTest {

    @Mock
    private InterestRateRepository interestRateRepository;

    @InjectMocks
    private InterestRateService interestRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAllActives() {
        // Given
        InterestRate rate1 = new InterestRate();
        rate1.setId(1);
        rate1.setName("rate1");
        rate1.setInterestRate(new BigDecimal("0.01"));
        rate1.setState("ACT");
        rate1.setStart(new Date());

        InterestRate rate2 = new InterestRate();
        rate2.setId(2);
        rate2.setName("rate2");
        rate2.setInterestRate(new BigDecimal("0.02"));
        rate2.setState("ACT");
        rate2.setStart(new Date());

        List<InterestRate> expectedRates = Arrays.asList(rate1, rate2);
        when(interestRateRepository.findByState("ACT")).thenReturn(expectedRates);

        // When
        List<InterestRate> actualRates = interestRateService.listAllActives();

        // Then
        verify(interestRateRepository, times(1)).findByState("ACT");
        assertEquals(expectedRates, actualRates);
    }

    @Test
    void testObtainById() {
        // Given
        Integer id = 1;
        InterestRate expectedRate = new InterestRate();
        expectedRate.setId(id);
        expectedRate.setName("rate1");
        expectedRate.setInterestRate(new BigDecimal("0.01"));
        expectedRate.setState("ACT");
        expectedRate.setStart(new Date());
        when(interestRateRepository.findById(id)).thenReturn(Optional.of(expectedRate));

        // When
        InterestRate actualRate = interestRateService.obtainById(id);

        // Then
        verify(interestRateRepository, times(1)).findById(id);
        assertEquals(expectedRate, actualRate);
    }

    @Test
    void testObtainByIdNotFound() {
        // Given
        Integer id = 1;
        when(interestRateRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(CRUDException.class, () -> {
            interestRateService.obtainById(id);
        });

        // Then
        verify(interestRateRepository, times(1)).findById(id);
        assertEquals("Interest Rate with id: {1} does not exist", exception.getMessage());
    }

    @Test
    void testCreate() {
        // Given
        InterestRate interestRate = new InterestRate();
        interestRate.setName("test");
        interestRate.setInterestRate(new BigDecimal("0.01"));
        interestRate.setState("ACT");
        interestRate.setStart(new Date());

        // When
        doReturn(interestRate).when(interestRateRepository).save(any(InterestRate.class));
        interestRateService.create(interestRate);

        // Then
        verify(interestRateRepository, times(1)).save(interestRate);
    }

    @Test
    void testCreateException() {
        // Given
        InterestRate interestRate = new InterestRate();
        interestRate.setName("test");
        interestRate.setInterestRate(new BigDecimal("0.01"));
        interestRate.setState("ACT");
        interestRate.setStart(new Date());

        // Mocking the repository to throw an exception when saving
        doThrow(new RuntimeException("Failed to save")).when(interestRateRepository).save(any(InterestRate.class));

        // When and Then
        CRUDException exception = assertThrows(CRUDException.class, () -> interestRateService.create(interestRate));

        assertEquals(510, exception.getErrorCode());
        assertEquals("Interest Rate cannot be created, error:Failed to save", exception.getMessage());
    }

    @Test
    void testUpdate() throws CRUDException {
        // Given
        Integer id = 1;
        InterestRate interestRate = new InterestRate();
        interestRate.setName("test");
        interestRate.setInterestRate(new BigDecimal("0.02"));
        interestRate.setState("ACT");
        interestRate.setStart(new Date());
        InterestRate expectedRate = new InterestRate();
        expectedRate.setId(id);
        expectedRate.setName("rate1");
        expectedRate.setInterestRate(new BigDecimal("0.01"));
        expectedRate.setState("ACT");
        expectedRate.setStart(new Date());
        when(interestRateRepository.findById(id)).thenReturn(Optional.of(expectedRate));
        when(interestRateRepository.save(any(InterestRate.class))).thenReturn(interestRate);

        // When
        interestRateService.update(id, interestRate);

        // Then
        verify(interestRateRepository, times(1)).findById(id);
        verify(interestRateRepository, times(1)).save(any(InterestRate.class));
        assertEquals(interestRate.getName(), expectedRate.getName());
        assertEquals(interestRate.getInterestRate(), expectedRate.getInterestRate());
        assertEquals(interestRate.getState(), expectedRate.getState());
        assertEquals(interestRate.getStart(), expectedRate.getStart());
    }

    @Test
    void testInactivate() {
        // Given
        Integer id = 1;
        InterestRate expectedRate = new InterestRate();
        expectedRate.setId(id);
        expectedRate.setName("rate1");
        expectedRate.setInterestRate(new BigDecimal("0.01"));
        expectedRate.setState("ACT");
        expectedRate.setStart(new Date());
        when(interestRateRepository.findById(id)).thenReturn(Optional.of(expectedRate));

        // When
        interestRateService.inactivate(id);

        // Then
        verify(interestRateRepository, times(1)).findById(id);
        verify(interestRateRepository, times(1)).save(expectedRate);
        assertEquals("INA", expectedRate.getState());
        assertNotNull(expectedRate.getEnd());
    }

    @Test
    void testUpdateNotFound() {
        // Given
        Integer id = 1;
        InterestRate interestRate = new InterestRate();
        interestRate.setName("test");
        interestRate.setInterestRate(new BigDecimal("0.01"));
        interestRate.setState("ACT");
        interestRate.setStart(new Date());

        InterestRate updatedInterestRate = new InterestRate();
        updatedInterestRate.setName("updated");
        updatedInterestRate.setInterestRate(new BigDecimal("0.02"));
        updatedInterestRate.setState("ACT");
        updatedInterestRate.setStart(new Date());

        // When
        when(interestRateRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThrows(CRUDException.class, () -> interestRateService.update(id, updatedInterestRate));
        verify(interestRateRepository, times(1)).findById(id);

    }

    @Test
    void testUpdateError() {
        // Given
        Integer id = 1;
        InterestRate interestRate = new InterestRate();
        interestRate.setName("test");
        interestRate.setInterestRate(new BigDecimal("0.01"));
        interestRate.setState("ACT");
        interestRate.setStart(new Date());

        InterestRate updatedInterestRate = new InterestRate();
        updatedInterestRate.setName("updated");
        updatedInterestRate.setInterestRate(new BigDecimal("0.02"));
        updatedInterestRate.setState("ACT");
        updatedInterestRate.setStart(new Date());

        // When
        when(interestRateRepository.findById(id)).thenReturn(Optional.of(interestRate));
        doThrow(new RuntimeException("Something went wrong")).when(interestRateRepository)
                .save(any(InterestRate.class));

        // Then
        assertThrows(CRUDException.class, () -> interestRateService.update(id, updatedInterestRate));
        verify(interestRateRepository, times(1)).findById(id);
        verify(interestRateRepository, times(1)).save(updatedInterestRate);
    }

}
