package com.test.campingusproject_customer.ui.review

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentReviewBinding
import com.test.campingusproject_customer.databinding.RowReviewBinding
import com.test.campingusproject_customer.databinding.RowReviewImageBinding
import com.test.campingusproject_customer.repository.ReviewRepository
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.ReviewViewModel
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.RoundingMode

class ReviewFragment : Fragment() {
    lateinit var fragmentReviewBinding: FragmentReviewBinding
    lateinit var mainActivity: MainActivity

    // 뷰모델
    lateinit var reviewViewModel: ReviewViewModel

    // 다음화면으로 보낼 정보를 담는 번들
    var newBundle: Bundle = Bundle()

    var reviewCount = 0         // 리뷰 전체 숫자
    var reviewTotalRatingScore = 0.0       // 별점 총 점수

    var seekbarZero = 0
    var seekbarOne = 0
    var seekbarTwo = 0
    var seekbarThree = 0
    var seekbarFour = 0
    var seekbarFive = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentReviewBinding = FragmentReviewBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 뷰모델 객체 생성
        reviewViewModel = ViewModelProvider(mainActivity)[ReviewViewModel::class.java]

        // 아이템에 사용할 회원정보 가져오기
        reviewViewModel.run {
            reviewList.observe(mainActivity) {
                fragmentReviewBinding.recyclerViewRowReview.adapter?.notifyDataSetChanged()
                //상품 Id로 모든 리뷰 이미지 가져오기
                reviewViewModel.getAllImages(it)

            }
            reviewImageList.observe(mainActivity){
                fragmentReviewBinding.recyclerViewRowReviewImage.adapter?.notifyDataSetChanged()
            }
        }

        // 번들 객체로 상품 id 가져오기
        val productId = arguments?.getLong("productId")!!

        runBlocking {
            // 상품 Id로 리뷰작성 DB 가져오기
            reviewViewModel.getReviewInfo(productId)
        }


        fragmentReviewBinding.run {
            toolbarReview.run {
                title = "상품 리뷰"

                // 백버튼
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.REVIEW_FRAGMENT)
                }
            }

            // 지연 출력
            Handler(Looper.getMainLooper()).postDelayed({
                // 리뷰 전체 갯수
                textViewReviewNumber.text = "리뷰 : $reviewCount"

                // 리뷰 총 별점
                // 반올림 하기 위한 함수
                if(reviewCount!=0){
                    val bd = BigDecimal(reviewTotalRatingScore / reviewCount)
                    val rounded = bd.setScale(1, RoundingMode.HALF_UP)
                    textViewReviewScore.text = rounded.toDouble().toString()
                }else{
                    textViewReviewScore.text = "0.0"
                }

                for(idx in 0 until reviewViewModel.reviewList.value?.size!!){
                    when(reviewViewModel.reviewList.value?.get(idx)?.reviewRating){
                    0L -> seekbarZero++
                    1L -> seekbarOne++
                    2L -> seekbarTwo++
                    3L -> seekbarThree++
                    4L -> seekbarFour++
                    5L -> seekbarFive++
                    }
                }

                linearLayoutReviewSeekBarGroup.run {
                    seekBar5Review.run {
                        isEnabled= false
                        progress = if(seekbarFive==0) 0 else (100 * (reviewCount/seekbarFive))
                    }
                    seekBar4Review.run {
                        isEnabled= false
                        progress = if(seekbarFour==0) 0 else (100 * (reviewCount/seekbarFour))
                    }
                    seekBar3Review.run {
                        isEnabled= false
                        progress = if(seekbarThree==0) 0 else (100 * (reviewCount/seekbarThree))
                    }
                    seekBar2Review.run {
                        isEnabled= false
                        progress = if(seekbarTwo==0) 0 else (100 * (reviewCount/seekbarTwo))
                    }
                    seekBar1Review.run {
                        isEnabled= false
                        progress = if(seekbarOne==0) 0 else (100 * (reviewCount/seekbarOne))
                    }
                }

                // 총 별점에 따른 레이팅바 표현
            }, 1500)

            recyclerViewRowReview.run {
                adapter = ReviewAdapter()

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            recyclerViewRowReviewImage.run {
                adapter = ReviewImageAdapter()
                layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL,false)
            }
        }

        return fragmentReviewBinding.root
    }

    // 리뷰이미지 리사이클러뷰 어댑터
    inner class ReviewImageAdapter: RecyclerView.Adapter<ReviewImageAdapter.ReviewImageViewHolder>() {
        inner class ReviewImageViewHolder(rowReviewImageBinding: RowReviewImageBinding) :RecyclerView.ViewHolder(rowReviewImageBinding.root) {
            val imageViewRowReviewImage : ImageView

            init {
                imageViewRowReviewImage = rowReviewImageBinding.imageViewRowReviewImage
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
            val rowReviewImageBinding = RowReviewImageBinding.inflate(layoutInflater)

            rowReviewImageBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return ReviewImageViewHolder(rowReviewImageBinding)
        }

        override fun getItemCount(): Int {
            return reviewViewModel.reviewImageList.value?.size!!
        }

        override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
            Glide.with(mainActivity).load(reviewViewModel.reviewImageList.value?.get(position))
                .override(500, 500)
                .into(holder.imageViewRowReviewImage)
        }
    }

    // 리뷰 아이템 리사이클러뷰 어댑터
    inner class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
        inner class ReviewViewHolder(rowReviewBinding: RowReviewBinding) :
            RecyclerView.ViewHolder(rowReviewBinding.root) {
            var imageViewReviewProfileImage: ImageView
            var textViewReviewProfileName: TextView
            var RatingBarReviewProfile: RatingBar
            var textViewReviewContent: TextView

            init {
                imageViewReviewProfileImage = rowReviewBinding.imageViewReviewProfileImage
                textViewReviewProfileName = rowReviewBinding.textViewReviewProfileName
                RatingBarReviewProfile = rowReviewBinding.RatingBarReviewProfile
                textViewReviewContent = rowReviewBinding.textViewReviewContent

                rowReviewBinding.root.setOnClickListener {
                    newBundle.run {
                        newBundle.putInt("adapterPosition", adapterPosition)
                    }
                    mainActivity.replaceFragment(
                        MainActivity.REVIEW_DETAIL_FRAGMENT,
                        true,
                        true,
                        newBundle
                    )
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ReviewAdapter.ReviewViewHolder {
            val rowReviewBinding = RowReviewBinding.inflate(layoutInflater)
            val reviewViewHolder = ReviewViewHolder(rowReviewBinding)

            rowReviewBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return reviewViewHolder
        }

        override fun getItemCount(): Int {
            return reviewViewModel.reviewList.value?.size!!
        }

        override fun onBindViewHolder(holder: ReviewAdapter.ReviewViewHolder, position: Int) {
            // 리뷰 작성자 아이디로 유저 DB 조회
            ReviewRepository.getUserData(reviewViewModel.reviewList.value?.get(position)!!.reviewWriterId) {
                if (it.result.exists()) {
                    for (c1 in it.result.children) {
                        val reviewUserName = c1.child("customerUserName").value as String
                        val reviewUserProfileImage =
                            c1.child("customerUserProfileImage").value as String

                        holder.textViewReviewProfileName.text = reviewUserName
                        // 이미지
                        ReviewRepository.getUserProfileImage(reviewUserProfileImage) { uri ->
                            Glide.with(mainActivity).load(uri.result)
                                .into(holder.imageViewReviewProfileImage)
                        }
                        holder.imageViewReviewProfileImage.setImageResource(R.drawable.account_circle_24px)

                        holder.RatingBarReviewProfile.rating =
                            reviewViewModel.reviewList.value?.get(position)!!.reviewRating.toFloat()
                        reviewTotalRatingScore += reviewViewModel.reviewList.value?.get(position)!!.reviewRating
                        holder.textViewReviewContent.text =
                            reviewViewModel.reviewList.value?.get(position)!!.reviewContent

                        reviewCount = position + 1
                    }
                }
            }
        }
    }
}