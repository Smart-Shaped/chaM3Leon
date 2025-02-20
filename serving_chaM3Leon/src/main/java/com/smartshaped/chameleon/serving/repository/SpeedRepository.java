package com.smartshaped.chameleon.serving.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.smartshaped.chameleon.serving.model.SpeedModel;

@NoRepositoryBean
public interface SpeedRepository<T extends SpeedModel,ID> extends CassandraRepository<T,ID> {

}
