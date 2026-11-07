package com.internshiptracker;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternshipApplicationServiceTest {
    @Test
    void shouldRejectBlankCompany() throws SQLException{
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);

        InternshipApplicationService service = new InternshipApplicationService(repository);

        InternshipApplication application = new InternshipApplication(
                        "   ",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "New York"
                );
        assertThrows(IllegalArgumentException.class, () -> {
            service.addApplication(application, 1);
        });
        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectBlankPosition() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);

        InternshipApplicationService service = new InternshipApplicationService(repository);
        InternshipApplication application = new InternshipApplication(
                "Google", "  ", ApplicationStatus.APPLIED, "New York");
        assertThrows(IllegalArgumentException.class, () -> {
            service.addApplication(application, 1);
        });
        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectNullStatus() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);

        InternshipApplicationService service = new InternshipApplicationService(repository);
        InternshipApplication application = new InternshipApplication(
                "Google",
                "Software Engineer Intern",
                null,
                "New York");
        assertThrows(IllegalArgumentException.class, () -> {
            service.addApplication(application,1);
        });
        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectNullStatusUpdate() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);

        InternshipApplicationService service = new InternshipApplicationService(repository);

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateStatus(3,null, 1);
        });
        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectNullApplication() throws SQLException{
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);
        assertThrows(IllegalArgumentException.class, () -> {service.addApplication(null,1);});
        verifyNoInteractions(repository);
    }

    @Test
    void shouldSaveValidApplication() throws SQLException {
        InternshipApplicationRepository repository =
                mock(InternshipApplicationRepository.class);

        InternshipApplicationService service =
                new InternshipApplicationService(repository);

        InternshipApplication application =
                new InternshipApplication(
                        "Google",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "New York"
                );
        service.addApplication(application,1);
        verify(repository).save(application,1);
    }

    @Test
    void shouldRejectNegativePage(){
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);
        assertThrows(IllegalArgumentException.class, ()->{
            service.getApplications(null,null, -1, 10, 1);});
        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectZeroPageSize(){
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);
        assertThrows(IllegalArgumentException.class, () -> {
            service.getApplications(null, null, 0,0, 1);});
        verifyNoInteractions(repository);
    }
    @Test
    void shouldCalculateTotalPagesCorrectly() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);

        when(repository.findPage(null, null, 0, 10, 1))
                .thenReturn(new ArrayList<>());
        when(repository.count(null,null, 1)).thenReturn(23);

        PageResponse response = service.getApplications(null, null, 0, 10, 1);

        assertEquals(3, response.totalPages());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals(23, response.totalElements());
    }

    @Test
    void shouldCalculatePagesWhenTotalIsExactlyDivisible() throws SQLException{
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);

        when(repository.findPage(null, null, 0, 10, 1))
                .thenReturn(new ArrayList<>());
        when(repository.count(null,null, 1)).thenReturn(20);

        PageResponse response = service.getApplications(null, null, 0, 10, 1);

        assertEquals(2, response.totalPages());
    }

    @Test
    void shouldReturnZeroPagesWhenNoApplicationsExist() throws SQLException{
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);

        when(repository.findPage(null, null, 0, 10, 1))
                .thenReturn(new ArrayList<>());
        when(repository.count(null,null, 1)).thenReturn(0);

        PageResponse response = service.getApplications(null, null, 0, 10, 1);

        assertEquals(0, response.totalPages());
    }

    @Test
    void shouldPassFiltersAndPaginationToRepository() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);

        when(repository.findPage(ApplicationStatus.INTERVIEW, "Google", 1, 5,1))
                .thenReturn(new ArrayList<>());
        PageResponse response = service.getApplications(ApplicationStatus.INTERVIEW, "Google", 1,5,1);

        assertEquals(1,response.page());
        verify(repository).findPage(ApplicationStatus.INTERVIEW, "Google", 1, 5,1);
        verify(repository).count(ApplicationStatus.INTERVIEW, "Google",1);
    }

    @Test
    void shouldNormalizeBlankCompanyFilter() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);

        when(repository.findPage(ApplicationStatus.OA, null, 2, 10, 1))
                .thenReturn(new ArrayList<>());
        service.getApplications(ApplicationStatus.OA, "  ", 2,10, 1);
        verify(repository).findPage(ApplicationStatus.OA, null, 2, 10, 1);
        verify(repository).count(ApplicationStatus.OA, null, 1);
    }

    @Test
    void shouldReturnTrueWhenApplicationIsDeleted() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);

        when(repository.deleteById(99,1)).thenReturn(true);
        boolean result = service.deleteById(99, 1);
        assertTrue(result);
        verify(repository).deleteById(99, 1);
    }

    @Test
    void shouldReturnFalseWhenApplicationDoesNotExist() throws SQLException {
        InternshipApplicationRepository repository = mock(InternshipApplicationRepository.class);
        InternshipApplicationService service = new InternshipApplicationService(repository);

        when(repository.deleteById(999, 1)).thenReturn(false);
        boolean result = service.deleteById(999, 1);
        assertFalse(result);
        verify(repository).deleteById(999, 1);
    }
}
