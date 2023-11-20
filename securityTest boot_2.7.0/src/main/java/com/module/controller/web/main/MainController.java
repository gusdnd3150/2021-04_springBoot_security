package com.module.controller.web.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.module.service.MainService;

@Controller
public class MainController {
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	MainService mainService;

	@RequestMapping("/main/main")
	public String main() {
		try {
			////logger.info("mainService.TestSelect::" + mainService.TestSelect(null));
		} catch (Exception e) {
			////logger.info("main Exception::" + e);
			////logger.info("main Exception.getCause()::" + e.getCause());
		}
		return "/main/main";
	}

	@ResponseBody
	@RequestMapping("/test")
	public String test(Authentication auth) {
		////logger.info("@@@@@@@test" + auth.getAuthorities().toString());
		return "OK";
	}

	@ResponseBody
	@RequestMapping("/adminOnly")
	public String adminOnly(Authentication auth) {
		////logger.info("@@@@@@@여기" + auth.getAuthorities().toString());
		return "Secret Page";
	}

}
