package com.test.campingusproject_customer.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.test.campingusproject_customer.dataclassmodel.OrderModel
import com.test.campingusproject_customer.dataclassmodel.OrderProductModel
import kotlinx.coroutines.tasks.await

class OrderRepository {
    companion object{
        //Order Model 저장
        fun addOrderInfo(orderModel: OrderModel, callback : (Task<Void>) -> Unit){
            val database  = FirebaseDatabase.getInstance()

            val orderRef = database.getReference("OrderData")
            orderRef.push().setValue(orderModel).addOnCompleteListener(callback)
        }

        //Order Product 저장
        fun addOrderProductInfo(orderProductModel: OrderProductModel, callback : (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()

            val orderProductRef = database.getReference("OrderProductData")
            orderProductRef.push().setValue(orderProductModel).addOnCompleteListener(callback)
        }

        //상품 ID를 가져오는 함수
        suspend fun getProductId() : DataSnapshot{
            val database = FirebaseDatabase.getInstance()

            val orderProductId = database.getReference("OrderProductId")
            return orderProductId.get().await()
        }

        //상품 ID를 설정하는 함수
        fun setProductId(orderProductId : Long){
            val database = FirebaseDatabase.getInstance()
            val orderProductIdRef = database.getReference("OrderProductId")

            orderProductIdRef.get().addOnCompleteListener {
                it.result.ref.setValue(orderProductId)
            }
        }
    }
}