package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintRequest;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintResponse;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // GET - api/complaint
    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> findAll() {

        List<ComplaintResponse> response = complaintService.findAll();

        return ResponseEntity.ok(response);
    }

    // GET - api/complaint/{id_complaint}
    @GetMapping("/{id_complaint}")
    public ResponseEntity<ComplaintResponse> findById(
            @PathVariable("id_complaint") Long idComplaint
    ) {

        ComplaintResponse response = complaintService.findById(idComplaint);

        return ResponseEntity.ok(response);
    }

    // POST - api/complaint
    @PostMapping
    public ResponseEntity<ComplaintResponse> create(
            @Valid @RequestBody ComplaintRequest request
    ) {

        ComplaintResponse response = complaintService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT - api/complaint/{id_complaint}
    @PutMapping("/{id_complaint}")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable("id_complaint") Long idComplaint,
            @Valid @RequestBody ComplaintStatusRequest request
    ) {

        ComplaintResponse response = complaintService.updateStatus(idComplaint, request);

        return ResponseEntity.ok(response);
    }
}