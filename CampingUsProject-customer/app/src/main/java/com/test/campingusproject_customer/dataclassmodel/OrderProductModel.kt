package com.test.campingusproject_customer.dataclassmodel

data class OrderProductModel (
    val orderId : String,               //주문 ID
    val orderProductId : Long,          //주문 상품 고유 ID
    val orderSellerId : String,         //주문 상품 판매자 ID
    val orderDate : String,             //주문 날짜
    val orderUserId : String,           //유저ID
    val orderProductName : String,      //상품 이름
    val orderProductCount : String,     //상품 개수
    val orderProductPrice : String,     //상품 가격
    val orderProductImage : String,     //상품 이미지
    val orderProductState : String      //배송상태
)