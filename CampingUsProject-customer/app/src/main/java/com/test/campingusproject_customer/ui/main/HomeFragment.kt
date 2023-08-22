package com.test.campingusproject_customer.ui.main

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentHomeBinding
import com.test.campingusproject_customer.databinding.RowPopularboardBinding
import com.test.campingusproject_customer.databinding.RowPopularsaleBinding
import com.test.campingusproject_customer.databinding.RowRealtimerankBinding

class HomeFragment : Fragment() {
    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

        fragmentHomeBinding.run {
            materialToolbarHome.run {
                title = "CampingUs"
                setOnMenuItemClickListener {
                    //장바구니로 가기
                    title = "장바구니로 가기"
                    true
                }
            }
            //인기특가 recyclreView
            recyclerViewPopularSale.run {
                adapter = PopularSaleAdapter()
                layoutManager = GridLayoutManager(mainActivity,3)

                //구분선 추가
                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
                divider.run {
                    setDividerColorResource(mainActivity, R.color.subColor)
                    dividerInsetStart = 30
                    dividerInsetEnd = 30
                }
                addItemDecoration(divider)
            }

            //실시간 랭킹 recyclreView
            recyclerViewRealTimeRank.run {
                adapter = RealTimeRankAdapter()
                layoutManager = GridLayoutManager(mainActivity,3)

                //구분선 추가
                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
                divider.run {
                    setDividerColorResource(mainActivity, R.color.subColor)
                    dividerInsetStart = 30
                    dividerInsetEnd = 30
                }
                addItemDecoration(divider)
            }

            //인기 게시글 recyclreView
            recyclerViewPopularBoard.run {
                adapter = PopularBoardAdapter()
                layoutManager = LinearLayoutManager(mainActivity)

                //구분선 추가
                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
                divider.run {
                    setDividerColorResource(mainActivity, R.color.subColor)
                    dividerInsetStart = 30
                    dividerInsetEnd = 30
                }
                addItemDecoration(divider)
            }

            //인기특가 더보기 눌렀을 때
            textViewHomePopularSaleShowMore.setOnClickListener {
                materialToolbarHome.title = "인기특가 더보기"
            }
            //실시간랭킹 더보기 눌렀을 때
            textViewHomeRealTimeRankShowMore.setOnClickListener {
                materialToolbarHome.title = "실시간랭킹 더보기"
            }
            //인기게시판 더보기 눌렀을 때
            textViewHomePopularBoardShowMore.setOnClickListener {
                materialToolbarHome.title = "인기게시판 더보기"
            }
        }

        return fragmentHomeBinding.root
    }
    //인기특가 리싸이클러뷰 어댑터
    inner class PopularSaleAdapter : RecyclerView.Adapter<PopularSaleAdapter.PopularSaleViewHolder>(){
        inner class PopularSaleViewHolder(rowPopularsaleBinding: RowPopularsaleBinding) : RecyclerView.ViewHolder(rowPopularsaleBinding.root) {
            val imageViewRowPopularSaleProductImage : ImageView //제품 사진
            val textViewRowPopularSaleProductName : TextView // 제품 이름
            val textViewRowPopularSaleProductBrand : TextView // 제품 브랜드
            val textViewRowPopularSaleProductOriginalPrice : TextView // 제품 원래 가격
            val textViewRowPopularSaleProductDiscountPrice : TextView // 제품 할인 가격
            val textViewRowPopularSaleLike : TextView // 제품 추천 수

            init {
                imageViewRowPopularSaleProductImage = rowPopularsaleBinding.imageViewRowPopularSaleProductImage
                textViewRowPopularSaleProductName = rowPopularsaleBinding.textViewRowPopularSaleProductName
                textViewRowPopularSaleProductBrand = rowPopularsaleBinding.textViewRowPopularSaleProductBrand
                textViewRowPopularSaleProductOriginalPrice = rowPopularsaleBinding.textViewRowPopularSaleProductOriginalPrice
                textViewRowPopularSaleProductDiscountPrice = rowPopularsaleBinding.textViewRowPopularSaleProductDiscountPrice
                textViewRowPopularSaleLike = rowPopularsaleBinding.textViewRowPopularSaleLike
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularSaleViewHolder {
            val rowPopularsaleBinding = RowPopularsaleBinding.inflate(layoutInflater)

            rowPopularsaleBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PopularSaleViewHolder(rowPopularsaleBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: PopularSaleViewHolder, position: Int) {
            //holder.imageViewRowPopularSaleProductImage
            holder.textViewRowPopularSaleProductName.text = "바람숭숭 텐트"
            holder.textViewRowPopularSaleProductBrand.text = "모기장 전문점"
            holder.textViewRowPopularSaleProductOriginalPrice.text = "1,000,000원"
            holder.textViewRowPopularSaleProductOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG) //취소선 긋기(글자 중간에 줄 긋기)
            holder.textViewRowPopularSaleProductDiscountPrice.text = "700,000원"
            holder.textViewRowPopularSaleLike.text = "32"
        }

    }

    //실시간랭킹 리싸이클러뷰 어댑터
    inner class RealTimeRankAdapter : RecyclerView.Adapter<RealTimeRankAdapter.RealTimeRankViewHolder>(){
        inner class RealTimeRankViewHolder(rowRealtimerankBinding: RowRealtimerankBinding) : RecyclerView.ViewHolder(rowRealtimerankBinding.root) {
            val imageViewRowRealTimeRankProductImage : ImageView //제품 사진
            val textViewRowRealTimeRankProductName : TextView // 제품 이름
            val textViewRowRealTimeRankProductBrand : TextView // 제품 브랜드
            val textViewRowRealTimeRankProductPrice : TextView // 제품 가격
            val textViewRowRealTimeRankLike : TextView // 제품 추천 수

            init {
                imageViewRowRealTimeRankProductImage = rowRealtimerankBinding.imageViewRowRealTimeRankProductImage
                textViewRowRealTimeRankProductName = rowRealtimerankBinding.textViewRowRealTimeRankProductName
                textViewRowRealTimeRankProductBrand = rowRealtimerankBinding.textViewRowRealTimeRankProductBrand
                textViewRowRealTimeRankProductPrice = rowRealtimerankBinding.textViewRowRealTimeRankProductPrice
                textViewRowRealTimeRankLike = rowRealtimerankBinding.textViewRowRealTimeRankLike
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealTimeRankViewHolder {
            val rowRealtimerankBinding = RowRealtimerankBinding.inflate(layoutInflater)

            rowRealtimerankBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return RealTimeRankViewHolder(rowRealtimerankBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: RealTimeRankViewHolder, position: Int) {
            //holder.imageViewRowRealTimeRankProductImage
            holder.textViewRowRealTimeRankProductName.text = "바람막이 텐트"
            holder.textViewRowRealTimeRankProductBrand.text = "악어가죽 텐트 전문점"
            holder.textViewRowRealTimeRankProductPrice.text = "999,999,999원"
            holder.textViewRowRealTimeRankLike.text = "${99 - position}"
        }
    }

    //인기게시판 리싸이클러뷰 어댑터
    inner class PopularBoardAdapter : RecyclerView.Adapter<PopularBoardAdapter.PopularBoardViewHolder>(){
        inner class PopularBoardViewHolder(rowPopularboardBinding: RowPopularboardBinding) : RecyclerView.ViewHolder(rowPopularboardBinding.root) {
            val imageViewRowPopularBoardWriterImage : ImageView // 작성자 프로필 사진
            val imageViewRowPopularBoardTitle : TextView // 게시글 제목
            val imageViewRowPopularBoardWriter : TextView // 게시글 작성자
            val textViewRowPopularBoardLike : TextView // 좋아요 수


            init {
                imageViewRowPopularBoardWriterImage = rowPopularboardBinding.imageViewRowPopularBoardWriterImage
                imageViewRowPopularBoardTitle = rowPopularboardBinding.imageViewRowPopularBoardTitle
                imageViewRowPopularBoardWriter = rowPopularboardBinding.imageViewRowPopularBoardWriter
                textViewRowPopularBoardLike = rowPopularboardBinding.textViewRowPopularBoardLike
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularBoardViewHolder {
            val rowPopularboardBinding = RowPopularboardBinding.inflate(layoutInflater)

            rowPopularboardBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PopularBoardViewHolder(rowPopularboardBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: PopularBoardViewHolder, position: Int) {
            // holder.imageViewRowPopularBoardWriterImage =
            holder.imageViewRowPopularBoardTitle.text = "강현구 = 차은우"
            holder.imageViewRowPopularBoardWriter.text = "강현구"
            holder.textViewRowPopularBoardLike.text = "${100 - position}"
        }

    }
}