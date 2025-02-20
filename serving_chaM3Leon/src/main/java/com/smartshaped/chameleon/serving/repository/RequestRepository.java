package com.smartshaped.chameleon.serving.repository;

import com.smartshaped.chameleon.serving.model.Request;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends CassandraRepository<Request, UUID> {}
