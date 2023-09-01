package com.test.campingusproject_customer.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.campingusproject_customer.dataclassmodel.ReviewModel
import com.test.campingusproject_customer.repository.ReviewRepository
import kotlinx.coroutines.runBlocking

class ReviewViewModel: ViewModel() {
    val reviewList = MutableLiveData<MutableList<ReviewModel>>()

    val reviewProductId = MutableLiveData<Long>()
    val reviewWriterId = MutableLiveData<String>()
    val reviewRating = MutableLiveData<Long>()
    val reviewImage = MutableLiveData<String>()
    val reviewContent = MutableLiveData<String>()

    val reviewImageList = MutableLiveData<MutableList<Uri>>()

    init {
        reviewList.value = mutableListOf()
        reviewImageList.value = mutableListOf<Uri>()
    }

    // 리뷰 전체 DB가져오기
    fun getAllReviewInfo() {
        val tempList = mutableListOf<ReviewModel>()

        ReviewRepository.getAllReviewInfo {
            if(it.result.exists()) {
                for(c1 in it.result.children){
                    val reviewId = c1.child("reviewId").value as Long
                    val reviewProductId = c1.child("reviewProductId").value as Long
                    val reviewWriterId = c1.child("reviewWriterId").value as String
                    val reviewRating = c1.child("reviewRating").value as Long
                    val reviewImage = c1.child("reviewImage").value as String
                    val reviewContent = c1.child("reviewContent").value as String

                    val review = ReviewModel(reviewId, reviewProductId, reviewWriterId, reviewRating, reviewImage, reviewContent)

                    tempList.add(review)
                }

                reviewList.value = tempList
            }
        }
    }

    // 상품Id로 접근하여 리뷰 DB 가져오기
    fun getReviewInfo(productId: Long) {
        val tempList = mutableListOf<ReviewModel>()

        ReviewRepository.getReviewInfo(productId) {
            if(it.result.exists()) {
                for(c1 in it.result.children){
                    val reviewId = c1.child("reviewId").value as Long
                    val reviewProductId = c1.child("reviewProductId").value as Long
                    val reviewWriterId = c1.child("reviewWriterId").value as String
                    val reviewRating = c1.child("reviewRating").value as Long
                    val reviewImage = c1.child("reviewImage").value as String
                    val reviewContent = c1.child("reviewContent").value as String

                    val review = ReviewModel(reviewId, reviewProductId, reviewWriterId, reviewRating, reviewImage, reviewContent)

                    tempList.add(review)
                }

                reviewList.value = tempList
            }
        }
    }

    fun getAllImages(reviewModel: MutableList<ReviewModel>){
        val tempList = mutableListOf<Uri>()
        var tempList2 = mutableListOf<Uri>()

        for(idx in 0 until reviewModel.size){
            Log.d("imageLog", "진입1")
            ReviewRepository.getAllImages(reviewModel[idx].reviewImage){storageReference ->
                Log.d("imageLog", "진입2")
                runBlocking {
                    storageReference.downloadUrl.addOnCompleteListener{
                        if(it.isSuccessful){
                            tempList.add(it.result)
                            Log.d("imageLog", "${it.result}")
                        }
                    }
                }
            }
            tempList2 = tempList
        }
        reviewImageList.value = tempList2
    }
}