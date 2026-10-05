package com.internshiptracker;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.http.ResponseEntity;

@RestController
public class InternshipApplicationController {

    private final InternshipApplicationService service;

    public InternshipApplicationController(InternshipApplicationService service){
        this.service = service;
    }

    @GetMapping("/applications")
    public PageResponse getApplications
            (@RequestParam(required = false) ApplicationStatus status,
             @RequestParam(required = false) String company,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size) throws SQLException
    {
        return service.getApplications(status,company, page, size);
    }

    @GetMapping("/applications/{id}")
    public  ResponseEntity<InternshipApplication> findById(@PathVariable int id) throws SQLException{
        InternshipApplication application =  service.findById(id);

        if(application == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(application);
    }

    @PostMapping("/applications")
    public ResponseEntity<InternshipApplication> createApplication(@RequestBody CreateApplicationRequest request)
            throws SQLException
    {
        InternshipApplication application = new InternshipApplication(request.company(),
                request.position(), request.status(), request.location());
        service.addApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> deleteApplicationById(@PathVariable int id) throws SQLException{
        boolean deleted = service.deleteById(id);

        if(!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/applications/{id}")
    public ResponseEntity<Void> changeStatusByID(@PathVariable int id, @RequestBody UpdateStatusRequest request)
            throws SQLException
    {
        ApplicationStatus status = request.status();
        boolean updated = service.updateStatus(id,status);

        if(!updated)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
