package com.pmc.market.service;

import com.pmc.market.ShopApplication;
import com.pmc.market.model.dto.FavoriteShopDto;
import com.pmc.market.model.dto.ShopInput;
import com.pmc.market.model.entity.Shop;
import com.pmc.market.repository.FavoriteRepository;
import com.pmc.market.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ShopApplication.class})
class ShopServiceTest {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @DisplayName("전체 마켓 리스트")
    @Test
    void findAll_전체_마켓_리스트() {
        assertEquals(shopService.findAll().size(), shopRepository.count());
    }

    @DisplayName("마켓_생성")
    @Test
    void makeShop_마켓_생성() {
        long count = shopRepository.count();
        /* user 의 이메일과 owner 의 값이 같을 경우에만 패스
        * */
        shopService.makeShop(ShopInput.builder()
                .name("뉴 마켓")
                .businessName("비지니스이름")
                .businessNumber("1234-12345")
                .fullDescription("마켓마켓마켓")
                .owner("email@email.com")
                .period(1)
                .shortDescription("shotDescription")
                .telephone("010-0000-0000")
                .build());
        assertEquals(count + 1, shopRepository.count());
    }

    @DisplayName("가장 인기 있는 마켓 count 개 조회")
    @Test
    void findFavorite_인기마켓_리스트() {
        int count = 3;
        List<FavoriteShopDto> favoriteShopDtoList = shopService.findFavorite(count);
        assertTrue(favoriteShopDtoList.size() == count);
    }

}