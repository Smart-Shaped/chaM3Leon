package com.smartshaped.chameleon.serving.repository;

import com.smartshaped.chameleon.serving.model.BatchModel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BatchRepository<T extends BatchModel, ID> extends CassandraRepository<T, ID> {}
