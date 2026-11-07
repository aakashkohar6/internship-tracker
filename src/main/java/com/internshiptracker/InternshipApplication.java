package com.internshiptracker;

import java.time.LocalDate;

public class InternshipApplication {

    private String company;
    private String position;
    private ApplicationStatus status;
    private String location;
    private LocalDate applicationDate;

    private int id;

    public InternshipApplication(int id, String company, String position,
                                 ApplicationStatus status, String location, LocalDate applicationDate){
        this.company = company;
        this.position = position;
        this.status = status;
        this.location = location;
        this.id = id;
        this.applicationDate = applicationDate;
    }

    public InternshipApplication(String company, String position, ApplicationStatus status, String location){
        this.company = company;
        this.position = position;
        this.status = status;
        this.location = location;
    }
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    public String getCompany(){
        return company;
    }

    public String getPosition(){
        return position;
    }

    public ApplicationStatus getStatus(){
        return status;
    }

    public String getLocation(){
        return location;
    }

    public  void setStatus(ApplicationStatus status){
        this.status = status;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }
}
