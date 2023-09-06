package com.test.campingusproject_seller.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.test.campingusproject_seller.dataclassmodel.OrderProductModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class OrderProductRepository {
    companion object{
        //판매자 아이디로 구매된 제품리스트를 가져오기 위한 함수
        fun getOrderProductBySellerId(sellerId:String, callback: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val orderProductRef = database.getReference("OrderProductData")
            orderProductRef.orderByChild("orderSellerId").equalTo(sellerId).get()
                .addOnCompleteListener(callback)
        }

        //판매자의 배송상태를 "배송완료로 변경"하는 메서드
        fun setOrderProductState(orderProductId:Long,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val orderProductRef = database.getReference("OrderProductData")
            orderProductRef.orderByChild("orderProductId").equalTo(orderProductId.toDouble()).get().addOnCompleteListener{
                if(it.result.exists()){
                    for (d in it.result.children){
                        d.ref.child("orderProductState").setValue("배송 완료").addOnCompleteListener(callback1)
                    }
                }
            }
        }

        //제품 데이터의 count를 가져오는 메서드
       fun productCount(orderProductName:String,callback: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val productRef = database.getReference("ProductData")
            productRef.orderByChild("productName").equalTo(orderProductName).get().addOnCompleteListener(callback)
        }


        fun setProductCount(orderProductName:String,resultCount:Long,callback1: (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val orderProductRef = database.getReference("ProductData")
            orderProductRef.orderByChild("productName").equalTo(orderProductName).get().addOnCompleteListener{
                if(it.result.exists()){
                    for (d in it.result.children){
                        d.ref.child("productCount").setValue(resultCount).addOnCompleteListener(callback1)
                    }
                }
            }
        }

        fun getOrderInfoByOrderId(orderId:String,callback: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val orderRef = database.getReference("OrderData")
            orderRef.orderByChild("orderId").equalTo(orderId).get().addOnCompleteListener(callback)
        }
    }
}