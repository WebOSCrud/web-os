package cn.donting.web.os.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class Controller {
    public Controller() {
        log.info("Controller ..........init");
    }

    @Autowired
    HttpServletRequest httpServletRequest;

    @GetMapping("/test")
    public String test() {
        return httpServletRequest.getSession().getId()+"update";
    }
}
