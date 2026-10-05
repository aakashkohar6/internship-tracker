package com.internshiptracker;

public record CreateApplicationRequest(String company,
                                       String position,
                                       ApplicationStatus status,
                                       String location)
{


}
