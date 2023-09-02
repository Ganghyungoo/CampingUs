package com.test.campingusproject_seller.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class OrderProductRepository {
    companion object{
        //판매자 아이디로 구매된 제품리스트를 가져오기 위한 함수
        fun getOrderProductBySellerId(sellerId:String, callback: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val orderProductRef = database.getReference("OrderProductData")
            orderProductRef.orderByChild("orderSellerId").equalTo(sellerId).get()
                .addOnCompleteListener(callback)
        }

        fun setOrderProductState(){
            val database = FirebaseDatabase.getInstance()
            val orderProductRef = database.getReference("OrderProductData")
            orderProductRef.orderByChild("orderSellerId")
        }
    }
}