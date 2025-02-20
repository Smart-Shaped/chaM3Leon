package com.smartshaped.chameleon.serving.controller;

import com.smartshaped.chameleon.serving.model.Request;
import com.smartshaped.chameleon.serving.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

  @Autowired private RequestRepository requestRepository;

  @PostMapping
  public ResponseEntity<Request> createRequest(@RequestBody Request request) {
    Request savedRequest = requestRepository.save(request);
    return ResponseEntity.ok(savedRequest);
  }

  @GetMapping
  public ResponseEntity<List<Request>> readAllRequest() {
    List<Request> req = requestRepository.findAll();
    return ResponseEntity.ok(req);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Request> readRequest(@PathVariable UUID id) {
    Request req = requestRepository.findById(id).orElse(null);
    return ResponseEntity.ok(req);
  }


}
