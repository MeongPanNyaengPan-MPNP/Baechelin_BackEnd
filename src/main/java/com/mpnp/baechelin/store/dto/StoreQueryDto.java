package com.mpnp.baechelin.store.dto;

import com.mpnp.baechelin.bookmark.domain.Bookmark;
import com.mpnp.baechelin.review.domain.Review;
import com.mpnp.baechelin.store.domain.StoreImage;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StoreQueryDto {
    private String category;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String elevator;
    private String toilet;
    private String parking;
    private String phoneNumber;
    private String heightDifferent;
    private String approach;
    private int bookMarkCount;
    private int reviewCount;
    private double pointAvg;
    private List<StoreImage> storeImageList;
    private List<Review> reviewList;
    private List<Bookmark> bookmarkList;

}
