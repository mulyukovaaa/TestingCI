package org.itmo.testing.lab2.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserStatusServiceTest {

    @Mock
    private UserAnalyticsService userAnalyticsService;

    private UserStatusService userStatusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userStatusService = new UserStatusService(userAnalyticsService);
    }

    @Test
    void testGetUserStatus_Inactive() {
        String userId = "user1";
        when(userAnalyticsService.getTotalActivityTime(userId)).thenReturn(30L);

        String status = userStatusService.getUserStatus(userId);

        assertEquals("Inactive", status);
        verify(userAnalyticsService, times(1)).getTotalActivityTime(userId);
    }

    @Test
    void testGetUserStatus_Active() {
        String userId = "user2";
        when(userAnalyticsService.getTotalActivityTime(userId)).thenReturn(90L);

        String status = userStatusService.getUserStatus(userId);

        assertEquals("Active", status);
        verify(userAnalyticsService, times(1)).getTotalActivityTime(userId);
    }

    @Test
    void testGetUserStatus_HighlyActive() {
        String userId = "user3";
        when(userAnalyticsService.getTotalActivityTime(userId)).thenReturn(150L);

        String status = userStatusService.getUserStatus(userId);

        assertEquals("Highly active", status);
        verify(userAnalyticsService, times(1)).getTotalActivityTime(userId);
    }

    @Test
    void testGetUserStatus() {
        when(userAnalyticsService.getTotalActivityTime("user1")).thenReturn(30L);
        assertEquals("Inactive", userStatusService.getUserStatus("user1"));

        when(userAnalyticsService.getTotalActivityTime("user2")).thenReturn(90L);
        assertEquals("Active", userStatusService.getUserStatus("user2"));

        when(userAnalyticsService.getTotalActivityTime("user3")).thenReturn(150L);
        assertEquals("Highly active", userStatusService.getUserStatus("user3"));

        verify(userAnalyticsService, times(3)).getTotalActivityTime(anyString());
    }

    @Test
    void testGetUserLastSessionDate_MultipleSessions() {
        LinkedList<UserAnalyticsService.Session> sessions = new LinkedList<>();
        sessions.add(new UserAnalyticsService.Session(
                LocalDateTime.of(2024, 3, 10, 12, 0),
                LocalDateTime.of(2024, 3, 10, 14, 0)
        ));
        sessions.add(new UserAnalyticsService.Session(
                LocalDateTime.of(2024, 3, 11, 16, 0),
                LocalDateTime.of(2024, 3, 11, 18, 0)
        ));

        when(userAnalyticsService.getUserSessions("user1")).thenReturn(sessions);

        Optional<String> lastSessionDate = userStatusService.getUserLastSessionDate("user1");

        assertTrue(lastSessionDate.isPresent());
        assertEquals("2024-03-11", lastSessionDate.get());
        verify(userAnalyticsService).getUserSessions("user1");
    }

    @Test
    void testGetUserLastSessionDate_EmptySessions() {
        LinkedList<UserAnalyticsService.Session> sessions = new LinkedList<>();
        when(userAnalyticsService.getUserSessions("user1")).thenReturn(sessions);

        Optional<String> lastSessionDate = userStatusService.getUserLastSessionDate("user1");

        assertFalse(lastSessionDate.isPresent());
        verify(userAnalyticsService).getUserSessions("user1");
    }

    @Test
    void testGetUserLastSessionDate_SingleSession() {
        LinkedList<UserAnalyticsService.Session> sessions = new LinkedList<>();
        sessions.add(new UserAnalyticsService.Session(
                LocalDateTime.of(2024, 3, 10, 12, 0),
                LocalDateTime.of(2024, 3, 10, 14, 0)
        ));
        when(userAnalyticsService.getUserSessions("user1")).thenReturn(sessions);

        Optional<String> lastSessionDate = userStatusService.getUserLastSessionDate("user1");

        assertTrue(lastSessionDate.isPresent());
        assertEquals("2024-03-10", lastSessionDate.get());
        verify(userAnalyticsService).getUserSessions("user1");
    }

    @Test
    void testGetUserLastSessionDate_NullLogoutTime() {
        LinkedList<UserAnalyticsService.Session> sessions = new LinkedList<>();
        UserAnalyticsService.Session sessionWithNullLogout = mock(UserAnalyticsService.Session.class);
        when(sessionWithNullLogout.getLogoutTime()).thenReturn(null);
        sessions.add(sessionWithNullLogout);

        when(userAnalyticsService.getUserSessions("user1")).thenReturn(sessions);

        Optional<String> lastSessionDate = userStatusService.getUserLastSessionDate("user1");

        assertFalse(lastSessionDate.isPresent());
        verify(userAnalyticsService).getUserSessions("user1");
        verify(sessionWithNullLogout).getLogoutTime();
    }

}
