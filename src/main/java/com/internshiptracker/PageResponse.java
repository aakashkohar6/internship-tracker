package com.internshiptracker;

import java.util.ArrayList;

public record PageResponse
        (ArrayList<InternshipApplication> content, int page, int size, int totalElements, int totalPages) {
}
