package com.internshiptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.mariadb.jdbc.MariaDbDataSource;
import javax.sql.DataSource;


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
    private DataSource createTestDataSource() throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();

        dataSource.setUrl(System.getenv("DB_URL"));
        dataSource.setUser("internship_app_user");
        dataSource.setPassword(System.getenv("DB_PASSWORD"));

        return dataSource;
    }

    @Test
    void shouldFindSavedApplicationById() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository(createTestDataSource());
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        application.setApplicationDate(LocalDate.of(2026, 10, 24));
        int userId = createTestUser();
        try {
            repository.save(application, userId);
            InternshipApplication found = repository.findById(application.getId(), userId);
            assertEquals("IntegrationTestCompany", found.getCompany());
            assertEquals(LocalDate.of(2026, 10, 24), found.getApplicationDate());

        } finally {
            if(application.getId() > 0) {
                repository.deleteById(application.getId(), userId);
            }
            deleteTestUser(userId);
        }
    }

    @Test
    void shouldUpdateApplicationStatus() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository(createTestDataSource());
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        int userId = createTestUser();
        try{
            repository.save(application, userId);
            repository.updateStatus(application.getId(), ApplicationStatus.INTERVIEW, userId);
            InternshipApplication updated = repository.findById(application.getId(), userId);

            assertEquals(ApplicationStatus.INTERVIEW, updated.getStatus());
        }
        finally {
            if(application.getId() > 0){
                repository.deleteById(application.getId(), userId);
            }
            deleteTestUser(userId);
        }
    }

    @Test
    void shouldDeleteSavedApplication() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository(createTestDataSource());
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        int userId = createTestUser();
        try{
            repository.save(application, userId);
            repository.deleteById(application.getId(), userId);
            InternshipApplication deleted = repository.findById(application.getId(), userId);
            assertNull(deleted);
        }
        finally {
            if(application.getId()>0){
                repository.deleteById(application.getId(), userId);
            }
            deleteTestUser(userId);
        }
    }

    @Test
    void shouldFilterApplicationsByCompany() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository(createTestDataSource());
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        InternshipApplication application1 = new InternshipApplication(
                "Google Cloud", "Intern", ApplicationStatus.INTERVIEW,"New York");
        int userId = createTestUser();

        try{
            repository.save(application, userId);
            repository.save(application1, userId);

            ArrayList<InternshipApplication> result=
                    repository.findPage(null,"Google",0,10, userId);

            boolean containsGoogle = result.stream().
                    anyMatch(app ->app.getId()==application1.getId());
            boolean containsNotGoogle = result.stream().
                    anyMatch(app ->app.getId()==application.getId());

            assertTrue(containsGoogle);
            assertFalse(containsNotGoogle);
        }
        finally {
            if(application.getId()>0){
                repository.deleteById(application.getId(), userId);
            }
            if(application1.getId()>0){
                repository.deleteById(application1.getId(), userId);
            }
            deleteTestUser(userId);
        }
    }

    @Test
    void shouldFilterApplicationsByStatus() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository(createTestDataSource());
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        InternshipApplication application1 = new InternshipApplication(
                "Google", "Intern", ApplicationStatus.INTERVIEW, "New York");
        int userId = createTestUser();
        try {
            repository.save(application, userId);
            repository.save(application1, userId);

            ArrayList<InternshipApplication> result =
                    repository.findPage(ApplicationStatus.INTERVIEW, null, 0, 10, userId);

            boolean containsInterviewApplications = result.stream().
                    anyMatch(app -> app.getId() == application1.getId());
            boolean containsAppliedApplications = result.stream().
                    anyMatch(app -> app.getId() == application.getId());

            assertTrue(containsInterviewApplications);
            assertFalse(containsAppliedApplications);
        } finally {
            if (application.getId() > 0) {
                repository.deleteById(application.getId(), userId);
            }
            if (application1.getId() > 0) {
                repository.deleteById(application1.getId(), userId);
            }
            deleteTestUser(userId);
        }
    }
    @Test
    void shouldReturnCorrectPage() throws SQLException{
        InternshipApplicationRepository repository = new InternshipApplicationRepository(createTestDataSource());
        ArrayList<InternshipApplication> applications = new ArrayList<>();
        int userId = createTestUser();

        try{
            for(int i = 0; i < 5; i++){
                InternshipApplication app = new InternshipApplication(
                        "TestCompany" +i, "Intern", ApplicationStatus.OA,
                        "New York"
                );
                repository.save(app, userId);
                applications.add(app);
            }
            ArrayList<InternshipApplication> results = repository.findPage(null,
                    "TestCompany", 1, 2, userId);
            assertEquals(2, results.size());
            assertEquals(applications.get(2).getId(), results.get(0).getId());
            assertEquals(applications.get(1).getId(), results.get(1).getId());
        }
        finally {
            for(InternshipApplication app : applications){
                repository.deleteById(app.getId(),userId);
            }
            deleteTestUser(userId);
        }
    }

    @Test
    void shouldSaveApplicationWithoutApplicationDate() throws SQLException {
        InternshipApplicationRepository repository = new InternshipApplicationRepository(createTestDataSource());
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        int userId = createTestUser();
        try{
            repository.save(application, userId);
            InternshipApplication app = repository.findById(application.getId(), userId);
            assertNull(app.getApplicationDate());
        }
        finally {
            if(application.getId() > 0){
                repository.deleteById(application.getId(), userId);
            }
            deleteTestUser(userId);
        }
    }

    @Test
    void shouldNotFindApplicationBelongingToAnotherUser() throws SQLException {
        InternshipApplicationRepository repository = new
                InternshipApplicationRepository(createTestDataSource());
        InternshipApplication application =
                new InternshipApplication(
                        "IntegrationTestCompany",
                        "Software Engineer Intern",
                        ApplicationStatus.APPLIED,
                        "Remote"
                );
        int userIdA = createTestUser();
        int userIdB = createTestUser();
        try{
            repository.save(application, userIdA);
            InternshipApplication result = repository.findById(application.getId(), userIdB);
            assertNull(result);

            InternshipApplication ownerResult =
                    repository.findById(application.getId(), userIdA);
            assertNotNull(ownerResult);
        }
        finally {
            if(application.getId() > 0){
                repository.deleteById(application.getId(), userIdA);
            }
            deleteTestUser(userIdA);
            deleteTestUser(userIdB);
        }
    }

    private int createTestUser() throws SQLException {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection connection = createTestDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "test_user_" + UUID.randomUUID());
            statement.setString(2, "dummy_hash");

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

                throw new SQLException("Failed to retrieve generated test user ID.");
            }
        }
    }

    private void deleteTestUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = createTestDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}
