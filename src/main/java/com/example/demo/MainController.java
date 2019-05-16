package com.example.demo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
	@RequestMapping("/")
    public String index() {
        return "index";
    }
	@RequestMapping("/start")
    public String start() {
        return "start";
    }
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
    @RequestMapping("/admin")
    public String adminn() {
        return "admin";
    }
    @RequestMapping("/godina")
    public String unosGodina() {
        return "Godina";
    }
    @RequestMapping("/unos")
    public String unoss() {
        return "Unos";
    }    
    @RequestMapping("/403")
    public String nema() {
        return "403";
    }
    @RequestMapping("/lozinke")
    public String loz() {
        return "lozinke";
    }
    @RequestMapping("/paramputni")
    public String paramPutni() {
        return "Paramputnitr";
    }    
    @RequestMapping("/servisi")
    public String servisi() {
        return "Servisi";
    }
    @RequestMapping("/doprinosi")
    public String doprinosiAdmin() {
        return "Doprinosi";
    }    
}