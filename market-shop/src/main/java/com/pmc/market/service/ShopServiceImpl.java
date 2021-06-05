package com.pmc.market.service;

import com.pmc.market.entity.Role;
import com.pmc.market.entity.User;
import com.pmc.market.error.exception.BusinessException;
import com.pmc.market.error.exception.EntityNotFoundException;
import com.pmc.market.error.exception.ErrorCode;
import com.pmc.market.exception.OnlyCanMakeShopOneException;
import com.pmc.market.model.dto.NoticeRequestDto;
import com.pmc.market.model.dto.NoticeResponseDto;
import com.pmc.market.model.dto.ShopRequestDto;
import com.pmc.market.model.dto.ShopResponseDto;
import com.pmc.market.model.entity.Category;
import com.pmc.market.model.entity.Shop;
import com.pmc.market.model.entity.ShopNotice;
import com.pmc.market.repository.CategoryRepository;
import com.pmc.market.repository.FavoriteCustomRepository;
import com.pmc.market.repository.NoticeRepository;
import com.pmc.market.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final FavoriteCustomRepository favoriteCustomRepository;
    private final CategoryRepository categoryRepository;
    private final NoticeRepository noticeRepository;

    @Transactional // lazy
    @Override
    public List<ShopResponseDto> findAll() {
        return shopRepository.findAll().stream().map(ShopResponseDto::of).collect(Collectors.toList());
    }

    @Override
    public void makeShop(ShopRequestDto shopRequestDto, User user) {
        // 개인당 1개의 shop 만 생성 가능하도록
        if (!user.getRole().equals(Role.SELLER)) {
            throw new BusinessException("마켓을 생성하려면 판매자로 전환해야 합니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (shopRepository.countByUserEmail(user.getEmail()) > 0) {
            throw new OnlyCanMakeShopOneException("계정당 1개의 마켓만 만들 수 있습니다.");
        }
        Category category = categoryRepository.findById(shopRequestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 카테고리가 없습니다."));
        shopRepository.save(shopRequestDto.toEntity(shopRequestDto, user, category));
    }

    @Override
    public List<ShopResponseDto> findFavorite(int count) {
        return favoriteCustomRepository.findShopsMostFavoriteCount(count);
    }

    @Override
    public List<ShopResponseDto> findNew(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.ASC, "regDate"));
        Page<Shop> all = shopRepository.findAll(pageable);
        List<ShopResponseDto> shops = all.getContent().stream().map(ShopResponseDto::of).collect(Collectors.toList());
        return shops;
    }

    @Override
    public ShopResponseDto getShopById(long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 마켓을 찾을 수 없습니다."));
        return ShopResponseDto.of(shop);
    }

    @Override
    public List<ShopResponseDto> getShopsByCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 카테고리가 없습니다."));
        return shopRepository.findByCategory(category).stream()
                .map(ShopResponseDto::of).collect(Collectors.toList());
    }

    @Override
    public List<ShopResponseDto> getShopsBySearch(String searchWord) {
        return shopRepository.findByName(searchWord).stream()
                .map(ShopResponseDto::of).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateShop(ShopRequestDto shopRequestDto, long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 마켓을 찾을 수 없습니다."));

        if (shop.getCategory().getId() != shopRequestDto.getCategoryId()) {
            Category category = categoryRepository.findById(shopRequestDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다."));
            shop.updateCategory(category);
        }
        shop.update(shopRequestDto);

        try {
            shopRepository.save(shop);
        } catch (Exception e) {
            throw new BusinessException("Shop Update 도중 에러가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public void deleteShop(long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 마켓을 찾을 수 없습니다."));
        shopRepository.delete(shop); // shop 삭제
    }

    @Override
    public List<ShopNotice> getNoticeList(long shopId) {
        return noticeRepository.findAllByShopId(shopId);
    }

    @Override
    public NoticeResponseDto insertNotice(long id, NoticeRequestDto noticeRequestDto) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 마켓을 찾을 수 없습니다."));
        ShopNotice shopNotice = noticeRequestDto.toEntity(noticeRequestDto, shop);
        noticeRepository.save(shopNotice);
        return NoticeResponseDto.of(shopNotice);
    }

    @Override
    public NoticeResponseDto getNotice(long noticeId) {
        ShopNotice shopNotice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("해당 마켓을 찾을 수 없습니다."));
        return NoticeResponseDto.of(shopNotice);
    }

    @Override
    public NoticeResponseDto updateNotice(long noticeId, NoticeRequestDto noticeRequestDto) {
        ShopNotice shopNotice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException("해당 마켓을 찾을 수 없습니다."));
        shopNotice.updateNotice(noticeRequestDto);
        noticeRepository.save(shopNotice);
        return NoticeResponseDto.of(shopNotice);
    }

    @Override
    public void deleteNotice(long noticeId) {
        try {
            noticeRepository.deleteById(noticeId);
        } catch (EmptyResultDataAccessException e) {
            // select 쿼리 수를 줄이기 위해 id 조회를 하지 않고 jpa 에서 찾을 수 없다는 exception 발생할 때를 잡는다.
            throw new EntityNotFoundException("해당 마켓을 찾을 수 없습니다.");
        }
    }

}
