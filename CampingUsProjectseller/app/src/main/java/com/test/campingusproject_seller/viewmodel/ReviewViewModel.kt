package com.test.campingusproject_seller.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.campingusproject_seller.dataclassmodel.OrderProductModel
import com.test.campingusproject_seller.dataclassmodel.ReviewModel
import com.test.campingusproject_seller.dataclassmodel.ReviewProductModel
import com.test.campingusproject_seller.repository.ReviewRepository
import kotlinx.coroutines.runBlocking

class ReviewViewModel : ViewModel() {
    var reviewList = MutableLiveData<MutableList<ReviewModel>>()

    fun getReviewList(sellerId: String) {
        ReviewRepository.getReviewData(sellerId) { review ->
            if (review.result.exists()) {
                val tempList = mutableListOf<ReviewModel>()
                for (c1 in review.result.children) {
                    val reviewContent = c1.child("reviewContent").value as String
                    val reviewId = c1.child("reviewId").value as Long
                    val reviewImage = c1.child("reviewImage").value as String
                    val reviewProductId = c1.child("reviewProductId").value as Long
                    val reviewRating = c1.child("reviewRating").value as Long
                    val reviewWriterId = c1.child("reviewWriterId").value as String
                    val sellerIdData = c1.child("sellerId").value as String

                    val reviewModel = ReviewModel(
                        reviewContent,
                        reviewId,
                        reviewImage,
                        reviewProductId,
                        reviewRating,
                        reviewWriterId,
                        sellerIdData
                    )
                    tempList.add(reviewModel)
                }
                reviewList.value = tempList
            }
        }
    }

}