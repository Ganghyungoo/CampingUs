package com.test.campingusproject_seller.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    companion object{
        //리뷰 리스트를 가져오기 위한 함수
       fun getReviewData(sellerId:String,callback: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val reviewRef = database.getReference("ReviewData")
            reviewRef.orderByChild("sellerId").equalTo(sellerId).get().addOnCompleteListener(callback)
        }

        suspend fun getProductName(prodctId:Long,callback: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val productRef = database.getReference("ProductData")

            productRef.orderByChild("productId").equalTo(prodctId.toDouble()).get().addOnCompleteListener(callback)
        }


    }
}



