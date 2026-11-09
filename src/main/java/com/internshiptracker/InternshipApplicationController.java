package com.internshiptracker;


import java.sql.SQLException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class InternshipApplicationController {

    private final InternshipApplicationService service;

    public InternshipApplicationController(InternshipApplicationService service){
        this.service = service;
    }

    @Operation(
            summary = "List internship applications",
            description = "Retrieves internship applications with " +
                    "optional status and company filters, using pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application retrieved Successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/applications")
    public ResponseEntity<PageResponse> getApplications
            (@RequestParam(required = false) ApplicationStatus status,
             @RequestParam(required = false) String company,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
             HttpServletRequest request) throws SQLException
    {
        HttpSession session = request.getSession(false);
        Integer userId = session == null ? null: (Integer) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PageResponse applications = service.getApplications(status, company, page, size, userId);
        return ResponseEntity.ok(applications);
    }


    @Operation(
            summary = "Get an internship application by ID",
            description = "Retrieves a single internship application using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Application found"),
            @ApiResponse(responseCode = "404", description = "Application not found", content = @Content)
    })
    @GetMapping("/applications/{id}")
    public  ResponseEntity<InternshipApplication> findById(
            @PathVariable int id, HttpServletRequest request) throws SQLException{
        HttpSession session = request.getSession(false);
        Integer userId = session == null ? null: (Integer) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        InternshipApplication application =  service.findById(id, userId);

        if(application == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(application);
    }


    @Operation(
            summary = "Create an application",
            description = "Add a single internship application."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Application created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InternshipApplication.class)

                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid application",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @Parameter(
            name = "X-CSRF-Token",
            description = "CSRF token obtained from GET /auth/csrf after login",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER
    )
    @PostMapping("/applications")
    public ResponseEntity<InternshipApplication> createApplication(
            @RequestBody CreateApplicationRequest request, HttpServletRequest requestSession)
            throws SQLException
    {
        HttpSession session = requestSession.getSession(false);
        Integer userId = session == null ? null: (Integer) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        InternshipApplication application = new InternshipApplication(request.company(),
                request.position(), request.status(), request.location());
        application.setApplicationDate(request.applicationDate());
        service.addApplication(application, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }


    @Operation(
            summary = "Remove an application by its ID",
            description = "Delete a single internship application using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Application deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Application not found", content = @Content)
    })

    @Parameter(
            name = "X-CSRF-Token",
            description = "CSRF token obtained from GET /auth/csrf after login",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER
    )
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> deleteApplicationById(
            @PathVariable int id, HttpServletRequest request) throws SQLException{
        HttpSession session = request.getSession(false);
        Integer userId = session == null ? null: (Integer) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean deleted = service.deleteById(id, userId);
        if(!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Modify status of an application by its ID",
            description = "Update status of a single internship application using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status of application updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Application not found", content = @Content),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing status",
                    content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                    )
                )
    })

    @Parameter(
            name = "X-CSRF-Token",
            description = "CSRF token obtained from GET /auth/csrf after login",
            required = true,
            in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER
    )
    @PatchMapping("/applications/{id}")
    public ResponseEntity<Void> changeStatusByID(
            @PathVariable int id, @RequestBody UpdateStatusRequest request, HttpServletRequest servletRequest)
            throws SQLException
    {
        HttpSession session = servletRequest.getSession(false);
        Integer userId = session == null ? null: (Integer) session.getAttribute("userId");
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ApplicationStatus status = request.status();
        boolean updated = service.updateStatus(id,status, userId);

        if(!updated)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
