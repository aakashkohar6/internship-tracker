package com.internshiptracker;

public record ApiError (int status, String error, String message){
}
