package com.pmc.market.controller;

import com.pmc.market.model.entity.Product;
import com.pmc.market.model.ResponseMessage;
import com.pmc.market.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Product Controller", tags = "상품 컨트롤러")
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/save")
    @ApiOperation("상품 등록")
    public ResponseEntity<?> saveProduct(@RequestBody @Valid Product product){ // @RequestBody HTTP 요청몸체를 자바객체로 전달받음
        productService.saveProduct(product);
        return ResponseEntity.ok(ResponseMessage.success());
    }

    @GetMapping("/findAll")
    @ApiOperation("전체 상품 리스트")
    public ResponseEntity<?> findProducts(){
        return ResponseEntity.ok(ResponseMessage.success(productService.findProducts()));
    }

    @GetMapping("/findOne/{id}")
    @ApiOperation("특정 상품 조회")
    public ResponseEntity<?> findOneProduct(@PathVariable Long id){
        return ResponseEntity.ok(ResponseMessage.success(productService.findOneProduct(id)));
    }
}
