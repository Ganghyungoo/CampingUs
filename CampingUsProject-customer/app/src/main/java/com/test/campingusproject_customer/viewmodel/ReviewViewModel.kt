package com.test.campingusproject_customer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.campingusproject_customer.dataclassmodel.ReviewModel
import com.test.campingusproject_customer.repository.ReviewRepository

class ReviewViewModel: ViewModel() {
    val reviewList = MutableLiveData<MutableList<ReviewModel>>()

    val reviewProductId = MutableLiveData<Long>()
    val reviewWriterId = MutableLiveData<String>()
    val reviewRating = MutableLiveData<Long>()
    val reviewImage = MutableLiveData<String>()
    val reviewContent = MutableLiveData<String>()

    init {
        reviewList.value = mutableListOf()
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
}