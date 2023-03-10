package com.banquito.core.branches.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.banquito.core.branches.controller.dto.BranchRQRS;

import com.banquito.core.branches.exception.CRUDException;
import com.banquito.core.branches.model.Branch;
import com.banquito.core.branches.service.BranchService;

public class BranchControllerTest {

    @Mock
    private BranchService branchService;

    @InjectMocks
    private BranchController branchController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateValid() throws CRUDException {
        BranchRQRS branchRQRS = new BranchRQRS();
        branchRQRS.setCode("asd123");
        branchRQRS.setName("Branch1");

        branchService.create(any(Branch.class));

        BranchController branchController = new BranchController(branchService);
        ResponseEntity response = branchController.create(branchRQRS);
        assertNotEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateInvalid() throws CRUDException {
        BranchRQRS branchRQRS = new BranchRQRS();
        branchRQRS.setCode("asd123");
        branchRQRS.setName("Branch1");

        String expectedErrorMessage = "Failed to create branch";
        doThrow(new CRUDException(500, expectedErrorMessage)).when(branchService).create(any(Branch.class));

        BranchController branchController = new BranchController(branchService);
        ResponseEntity response = branchController.create(branchRQRS);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(branchService, times(1)).create(any(Branch.class));
    }

    @Test
    void testObtainAll() {

        Branch branch1 = new Branch();
        branch1.setCode("asd123");
        branch1.setName("Branch1");

        Branch branch2 = new Branch();
        branch2.setCode("zxc123");
        branch2.setName("Branch2");

        List<Branch> branches = new ArrayList<>();
        branches.add(branch1);
        branches.add(branch2);

        when(branchService.getAll()).thenReturn(branches);

        BranchController branchController = new BranchController(branchService);
        ResponseEntity<List<BranchRQRS>> response = branchController.obtainAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void testObtainByCodeWithValidCode() {
        Branch branch = new Branch();
        branch.setCode("asd123");
        branch.setName("Branch1");

        when(branchService.lookByCode(anyString())).thenReturn(branch);

        BranchController branchController = new BranchController(branchService);
        ResponseEntity<BranchRQRS> response = branchController.obtainByCode("asd123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testObtainByCodeWithInvalidCode() {
        when(branchService.lookByCode(anyString())).thenReturn(null);

        BranchController branchController = new BranchController(branchService);
        ResponseEntity<BranchRQRS> response = branchController.obtainByCode("asd123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdate() throws CRUDException {

        Branch branch1 = new Branch();
        branch1.setCode("asd123");
        branch1.setName("Branch1");
        branch1.setId("idTest");
        when(branchService.lookByCode("asd123")).thenReturn(branch1);

        BranchRQRS branchRQRS = new BranchRQRS();
        branchRQRS.setCode("asd123");
        branchRQRS.setName("Branch1-updated");
        // When
        ResponseEntity<BranchRQRS> response = branchController.update("asd123", branchRQRS);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BranchRQRS updatedBranch = response.getBody();
        assertEquals("asd123", updatedBranch.getCode());

    }

}
