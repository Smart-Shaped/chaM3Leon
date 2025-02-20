package com.smartshaped.chameleon.serving.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.smartshaped.chameleon.serving.model.SpeedModel;
import com.smartshaped.chameleon.serving.repository.SpeedRepository;

public abstract class SpeedController<T extends SpeedModel, ID> {

	private final SpeedRepository<T, ID> repository;

	protected SpeedController(SpeedRepository<T, ID> repository) {
		this.repository = repository;
	}

	@PostMapping
	public SpeedModel create(@RequestBody T entity) {
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
