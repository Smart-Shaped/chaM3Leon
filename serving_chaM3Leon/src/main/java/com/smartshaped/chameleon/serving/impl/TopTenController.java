package com.smartshaped.chameleon.serving.impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartshaped.chameleon.serving.controller.BatchController;

@RestController
@RequestMapping("/batch")
public class TopTenController extends BatchController<TopTenModel, String>{

	protected TopTenController(TopTenRepository repository) {
		super(repository);
	}

}
