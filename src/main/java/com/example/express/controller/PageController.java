package com.example.express.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @RequestMapping("/")
    public String showHome() {
        return "home.html";
    }

    @RequestMapping("/login")
    public String showLogin() {
        return "login.html";
    }

    @GetMapping("/register")
    public String showRegister() { return "register.html"; }

//    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @ResponseBody
//    public String printAdmin() {
//        return "如果你看见这句话，说明你有ROLE_ADMIN角色";
//    }
//
//    @GetMapping("/user")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    @ResponseBody
//    public String printUser() {
//        return "如果你看见这句话，说明你有ROLE_USER角色";
//    }
//
//    @GetMapping("/teacher")
//    @PreAuthorize("hasRole('ROLE_TEACHER')")
//    @ResponseBody
//    public String printTeacher() {
//        return "如果你看见这句话，说明你有ROLE_TEACHER角色";
//    }
//
//    @GetMapping("/adminOrTeacher")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
//    @ResponseBody
//    public String printAdminAndTeacher() {
//        return "如果你看见这句话，说明你有ROLE_ADMIN或ROLE_TEACHER和角色";
//    }
}