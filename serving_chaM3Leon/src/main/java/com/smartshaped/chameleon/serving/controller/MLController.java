package com.smartshaped.chameleon.serving.controller;

import com.smartshaped.chameleon.serving.model.MLModel;
import com.smartshaped.chameleon.serving.repository.MLRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;

public abstract class MLController<T extends MLModel, ID> {
  private final MLRepository<T, ID> repository;

  protected MLController(MLRepository<T, ID> repository) {
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
