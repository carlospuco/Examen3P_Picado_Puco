package com.banquito.core.branches.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import com.banquito.core.branches.exception.CRUDException;
import com.banquito.core.branches.model.Branch;
import com.banquito.core.branches.repository.BranchRepository;
import com.banquito.core.branches.service.BranchService;

public class BranchServiceTest {

    private BranchService branchService;

    @Mock
    private Logger logger;

    @Mock
    private BranchRepository branchRepositoryMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        branchService = new BranchService(branchRepositoryMock);
    }

    @Test
    void testCreateValid() throws CRUDException {
        Branch branch = new Branch();
        branch.setCode("asd123");
        branch.setName("Branch1");
        when(branchRepositoryMock.save(branch)).thenReturn(branch);
        branchService.create(branch);
        verify(branchRepositoryMock, times(1)).save(branch);

    }

    @Test
    void testCreateInvalid() {
        Branch branch = new Branch();
        branch.setCode("asd123");
        branch.setName("Branch1");
        Exception expectedException = new RuntimeException("Failed to save branch");
        when(branchRepositoryMock.save(branch)).thenThrow(expectedException);

        try {
            branchService.create(branch);
            fail("Expected CRUDException to be thrown");
        } catch (CRUDException e) {
            assertEquals(510, e.getErrorCode());
            assertEquals("Branch cannot be created, error:" + expectedException.getMessage(), e.getMessage());
            assertEquals(expectedException, e.getCause());
        }

        verify(branchRepositoryMock, times(1)).save(branch);
    }

    @Test
    void testGetAll() {
        List<Branch> branches = new ArrayList<Branch>();
        Branch branch1 = new Branch();
        branch1.setCode("asd123");
        branch1.setName("Branch1");
        branches.add(branch1);
        Branch branch2 = new Branch();
        branch2.setCode("zxc123");
        branch2.setName("Branch2");
        branches.add(branch2);
        when(branchRepositoryMock.findAll()).thenReturn(branches);

        List<Branch> result = branchService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("asd123", result.get(0).getCode());
        assertEquals("zxc123", result.get(1).getCode());
        verify(branchRepositoryMock, times(1)).findAll();
    }

    @Test
    void testLookByCode() {
        String code = "asd123";
        Branch branch = new Branch();
        branch.setCode("asd123");
        branch.setName("Branch1");
        when(branchRepositoryMock.findByCode(code)).thenReturn(branch);

        Branch result = branchService.lookByCode(code);

        assertNotNull(result);
        assertEquals("asd123", result.getCode());
        assertEquals("Branch1", result.getName());
        verify(branchRepositoryMock, times(1)).findByCode(code);
    }

    @Test
    void testLookByIdWithValidId() throws CRUDException {
        String id = "asd123";
        Optional<Branch> branch = Optional.of(new Branch());
        branch.get().setId(id);
        branch.get().setCode("zxc123");
        branch.get().setName("Branch1");
        when(branchRepositoryMock.findById(id)).thenReturn(branch);

        Branch result = branchService.lookById(id);

        assertNotNull(result);
        assertEquals("zxc123", result.getCode());
        assertEquals("Branch1", result.getName());

        verify(branchRepositoryMock, times(1)).findById(id);
    }

    @Test
    void testLookByIdWithInvalidId() {
        String id = "invalidId";
        Optional<Branch> branch = Optional.empty();
        when(branchRepositoryMock.findById(id)).thenReturn(branch);

        try {
            branchService.lookById(id);
            fail("Expected CRUDException to be thrown");
        } catch (CRUDException e) {
            assertEquals(404, e.getErrorCode());
            assertEquals("Branch with id: {" + id + "} does not exist", e.getMessage());
        }

        verify(branchRepositoryMock, times(1)).findById(id);
    }

    @Test
    void testUpdate() throws CRUDException {
        ArgumentCaptor<Branch> argument = ArgumentCaptor.forClass(Branch.class);

        String name = "branch2";
        String code = "zxc123";
        Branch branch = new Branch();

        branch.setCode("zxc123");
        branch.setName("branch2");
        when(branchRepositoryMock.findByCode(any(String.class))).thenReturn(branch);

        branchService.update(code, branch);
        verify(branchRepositoryMock).save(argument.capture());
        assertEquals(name, argument.getValue().getName());

    }

}