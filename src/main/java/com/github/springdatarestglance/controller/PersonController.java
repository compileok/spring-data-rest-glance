package com.github.springdatarestglance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author xudeming
 * @date 2019/3/19
 */
@Controller
@RequestMapping("/person")
public class PersonController {

	@RequestMapping("/hello")
	@ResponseBody
	public String hello(){
		return " Hello,welcome to the normal controller! ";
	}

}
