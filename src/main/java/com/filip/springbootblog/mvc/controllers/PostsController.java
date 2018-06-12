package com.filip.springbootblog.mvc.controllers;

import com.filip.springbootblog.mail.service.implementations.FmMailServiceImpl;
import com.filip.springbootblog.mail.service.interfaces.FmMailService;
import com.filip.springbootblog.mail.service.interfaces.IFmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@SuppressWarnings("Duplicates")
@Controller
@RequestMapping(value = "/posts")
public class PostsController {


    @Autowired
    private IFmService fmMailService;


}
