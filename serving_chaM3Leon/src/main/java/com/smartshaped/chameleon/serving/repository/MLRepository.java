package com.smartshaped.chameleon.serving.repository;

import com.smartshaped.chameleon.serving.model.MLModel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MLRepository<T extends MLModel, ID> extends CassandraRepository<T, ID> {}
