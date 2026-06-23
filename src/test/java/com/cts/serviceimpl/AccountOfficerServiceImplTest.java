package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.AccountOfficer;
import com.cts.entity.User;
import com.cts.mapper.AccountOfficerMapper;
import com.cts.repository.AccountOfficerRepository;
import com.cts.repository.LedgerEntryRepository;
import com.cts.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountOfficerServiceImplTest {

    @Mock
    private AccountOfficerRepository accountOfficerRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountOfficerServiceImpl service;

    private User user;
    private AccountOfficer officer;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1);

        officer = new AccountOfficer();
        officer.setUser(user);
    }

    @Test
    void testAddOfficerSuccess() {
        AccountOfficerInputDto input = new AccountOfficerInputDto();
        input.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(accountOfficerRepository.save(any(AccountOfficer.class))).thenReturn(officer);

        try (MockedStatic<AccountOfficerMapper> mapperMock = mockStatic(AccountOfficerMapper.class)) {

            mapperMock.when(() -> AccountOfficerMapper.convertToAccountOfficer(input, user))
                    .thenReturn(officer);

            AccountOfficerOutputDto outputDto = new AccountOfficerOutputDto();
            mapperMock.when(() -> AccountOfficerMapper.convertToAccountOfficerOutputDto(officer))
                    .thenReturn(outputDto);

            AccountOfficerOutputDto result = service.addOfficer(input);

            assertNotNull(result);
            verify(userRepository).findById(1);
            verify(accountOfficerRepository).save(officer);
        }
    }

    @Test
    void testAddOfficerUserNotFound() {
        AccountOfficerInputDto input = new AccountOfficerInputDto();
        input.setUserId(99);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.addOfficer(input));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testGetAllOfficers() {
        when(accountOfficerRepository.findAll()).thenReturn(List.of(officer));

        try (MockedStatic<AccountOfficerMapper> mapperMock = mockStatic(AccountOfficerMapper.class)) {

            AccountOfficerOutputDto dto = new AccountOfficerOutputDto();
            mapperMock.when(() -> AccountOfficerMapper.convertToAccountOfficerOutputDto(officer))
                    .thenReturn(dto);

            List<AccountOfficerOutputDto> result = service.getAllOfficers();

            assertEquals(1, result.size());
        }
    }

    @Test
    void testGetOfficerByIdSuccess() {
        when(accountOfficerRepository.findById(1))
                .thenReturn(Optional.of(officer));

        try (MockedStatic<AccountOfficerMapper> mapperMock = mockStatic(AccountOfficerMapper.class)) {

            AccountOfficerOutputDto dto = new AccountOfficerOutputDto();
            mapperMock.when(() -> AccountOfficerMapper.convertToAccountOfficerOutputDto(officer))
                    .thenReturn(dto);

            AccountOfficerOutputDto result = service.getOfficerById(1);

            assertNotNull(result);
        }
    }

    @Test
    void testGetOfficerByIdNotFound() {
        when(accountOfficerRepository.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getOfficerById(1));

        assertEquals("Account officer not found", ex.getMessage());
    }

    @Test
    void testGetOfficerLedgerEntries() {
        when(accountOfficerRepository.findById(1))
                .thenReturn(Optional.of(officer));

        when(ledgerEntryRepository.findByAccountOfficer(officer))
                .thenReturn(Collections.emptyList());

        List<LedgerEntryOutputDto> result = service.getOfficerLedgerEntries(1);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetOfficerLedgerEntriesNotFound() {
        when(accountOfficerRepository.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getOfficerLedgerEntries(1));

        assertEquals("Account officer not found", ex.getMessage());
    }
}