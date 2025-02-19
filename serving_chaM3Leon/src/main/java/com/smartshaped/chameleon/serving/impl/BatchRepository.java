package com.smartshaped.chameleon.serving.impl;

import org.springframework.stereotype.Repository;

import com.smartshaped.chameleon.serving.repository.BatchGenericRepository;

@Repository
public interface BatchRepository extends BatchGenericRepository<BatchModel, String>{

}
