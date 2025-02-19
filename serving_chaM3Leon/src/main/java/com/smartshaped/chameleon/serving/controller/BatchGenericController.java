package com.smartshaped.chameleon.serving.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.smartshaped.chameleon.serving.repository.BatchGenericRepository;

public abstract class BatchGenericController<BatchBaseModel, ID> {

	private final BatchGenericRepository<BatchBaseModel, ID> repository;

	protected BatchGenericController(BatchGenericRepository<BatchBaseModel, ID> repository) {
		this.repository = repository;
	}

	@PostMapping
	public BatchBaseModel create(@RequestBody BatchBaseModel entity) {
		return repository.save(entity);
	}

	@GetMapping("/{id}")
	public Optional<BatchBaseModel> getById(@PathVariable ID id) {
		return repository.findById(id);
	}

	@GetMapping
	public List<BatchBaseModel> getAll() {
		return repository.findAll();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable ID id) {
		repository.deleteById(id);
	}
}
