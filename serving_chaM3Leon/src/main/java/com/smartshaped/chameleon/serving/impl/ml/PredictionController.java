package com.smartshaped.chameleon.serving.impl.ml;

import com.smartshaped.chameleon.serving.controller.MLController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predictions")
public class PredictionController extends MLController<PredictionModel, PredictionModelId> {

  protected PredictionController(PredictionRepository repository) {
    super(repository);
  }
}
