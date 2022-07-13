package com.mpnp.baechelin.user.dto;


import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.bookmark.domain.Folder;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.tag.domain.Tag;
import com.mpnp.baechelin.user.entity.user.User;
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

        TagResponseDto tag;

        LocalDateTime createdAt;
        LocalDateTime modifiedAt;

        public ReviewResponseDto(Review review) {
            this.id = review.getStoreId().getId();
            this.review = review.getId();
            this.point = review.getPoint();
            this.comment = review.getReview();
            this.reviewImageUrl = review.getReviewImageUrl();
            this.tag = new TagResponseDto(review.getTagId(), review);
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static class TagResponseDto {
        int id; // 태그 아이디
        int review; // 리뷰 아이디

        @Builder.Default
        char bKiosk = 'N', bTable = 'N', bMenu = 'N', bWheelchair = 'N', bHelp = 'N', bAutoDoor = 'N';
        @Builder.Default
        char fDelicious = 'N', fClean = 'N', fVibe = 'N', fQuantity = 'N', fPrice = 'N', fGoodToEat = 'N';

        public TagResponseDto(Tag tag, Review review) {
            this.id = tag.getId();
            this.review = review.getId();
            this.bKiosk = tag.getBKiosk();
            this.bTable = tag.getBTable();
            this.bMenu = tag.getBMenu();
            this.bWheelchair = tag.getBWheelchair();
            this.bHelp = tag.getBHelp();
            this.bAutoDoor = tag.getBAutoDoor();
            this.fDelicious = tag.getFDelicious();
            this.fClean = tag.getFClean();
            this.fVibe = tag.getFVibe();
            this.fQuantity = tag.getFQuantity();
            this.fPrice = tag.getFPrice();
            this.fGoodToEat = tag.getFGoodToEat();
        }
    }

}