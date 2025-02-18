package com.smartshaped.chameleon.serving.repository;

import com.smartshaped.chameleon.serving.model.Request;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RequestRepository extends CassandraRepository<Request, UUID> {}
