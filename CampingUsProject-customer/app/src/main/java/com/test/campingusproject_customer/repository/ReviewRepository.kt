package com.test.campingusproject_customer.repository

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.test.campingusproject_customer.dataclassmodel.ReviewModel

class ReviewRepository {
    companion object {
        // 리뷰 인덱스를 DB에 저장
        fun setReviewIdx(reviewId: Long, callback1: (Task<Void>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val reviewIdRef = database.getReference("ReviewIdx")

            reviewIdRef.get().addOnCompleteListener {
                it.result.ref.setValue(reviewId).addOnCompleteListener(callback1)
            }
        }

        // 리뷰 인덱스를 DB에서 가져온다.
        fun getReviewIdx(callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val reviewIdRef = database.getReference("ReviewIdx")

            reviewIdRef.get().addOnCompleteListener(callback1)
        }

        // 리뷰를 DB에 추가하는 함수
        fun setReviewInfo(reviewModel : ReviewModel, callback1 : (Task<Void>) -> Unit){
            val database = FirebaseDatabase.getInstance()
            val productRef = database.getReference("ReviewData")

            productRef.push().setValue(reviewModel).addOnCompleteListener(callback1)
        }

        // 리뷰 정보 전체를 가져오는 함수
        fun getAllReviewInfo(callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val reviewRef = database.getReference("ReviewData")

            reviewRef.orderByChild("reviewId").get().addOnCompleteListener(callback1)
        }

        // productId로 접근하여 ReviewData의 정보 가져오기
        fun getReviewInfo(productId: Long, callback1: (Task<DataSnapshot>) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val reviewRef = database.getReference("ReviewData")

            reviewRef.orderByChild("reviewProductId").equalTo(productId.toDouble()).get().addOnCompleteListener(callback1)
        }

        fun getAllImages(fileDir: String, callback: (StorageReference) -> Unit){
            val storage = FirebaseStorage.getInstance()
            val dirPath = fileDir.substring(0, fileDir.length-1)

            val imageRef = storage.reference.child(dirPath)
            imageRef.listAll()
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        task.result.items.forEach {
                            Log.d("imageTest", "${it.downloadUrl}")
                            callback(it)
                        }
                    }
                }
        }

        // 상품 이미지들을 업로드하는 함수
        fun uploadImages(uploadUri : MutableList<Uri>, fileDir : String, callback1: (Task<UploadTask.TaskSnapshot>) -> Unit){
            val storage = FirebaseStorage.getInstance()

            val count = uploadUri.size
            for(idx in 0 until count){
                val fileName = fileDir + "${idx+1}.png"
                val imageRef = storage.reference.child(fileName)
                imageRef.putFile(uploadUri[idx]).addOnCompleteListener(callback1)
            }
        }

        // 상품의 대표이미지(첫번째)만 가져오는 함수
        fun getProductFirstImage(fileDir:String, callback1: (Task<Uri>) -> Unit){
            val storage = FirebaseStorage.getInstance()
            val fileName = fileDir + "1.png"

            val imageRef = storage.reference.child(fileName)
            imageRef.downloadUrl.addOnCompleteListener(callback1)
        }

        // 사용자아이디로 사용자DB 접근
        fun getUserData(userId : String, callback: (Task<DataSnapshot>) -> Unit){
            val database = FirebaseDatabase.getInstance()

            val customerUserRef = database.getReference("CustomerUsers")
            customerUserRef.orderByChild("customerUserId").equalTo(userId)
                .get().addOnCompleteListener(callback)
        }

        // 유저 프로필 이미지 가져오기
        fun getUserProfileImage(fileDir:String, callback1: (Task<Uri>) -> Unit) {
            val storage = FirebaseStorage.getInstance()

            try {
                val fileRef = storage.reference.child(fileDir)

                fileRef.downloadUrl.addOnCompleteListener(callback1)
            } catch (e: IllegalArgumentException) {
            }
        }
    }
}