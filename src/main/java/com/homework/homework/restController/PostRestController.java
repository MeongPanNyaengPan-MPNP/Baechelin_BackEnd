package com.homework.homework.restController;

import com.homework.homework.domain.Post;
import com.homework.homework.dto.PostDto;
import com.homework.homework.dto.PostResponseDto;
import com.homework.homework.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@Slf4j
public class PostRestController {

    @Autowired
    private PostService postService;

    /**
     * 모든 게시물 조회
     * @return
     */
    @GetMapping("")
    public List<Post> getPosts() {
        return postService.getPosts();
    }

    /**
     * 게시물 단건 조회
     * @param postId
     * @return
     */
    @GetMapping("/{postId}")
    public Post getPost(
            @PathVariable Long postId
    ) {
        return postService.getPost(postId);
    }


    /**
     * 게시물 생성
     * @param postDto
     * @return
     */
    @PostMapping("/newpost")
    public Map<String, Object> addPost(@RequestBody PostDto postDto) {
        Map<String, Object> result = new HashMap<>();

        Long id = postService.addPost(postDto);

        if (id != null) {
            result.put("result", "게시물 생성 완료");
        } else {
            result.put("result", "게시물 등록에 실패했습니다. 관리자에게 문의해주세요.");
            log.error("[post] 게시물 등록");
        }
        return result;
    }

    /**
     * 게시물 수정
     * @param postId
     * @param postDto
     * @return
     */
    @PutMapping("/newpost/{postId}")
    public Map<String, Object> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDto postDto) {
        Map<String, Object> result = new HashMap<>();

        Long id = postService.updatePost(postId, postDto);

        if (id != null) {
            result.put("result", "게시물 수정 완료");
        } else {
            result.put("result", "게시물 수정에 실패했습니다. 관리자에게 문의해주세요.");
            log.error("[post] 게시물 수정 id: {}", id);
        }

        return result;
    }

    /**
     * 게시물 삭제
     * @param postId
     * @return
     */
    @DeleteMapping("/{postId}")
    public String deletePost(
            @PathVariable Long postId
    ) {
        postService.deletePost(postId);

        log.info("[post] 게시물 삭제 id : {}", postId);

        return "게시물 삭제 완료";
    }
}
