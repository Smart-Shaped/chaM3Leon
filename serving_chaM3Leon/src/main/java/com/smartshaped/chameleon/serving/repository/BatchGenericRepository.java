package com.smartshaped.chameleon.serving.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BatchGenericRepository<BatchBaseModel,ID> extends CassandraRepository<BatchBaseModel,ID> {

}
