package com.pmc.market.domain.user.entity;

import com.pmc.market.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
public class ShipAddress extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipAddress_id")
    private Long id;

    @NotNull
    private String address;

    @NotNull
    private String detail;

    @NotNull
    private String zipCode;

    @NotNull
    private String addressName;

    @Builder.Default
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateAddress(String address, String detail, String zipCode, String addressName) {
        this.address = address;
        this.detail = detail;
        this.zipCode = zipCode;
        this.addressName = addressName;
    }
}