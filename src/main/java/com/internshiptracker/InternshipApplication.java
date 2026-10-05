package com.internshiptracker;

public class InternshipApplication {

    private String company;
    private String position;
    private ApplicationStatus status;
    private String location;

    private int id;

    public InternshipApplication(int id, String company, String position,
                                 ApplicationStatus status, String location){
        this.company = company;
        this.position = position;
        this.status = status;
        this.location = location;

        this.id = id;
    }

    public InternshipApplication(String company, String position, ApplicationStatus status, String location){
        this.company = company;
        this.position = position;
        this.status = status;
        this.location = location;
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

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setID(int id){
        this.id = id;
    }

    public void displayApplication(){
        System.out.println("ID: " +id);
        System.out.println("Company: " +company);
        System.out.println("Position: " +position);
        System.out.println("Status: " +status);
        System.out.println("Location: " +location);
    }

    public  void setStatus(ApplicationStatus status){
        this.status = status;
    }

}
