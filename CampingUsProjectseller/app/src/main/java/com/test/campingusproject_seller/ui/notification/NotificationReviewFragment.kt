package com.test.campingusproject_seller.ui.notification

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.campingusproject_seller.R
import com.test.campingusproject_seller.databinding.FragmentNotificationReviewBinding
import com.test.campingusproject_seller.databinding.RowInquiryBinding
import com.test.campingusproject_seller.databinding.RowNotificationReviewBinding
import com.test.campingusproject_seller.databinding.RowSellStateOrderHistoryItemBinding
import com.test.campingusproject_seller.dataclassmodel.OrderProductModel
import com.test.campingusproject_seller.dataclassmodel.ReviewModel
import com.test.campingusproject_seller.repository.OrderProductRepository
import com.test.campingusproject_seller.repository.ProductRepository
import com.test.campingusproject_seller.ui.main.MainActivity
import com.test.campingusproject_seller.ui.sellstate.SellStateFragment
import com.test.campingusproject_seller.viewmodel.ReviewViewModel

class NotificationReviewFragment : Fragment() {
    lateinit var fragmentNotificationReviewBinding: FragmentNotificationReviewBinding
    lateinit var mainActivity: MainActivity
    lateinit var reviewViewModel: ReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentNotificationReviewBinding = FragmentNotificationReviewBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        reviewViewModel= ViewModelProvider(mainActivity)[ReviewViewModel::class.java]
        reviewViewModel.run {
            reviewList.observe(mainActivity){
                if (it != null) {
                    Log.d("testt","옵저버 발동!${it[0].reviewContent}")
                    (fragmentNotificationReviewBinding.recyclerViewReview.adapter as? NotificationReviewFragment.ReviewAdapter)?.updateItemList(it)
                }
            }
        }


        fragmentNotificationReviewBinding.recyclerViewReview.run {
            adapter=ReviewAdapter()
            layoutManager=LinearLayoutManager(mainActivity)
        }

        val pref = mainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        reviewViewModel.getReviewList(pref.getString("userId", null).toString())






        return fragmentNotificationReviewBinding.root
    }

    inner class ReviewAdapter:RecyclerView.Adapter<NotificationReviewFragment.ReviewAdapter.ViewHolder>(){
        private var itemList: List<ReviewModel> = emptyList()
        fun updateItemList(newList: List<ReviewModel>) {
            this.itemList = newList
            notifyDataSetChanged() // 갱신
        }
        inner class ViewHolder(rowNotificationReviewBinding: RowNotificationReviewBinding):RecyclerView.ViewHolder(rowNotificationReviewBinding.root){
            val reviewWriter:TextView
            val reviewDetail:TextView
            val progressBar:ProgressBar
            val reviewImage:ImageView
            val ratingBar:RatingBar
            init {
                reviewWriter=rowNotificationReviewBinding.textViewReviewWriterId
                reviewDetail=rowNotificationReviewBinding.textViewReviewDetail
                progressBar=rowNotificationReviewBinding.progressBarRow
                reviewImage=rowNotificationReviewBinding.imageViewReview
                ratingBar=rowNotificationReviewBinding.ratingBarReviewWrite
                rowNotificationReviewBinding.root.setOnClickListener {
                    //리뷰 상세보기 화면으로 전환해야함
                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val rowNotificationReviewBinding = RowNotificationReviewBinding.inflate(layoutInflater)
            val viewHolder = ViewHolder(rowNotificationReviewBinding)
            rowNotificationReviewBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
           return viewHolder
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.reviewWriter.text=itemList[position].reviewWriterId
            holder.reviewDetail.text=itemList[position].reviewContent
            ProductRepository.getProductFirstImage(itemList[position].reviewImage) { uri ->
                //글라이드 라이브러리로 이미지 표시
                //이미지 로딩 완료되거나 실패하기 전까지 프로그래스바 활성화
                Glide.with(mainActivity).load(uri.result)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            holder.progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            holder.progressBar.visibility = View.GONE
                            return false
                        }

                    })
                    .override(200, 200)
                    .into(holder.reviewImage)
            }
            val rating= itemList[position].reviewRating.toFloat()
            holder.ratingBar.rating=rating
        }
    }
}