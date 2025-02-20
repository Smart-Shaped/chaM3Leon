package com.smartshaped.chameleon.serving.impl;

import org.springframework.stereotype.Repository;

import com.smartshaped.chameleon.serving.repository.BatchRepository;

@Repository
public interface TopTenRepository extends BatchRepository<TopTenModel, String>{

}
