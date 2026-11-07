package com.internshiptracker;

import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.ArrayList;



@Service
public class InternshipApplicationService {
    private final InternshipApplicationRepository repository;


    public InternshipApplicationService(InternshipApplicationRepository repository){
        this.repository = repository;
    }

    public InternshipApplication findById(int id, int userId) throws  SQLException{
        return repository.findById(id, userId);
    }

    public void addApplication(InternshipApplication application, int userId) throws SQLException{
        if(application == null){
            throw new IllegalArgumentException("Application cannot be null");
        }
        if(application.getCompany() == null || application.getCompany().isBlank()) {
            throw new IllegalArgumentException("Company cannot be blank.");
        }
        if(application.getPosition() == null || application.getPosition().isBlank()) {
            throw new IllegalArgumentException("Position cannot be blank.");
        }
        if (application.getStatus() == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        repository.save(application, userId);
    }

    public boolean deleteById(int id, int userId) throws SQLException{
        return repository.deleteById(id, userId);
    }

    public boolean updateStatus(int id, ApplicationStatus newStatus, int userId) throws SQLException {
        if(newStatus == null){
            throw new IllegalArgumentException("New status cannot be null.");
        }
        return repository.updateStatus(id, newStatus, userId);
    }

    public PageResponse getApplications(
            ApplicationStatus status, String company, int page, int size, int userId)
            throws SQLException {
        if (company != null && company.isBlank()) {
            company = null;
        }
        if(page < 0 || size <= 0){
            throw new IllegalArgumentException("Page must be 0 or greater, and size must be greater than 0.");
        }
        ArrayList<InternshipApplication> content = repository.findPage(status, company, page, size, userId);
        int totalElements = repository.count(status, company, userId);
        int totalPages = (totalElements + size - 1)/size;
        return new PageResponse(content,page,size,totalElements, totalPages);
    }
}
