package com.staples.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping(value="/")
public class ViewController 
{
	@RequestMapping(value="",method=RequestMethod.GET)
	public String login()
	{
		System.out.println("login!");
		return "budup";
	}
	
	@RequestMapping(value="home",method=RequestMethod.GET)
	public String home()
	{
		System.out.println("home!");
		return "home";
	}
	
}
