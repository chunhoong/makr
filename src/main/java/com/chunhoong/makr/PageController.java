package com.chunhoong.makr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class PageController {

    @GetMapping
    public ModelAndView page() {
        return new ModelAndView("page");
    }

}
