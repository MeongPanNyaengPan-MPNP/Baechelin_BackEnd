package com.homework.homework;

import com.homework.homework.domain.Post;
import com.homework.homework.dto.CommentDto;
import com.homework.homework.repository.PostRepository;
import com.homework.homework.service.CommentService;
import com.homework.homework.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
class HomeworkApplicationTests {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private PostRepository postRepository;

    @Test
    @Transactional
    void getPostTest() {
        Long postId = 1L;

        Post post = postService.getPost(postId);
    }


}
