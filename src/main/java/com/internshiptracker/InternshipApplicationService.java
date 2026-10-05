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

    public InternshipApplication findById(int id) throws  SQLException{
        return repository.findById(id);
    }

    public void addApplication(InternshipApplication application) throws SQLException{
        if(application == null){
            throw new IllegalArgumentException("Application cannot be null");
        }
        if(application.getCompany() == null || application.getCompany().isBlank()) {
            throw new IllegalArgumentException("Company cannot be blank.");
        }
        repository.save(application);
    }

    public boolean deleteById(int id) throws SQLException{
        return repository.deleteById(id);
    }

    public boolean updateStatus(int id, ApplicationStatus newStatus) throws SQLException {
        return repository.updateStatus(id, newStatus);
    }

    public PageResponse getApplications(ApplicationStatus status, String company, int page, int size)
            throws SQLException {
        if (company != null && company.isBlank()) {
            company = null;
        }
        if(page < 0 || size <= 0){
            throw new IllegalArgumentException("Page must be 0 or greater, and size must be greater than 0.");
        }
        ArrayList<InternshipApplication> content = repository.findPage(status, company, page, size);
        int totalElements = repository.count(status, company);
        int totalPages = (totalElements+size-1)/size;
        return new PageResponse(content,page,size,totalElements, totalPages);
    }
}
