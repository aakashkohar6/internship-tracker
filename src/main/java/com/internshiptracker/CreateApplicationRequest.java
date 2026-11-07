package com.internshiptracker;

import java.time.LocalDate;

public record CreateApplicationRequest(String company,
                                       String position,
                                       ApplicationStatus status,
                                       String location,
                                       LocalDate applicationDate)
{
}
