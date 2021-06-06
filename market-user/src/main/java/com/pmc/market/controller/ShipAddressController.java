package com.pmc.market.controller;

import com.pmc.market.model.ResponseMessage;
import com.pmc.market.model.dto.ShipAddressRequestDto;
import com.pmc.market.service.ShipAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Api(tags = "유저 관리")
@RequestMapping("/users")
@Slf4j
public class ShipAddressController {
    private final ShipAddressService shipAddressService;

    @ApiOperation(value = "배송지 추가")
    @PostMapping("/{userId}/address")
    public ResponseEntity<?> addShipAddress(@ApiParam("유저 id") @PathVariable("userId") long id, @RequestBody @Valid ShipAddressRequestDto request) {
        shipAddressService.addShipAddress(id, request);
        return ResponseEntity.ok(ResponseMessage.success());
    }

    @ApiOperation(value = "배송지 수정")
    @PutMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> updateShipAddress(@ApiParam("유저 id") @PathVariable("userId") long userId,
                                               @ApiParam("주소 id") @PathVariable("addressId") long addressId,
                                               @RequestBody @Valid ShipAddressRequestDto request) {
//        shipAddressService.addShipAddress(id, request);
        return ResponseEntity.ok(ResponseMessage.success());
    }

    @ApiOperation(value = "유저의 배송지 목록")
    @GetMapping("/{userId}/address")
    public ResponseEntity<?> getShipAddress(@ApiParam("유저 id") @PathVariable("userId") long id) {
//        shipAddressService.getShipAddressList(id);
        return ResponseEntity.ok(ResponseMessage.success());
    }

    @ApiOperation(value = "배송지 삭제")
    @DeleteMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> deleteShipAddress(@ApiParam("유저 id") @PathVariable("userId") long userId,
                                               @ApiParam("주소 id") @PathVariable("addressId") long addressId) {
//        shipAddressService.deleteShipAddress(userId, addressId);
        return ResponseEntity.ok(ResponseMessage.success());
    }

}
