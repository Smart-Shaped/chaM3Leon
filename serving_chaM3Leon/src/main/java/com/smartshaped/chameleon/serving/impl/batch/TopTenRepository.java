package com.smartshaped.chameleon.serving.impl.batch;

import com.smartshaped.chameleon.serving.repository.BatchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopTenRepository extends BatchRepository<TopTenModel, String> {}
