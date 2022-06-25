package com.homework.homework.controller;

import com.homework.homework.domain.Post;
import com.homework.homework.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private PostService postService;

    @GetMapping("/main")
    public String main(Model model) {

        Long postId = 1L;
        Post post = postService.getPost(postId);

        model.addAttribute("post", post);

        return "main/main";
    }
}
