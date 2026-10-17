package com.internshiptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;


public class InternshipApplicationRepositoryIntegrationTest {
    @BeforeEach
    void verifyTestDatabase() {
        String url = System.getenv("DB_URL");

        if (!"jdbc:mariadb://localhost:3306/internship_tracker_test".equals(url)) {
            throw new IllegalStateException(
                    "Integration tests must use internship_tracker_test!"
            );
        }
    }

    @Test
    void shouldFindSavedApplicationById() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository();
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        try {
            repository.save(application);
            InternshipApplication found = repository.findById(application.getId());
            assertEquals("IntegrationTestCompany", found.getCompany());

        } finally {
            if(application.getId() > 0) {
                repository.deleteById(application.getId());
                // 4. Delete the application using its generated ID.
            }
        }
    }

    @Test
    void shouldUpdateApplicationStatus() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository();
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        try{
            repository.save(application);
            repository.updateStatus(application.getId(), ApplicationStatus.INTERVIEW);
            InternshipApplication updated = repository.findById(application.getId());

            assertEquals(ApplicationStatus.INTERVIEW, updated.getStatus());
        }
        finally {
            if(application.getId() > 0){
                repository.deleteById(application.getId());
            }
        }
    }

    @Test
    void shouldDeleteSavedApplication() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository();
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        try{
            repository.save(application);
            repository.deleteById(application.getId());
            InternshipApplication deleted = repository.findById(application.getId());
            assertNull(deleted);
        }
        finally {
            if(application.getId()>0){
                repository.deleteById(application.getId());
            }
        }
    }

    @Test
    void shouldFilterApplicationsByCompany() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository();
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        InternshipApplication application1 = new InternshipApplication(
                "Google Cloud", "Intern", ApplicationStatus.INTERVIEW,"New York");

        try{
            repository.save(application);
            repository.save(application1);

            ArrayList<InternshipApplication> result=
                    repository.findPage(null,"Google",0,10);

            boolean containsGoogle = result.stream().
                    anyMatch(app ->app.getId()==application1.getId());
            boolean containsNotGoogle = result.stream().
                    anyMatch(app ->app.getId()==application.getId());

            assertTrue(containsGoogle);
            assertFalse(containsNotGoogle);
        }
        finally {
            if(application.getId()>0){
                repository.deleteById(application.getId());
            }
            if(application1.getId()>0){
                repository.deleteById(application1.getId());
            }
        }
    }

    @Test
    void shouldFilterApplicationsByStatus() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository();
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        InternshipApplication application1 = new InternshipApplication(
                "Google", "Intern", ApplicationStatus.INTERVIEW, "New York");

        try {
            repository.save(application);
            repository.save(application1);

            ArrayList<InternshipApplication> result =
                    repository.findPage(ApplicationStatus.INTERVIEW, null, 0, 10);

            boolean containsInterviewApplications = result.stream().
                    anyMatch(app -> app.getId() == application1.getId());
            boolean containsAppliedApplications = result.stream().
                    anyMatch(app -> app.getId() == application.getId());

            assertTrue(containsInterviewApplications);
            assertFalse(containsAppliedApplications);
        } finally {
            if (application.getId() > 0) {
                repository.deleteById(application.getId());
            }
            if (application1.getId() > 0) {
                repository.deleteById(application1.getId());
            }
        }
    }
    @Test
    void shouldReturnCorrectPage() throws SQLException{
        InternshipApplicationRepository repository = new InternshipApplicationRepository();
        ArrayList<InternshipApplication> applications = new ArrayList<>();

        try{
            for(int i = 0; i < 5; i++){
                InternshipApplication app = new InternshipApplication(
                        "TestCompany" +i, "Intern", ApplicationStatus.OA,
                        "New York"
                );
                repository.save(app);
                applications.add(app);
            }
            ArrayList<InternshipApplication> results = repository.findPage(null,
                    "TestCompany", 1, 2);
            assertEquals(2, results.size());
            assertEquals(applications.get(2).getId(), results.get(0).getId());
            assertEquals(applications.get(1).getId(), results.get(1).getId());
        }
        finally {
            for(InternshipApplication app : applications){
                repository.deleteById(app.getId());
            }
        }
    }
}
