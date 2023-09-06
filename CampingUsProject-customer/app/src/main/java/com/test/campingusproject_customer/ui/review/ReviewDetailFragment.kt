package com.test.campRingusproject_customer.ui.review

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentReviewDetailBinding
import com.test.campingusproject_customer.databinding.RowReviewImageBinding
import com.test.campingusproject_customer.repository.ReviewRepository
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.ReviewViewModel

class ReviewDetailFragment : Fragment() {
    lateinit var fragmentReviewDetailBinding: FragmentReviewDetailBinding
    lateinit var mainActivity: MainActivity

    // 뷰모델
    lateinit var reviewViewModel: ReviewViewModel

    // 번들 객체로 아이템 인덱스 가져오기
    var position = 0
    var reviewRating = 0L
    var reviewImage = ""
    var reviewContent = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReviewDetailBinding = FragmentReviewDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        reviewViewModel = ViewModelProvider(mainActivity)[ReviewViewModel::class.java]

        reviewViewModel.run {
            reviewList.observe(mainActivity) {
                fragmentReviewDetailBinding.recyclerViewReviewDetailImage.adapter?.notifyDataSetChanged()
            }
            reviewList.value
        }

        position =  arguments?.getInt("adapterPosition")!!
        Log.d("ㅁㅇposition", position.toString())
        reviewRating = reviewViewModel.reviewList.value?.get(position)!!.reviewRating
        reviewImage = reviewViewModel.reviewList.value?.get(position)!!.reviewImage
        reviewContent = reviewViewModel.reviewList.value?.get(position)!!.reviewContent

        fragmentReviewDetailBinding.run {
            materialToolbarReviewDetail.run {
                // 백버튼
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.REVIEW_DETAIL_FRAGMENT)
                }
            }

            ReviewRepository.getUserData(reviewViewModel.reviewList.value?.get(position)!!.reviewWriterId) {
                if (it.result.exists()) {
                    for (c1 in it.result.children) {
                        val reviewUserName = c1.child("customerUserName").value as String
                        val reviewUserProfileImage = c1.child("customerUserProfileImage").value as String

                        // 유저 프로필 이미지
                        ReviewRepository.getUserProfileImage(reviewUserProfileImage) {uri ->
                            Glide.with(mainActivity).load(uri.result).into(imageViewReviewDetailProfileImage)
                        }

                        // 유저 이름
                        textViewReviewDetailProfileName.text = reviewUserName

                        // 별점
                        RatingBarReviewDetail.rating = reviewRating.toFloat()

                        // 리뷰 이미지
                        recyclerViewReviewDetailImage.run {
                            reviewViewModel.getAllReviewInfo()

                            adapter = ReviewDetailImageAdapter()

                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        }

                        // 리뷰 내용
                        textViewReviewDetailContent.text = reviewContent
                    }
                }
            }
        }

        return fragmentReviewDetailBinding.root
    }

    // 여러 이미지를 보여주기 위한 리사이클러뷰
    inner class ReviewDetailImageAdapter : RecyclerView.Adapter<ReviewDetailImageAdapter.ReviewDetailImageViewHolder>() {
        inner class ReviewDetailImageViewHolder(rowReviewImageBinding: RowReviewImageBinding) : RecyclerView.ViewHolder(rowReviewImageBinding.root) {
            var imageViewRowReviewImage: ImageView
            init {
                imageViewRowReviewImage = rowReviewImageBinding.imageViewRowReviewImage
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewDetailImageViewHolder {
            val rowReviewImageBinding = RowReviewImageBinding.inflate(layoutInflater)
            val reviewDetailImageViewHolder = ReviewDetailImageViewHolder(rowReviewImageBinding)

            rowReviewImageBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return reviewDetailImageViewHolder
        }

        override fun getItemCount(): Int {
            return reviewViewModel.reviewList.value?.size!!
        }

        override fun onBindViewHolder(holder: ReviewDetailImageViewHolder, position: Int) {
            Glide.with(mainActivity).load(reviewImage).override(600, 600).into(holder.imageViewRowReviewImage)
        }
    }
}