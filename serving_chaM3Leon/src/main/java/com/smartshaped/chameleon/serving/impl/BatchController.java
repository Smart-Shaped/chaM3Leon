package com.smartshaped.chameleon.serving.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartshaped.chameleon.serving.controller.BatchGenericController;

@RestController
@RequestMapping("/batch")
public class BatchController extends BatchGenericController<BatchModel, String>{

	protected BatchController(BatchRepository repository) {
		super(repository);
	}

}
