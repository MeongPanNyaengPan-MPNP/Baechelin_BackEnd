package com.mpnp.baechelin.user.dto;


import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoResponseDto {
    // User info
    private String name;
    private List<ReviewResponseDto> reviewList;
    private List<BookmarkFolderResponseDto> bookmarkFolderList;

    public UserInfoResponseDto(User user) {
        this.name = user.getName();
        this.reviewList = user.getReviewList().stream().map(ReviewResponseDto::new).collect(Collectors.toList());
        this.bookmarkFolderList = user.getFolderList().stream().map(BookmarkFolderResponseDto::new).collect(Collectors.toList());
        //folder

    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class BookmarkFolderResponseDto {
        private int userId;
        private List<BookmarkResponseDto> folders; // 폴더 여러 개

        public BookmarkFolderResponseDto(Folder folder) {
            this.userId = folder.getUserId().getId();
            this.folders = folder.getBookmarkList().stream().map(BookmarkResponseDto::new).collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class BookmarkResponseDto {
        private int folderId;
        private int storeId;

        public BookmarkResponseDto(Bookmark bookmark){
            this.folderId = bookmark.getFolderId().getId();
            this.storeId = bookmark.getStoreId().getId();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class ReviewResponseDto {
        int id; // storeId
        int review; // reviewId
        Double point;

        String comment;
        String reviewImageUrl;


        LocalDateTime createdAt;
        LocalDateTime modifiedAt;

        public ReviewResponseDto(Review review) {
            this.id = review.getStoreId().getId();
            this.review = review.getId();
            this.point = review.getPoint();
            this.comment = review.getReview();
            this.reviewImageUrl = review.getReviewImageUrl();
        }
    }


}