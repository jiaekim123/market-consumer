package com.pmc.market.model.entity;

import com.pmc.market.model.dto.NoticeRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Transient
    private boolean isNew;

    public void updateNotice(NoticeRequestDto noticeRequestDto) {
        this.title = noticeRequestDto.getTitle();
        this.content = noticeRequestDto.getContent();
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}