package com.xgama.sockethttpconvert.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	@RequestMapping("index")
	public String index(){
		return "index";
	}
	
	@RequestMapping("jstack")
	public String jstack(){
		return "jstack";
	}

}
