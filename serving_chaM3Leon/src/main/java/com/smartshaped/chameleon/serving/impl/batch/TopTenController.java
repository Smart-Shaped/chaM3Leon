package com.smartshaped.chameleon.serving.impl.batch;

import com.smartshaped.chameleon.serving.controller.BatchController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class TopTenController extends BatchController<TopTenModel, String> {

  protected TopTenController(TopTenRepository repository) {
    super(repository);
  }
}
