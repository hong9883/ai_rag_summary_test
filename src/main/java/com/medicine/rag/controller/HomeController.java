package com.medicine.rag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @GetMapping("/query")
    public String query() {
        return "query";
    }

    @GetMapping("/history")
    public String history() {
        return "history";
    }

    @GetMapping("/statistics")
    public String statistics() {
        return "statistics";
    }
}
