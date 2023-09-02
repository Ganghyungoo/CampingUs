package com.test.campingusproject_seller.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.campingusproject_seller.dataclassmodel.OrderProductModel
import com.test.campingusproject_seller.repository.OrderProductRepository
import com.test.campingusproject_seller.repository.ProductRepository
import kotlinx.coroutines.runBlocking

class SellerStateViewModel :ViewModel(){
    val orderProductList=MutableLiveData<MutableList<OrderProductModel>>()


    //내가 판매한 제품 중 결제완료인 제품들을 가져오는 메서드
    fun fetchOrderProduct(sellerId:String){
        OrderProductRepository.getOrderProductBySellerId(sellerId){
            if (it.result.exists()){
                val tempList= mutableListOf<OrderProductModel>()
                for (c1 in it.result.children){
                    if ((c1.child("orderProductState").value as String)=="배송 완료"){
                        continue
                    }
                    val orderId = c1.child("orderId").value as String
                    val orderProductId = c1.child("orderProductId").value as Long
                    val orderSellerId = c1.child("orderSellerId").value as String
                    val orderDate = c1.child("orderDate").value as String
                    val orderUserId  = c1.child("orderUserId").value as String
                    val orderProductName  = c1.child("orderProductName").value as String
                    val orderProductCount  = c1.child("orderProductCount").value as String
                    val orderProductPrice  = c1.child("orderProductPrice").value as String
                    val orderProductImage  = c1.child("orderProductImage").value as String
                    val orderProductState  = c1.child("orderProductState").value as String
                    val reviewState: Boolean = c1.child("reviewState").value as Boolean

                    val orderProduct=OrderProductModel(orderId,orderProductId, orderSellerId, orderDate, orderUserId, orderProductName, orderProductCount, orderProductPrice, orderProductImage, orderProductState,reviewState)
                    tempList.add(orderProduct)
                }
                orderProductList.value=tempList
            }
        }
    }


}