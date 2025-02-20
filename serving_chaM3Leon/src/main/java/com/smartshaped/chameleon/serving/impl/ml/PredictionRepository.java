package com.smartshaped.chameleon.serving.impl.ml;

import com.smartshaped.chameleon.serving.repository.MLRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionRepository extends MLRepository<PredictionModel, PredictionModelId> {}
