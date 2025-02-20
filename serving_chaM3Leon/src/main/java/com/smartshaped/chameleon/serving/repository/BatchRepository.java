package com.smartshaped.chameleon.serving.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.smartshaped.chameleon.serving.model.BatchModel;

@NoRepositoryBean
public interface BatchRepository<T extends BatchModel,ID> extends CassandraRepository<T,ID> {

}
