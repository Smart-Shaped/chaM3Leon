package com.smartshaped.chameleon.serving.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.smartshaped.chameleon.serving.model.BatchModel;
import com.smartshaped.chameleon.serving.repository.BatchRepository;

public abstract class BatchController<T extends BatchModel, ID> {

	private final BatchRepository<T, ID> repository;

	protected BatchController(BatchRepository<T, ID> repository) {
		this.repository = repository;
	}

	@PostMapping
	public T create(@RequestBody T entity) {
		return repository.save(entity);
	}

	@GetMapping("/{id}")
	public Optional<T> getById(@PathVariable ID id) {
		return repository.findById(id);
	}

	@GetMapping
	public List<T> getAll() {
		return repository.findAll();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable ID id) {
		repository.deleteById(id);
	}
}
