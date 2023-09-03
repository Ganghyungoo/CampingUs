package com.test.campingusproject_customer.ui.myprofile

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentReviewWriteBinding
import com.test.campingusproject_customer.databinding.RowReviewWriteImageBinding
import com.test.campingusproject_customer.dataclassmodel.ReviewModel
import com.test.campingusproject_customer.repository.OrderDetailRepository
import com.test.campingusproject_customer.repository.ProductRepository
import com.test.campingusproject_customer.repository.ReviewRepository
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.ProductViewModel
import com.test.campingusproject_customer.viewmodel.ReviewViewModel
import kotlinx.coroutines.runBlocking
import java.io.IOException

class ReviewWriteFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var fragmentReviewWriteBinding: FragmentReviewWriteBinding
    lateinit var albumLauncher: ActivityResultLauncher<Intent>

    var reviewImageList = mutableListOf<Uri>()

    var starScore = 0L                 // 별점
    var recommendationCheck = false      // 추천 눌렀는지 확인

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReviewWriteBinding = FragmentReviewWriteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 회원 이름 가져오기
        val sharedPreferences = mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("customerUserId", null)!!

        val orderId = arguments?.getString("orderId")
        val orderProductId = arguments?.getLong("orderProductId")
        val orderProductPrice = arguments?.getString("productPrice")
        val orderProductImage = arguments?.getString("productImage")
        val orderProductName = arguments?.getString("productName")
        val orderDate = arguments?.getString("orderDate")

        //앨범 런처 초기화
        albumLauncher = albumSetting()

        fragmentReviewWriteBinding.run {

            // 툴바
            toolbarReviewWrite.run {
                //백버튼 설정
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.REVIEW_WRITE_FRAGMENT)
                }
            }

            textViewReviewWriteTitle.setText(orderProductName)
            textViewReviewWriteDate.setText(orderDate)
            textViewReviewWritePrice.setText(orderProductPrice)

            if(orderProductImage != null){
                val currentProductImage = runBlocking { ProductRepository.getProductFirstImage(orderProductImage) }
                Glide.with(mainActivity).load(currentProductImage).error(R.drawable.error_24px)
                    .override(200, 200)
                    .into(imageViewReviewWrite)
            }

            // 레이팅바 (별점)
            ratingBarReviewWrite.run {
                setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    starScore = rating.toLong()
                }
            }

            // 추천
            imageViewReviewWriteLiked.run {
                setOnClickListener {
                    if(recommendationCheck){
                        setImageResource(R.drawable.favorite_24px)
                        recommendationCheck = false
                    }
                    else{
                        setImageResource(R.drawable.favorite_fill_24px)
                        recommendationCheck = true
                    }
                }
            }

            // 이미지 리사이클러뷰 설정
            recyclerViewReviewWritePicture.run {
                adapter = ReviewWriteAdapter()

                //recycler view 가로로 확장되게 함
                layoutManager = LinearLayoutManager(mainActivity, RecyclerView.HORIZONTAL, false)
            }

            // 이미지 추가 버튼 클릭 이벤트 - 앨범 이동
            imageButtonReviewWriteRegisterPicture.setOnClickListener{
                val albumIntent = Intent(Intent.ACTION_PICK)
                albumIntent.setType("image/*")

                albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                albumLauncher.launch(albumIntent)
            }

            // 취소 버튼
            buttonReviewWriteCancel.run {
                setOnClickListener {
                    mainActivity.removeFragment(MainActivity.REVIEW_WRITE_FRAGMENT)
                }
            }

            // 완료 버튼
            buttonReviewWriteDone.run {
                setOnClickListener {
                    ReviewRepository.getReviewIdx {
                        var reviewId = it.result.value as Long

                        // 유효성 검사
                        val reviewWriteText = textInputEditTextReviewWrite.text.toString()
                        if(reviewWriteText.isEmpty()) {
                            textInputLayoutReviewWrite.run {
                                error = "리뷰 내용을 작성해주세요."
                                setErrorIconDrawable(R.drawable.error_24px)
                                requestFocus()
                            }
                            return@getReviewIdx
                        } else {
                            textInputLayoutReviewWrite.error = null
                        }

                        if(starScore == 0L) {
                            // 다이얼로그
                            val builder = MaterialAlertDialogBuilder(mainActivity,R.style.ThemeOverlay_App_MaterialAlertDialog)
                            builder.run {
                                setTitle("별점 오류")
                                setMessage("별점을 선택해주세요.")
                                setNegativeButton("닫기") { dialogInterface: DialogInterface, i: Int ->
                                    ratingBarReviewWrite.requestFocus()
                                }
                                show()
                                return@getReviewIdx
                            }
                        }

                        val fileDir = if(reviewImageList.isEmpty()) {
                            Snackbar.make(mainActivity.activityMainBinding.root, "이미지를 등록해주세요.", Snackbar.LENGTH_SHORT).show()
                            return@getReviewIdx
                        } else {
                            //이미지 저장될 파일 경로를 저장
                            "ReviewImage/$orderProductId/$userId/"
                        }

                        if(orderProductName != null){
                            val currentProduct = runBlocking { ProductRepository.getOneProductData(orderProductName) }

                            for(c1 in currentProduct.children){
                                val currentProductId = c1.child("productId").value as Long
                                val currentProductRecommendationCount = c1.child("productRecommendationCount").value as Long

                                // 리뷰 객체
                                val review = ReviewModel(reviewId, currentProductId, userId, starScore, fileDir, textInputEditTextReviewWrite.text.toString())

                                ReviewRepository.setReviewInfo(review) {
                                    reviewId++

                                    if(orderId != null){
                                        OrderDetailRepository.setReviewState(orderId)

                                        // 증가된 reviewId 값 저장
                                        ReviewRepository.setReviewIdx(reviewId) {
                                            ReviewRepository.uploadImages(reviewImageList, fileDir) {

                                                if(recommendationCheck){
                                                    ProductRepository.likeButtonClicked(currentProductId, currentProductRecommendationCount){
                                                        Snackbar.make(fragmentReviewWriteBinding.root, "저장되었습니다.", Snackbar.LENGTH_SHORT).show()
                                                        mainActivity.removeFragment(MainActivity.REVIEW_WRITE_FRAGMENT)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return fragmentReviewWriteBinding.root
    }

    // 이미지 어댑터 클래스
    inner class ReviewWriteAdapter : RecyclerView.Adapter<ReviewWriteAdapter.ReviewWriteViewHolder>(){
        inner class ReviewWriteViewHolder (rowReviewWriteImageBinding: RowReviewWriteImageBinding) :
            RecyclerView.ViewHolder(rowReviewWriteImageBinding.root){
            var imageViewRowReviewWriteImage : ImageView
            var imageButtonRowReviewWriteImageDelete : ImageButton

            init {
                imageViewRowReviewWriteImage = rowReviewWriteImageBinding.imageViewRowReviewWriteImage
                imageButtonRowReviewWriteImageDelete = rowReviewWriteImageBinding.imageButtonRowReviewWriteImageDelete

                imageButtonRowReviewWriteImageDelete.setOnClickListener {
                    reviewImageList.removeAt(adapterPosition)
                    fragmentReviewWriteBinding.recyclerViewReviewWritePicture.adapter?.notifyDataSetChanged()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewWriteViewHolder {
            val rowReviewWriteImageBinding = RowReviewWriteImageBinding.inflate(layoutInflater)

            return ReviewWriteViewHolder(rowReviewWriteImageBinding)
        }

        override fun getItemCount(): Int {
            return reviewImageList.size
        }

        override fun onBindViewHolder(holder: ReviewWriteViewHolder, position: Int) {
            //bitmap factory option 사용해 비트맵 크기 줄임
            val option = BitmapFactory.Options()
            option.inSampleSize = 4

            val inputStream = mainActivity.contentResolver.openInputStream(reviewImageList[position])
            val bitmap = BitmapFactory.decodeStream(inputStream, null, option)

            //글라이드 라이브러리로 recycler view에 이미지 출력
            Glide.with(mainActivity).load(bitmap)
                .override(500, 500)
                .into(holder.imageViewRowReviewWriteImage)

        }
    }

    //앨범 설정 함수
    fun albumSetting() : ActivityResultLauncher<Intent>{
        // 앨범에서 이미지 가져오기
        val albumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            //이미지 가져오기 성공
            if(it.resultCode == Activity.RESULT_OK){
                //사진 여러장 선택한 경우
                if(it.data?.clipData != null){
                    val count = it.data?.clipData?.itemCount

                    for(idx in 0 until count!!){
                        val imageUri = it.data?.clipData?.getItemAt(idx)?.uri

                        reviewImageList.add(imageUri!!)
                    }
                }
                // 한장 선택한 경우
                else{
                    it.data?.data?.let { uri ->
                        val imageUri = uri

                        if(imageUri != null){
                            reviewImageList.add(imageUri)
                        }
                    }
                }

                // recycler view 갱신
                fragmentReviewWriteBinding.recyclerViewReviewWritePicture.adapter?.notifyDataSetChanged()
            }
        }
        return albumLauncher
    }

}