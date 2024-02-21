package fr.uge.revue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FrontController {
    @RequestMapping("/front/**")
    public ModelAndView redirectToFrontEnd() {
        return new ModelAndView("forward:/index.html");
    }
}
