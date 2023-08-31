package com.test.campingusproject_customer.ui.main

import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.navigation.NavigationView
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.ActivityMainBinding
import com.test.campingusproject_customer.databinding.FragmentComunityBinding
import com.test.campingusproject_customer.databinding.FragmentHomeBinding
import com.test.campingusproject_customer.databinding.RowBoardBinding
import com.test.campingusproject_customer.databinding.RowPopularsaleBinding
import com.test.campingusproject_customer.databinding.RowRealtimerankBinding
import com.test.campingusproject_customer.dataclassmodel.PostModel
import com.test.campingusproject_customer.repository.CustomerUserRepository
import com.test.campingusproject_customer.repository.ProductRepository
import com.test.campingusproject_customer.viewmodel.PostViewModel
import com.test.campingusproject_customer.viewmodel.ProductViewModel
import java.lang.Integer.min

class HomeFragment : Fragment() {
    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    lateinit var callback: OnBackPressedCallback
    lateinit var postViewModel : PostViewModel
    lateinit var fragmentComunityBinding: FragmentComunityBinding
    lateinit var activityMainBinding: ActivityMainBinding

    lateinit var productViewModel: ProductViewModel

    var postPopularList = mutableListOf<PostModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
        fragmentComunityBinding = FragmentComunityBinding.inflate(layoutInflater)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        productViewModel = ViewModelProvider(mainActivity)[ProductViewModel::class.java]
        productViewModel.run {
            productList.observe(mainActivity) {
                fragmentHomeBinding.recyclerViewPopularSale.adapter?.notifyDataSetChanged()
            }
        }

        postViewModel = ViewModelProvider(mainActivity)[PostViewModel::class.java]
        postViewModel.run {
            postDataList.observe(mainActivity) {
                postPopularList = it
                fragmentHomeBinding.recyclerViewPopularBoard.adapter?.notifyDataSetChanged()
            }
        }
        postViewModel.resetPostList()
        postViewModel.getPostPopularAll()

        mainActivity.activityMainBinding.bottomNavigationViewMain.selectedItemId = R.id.menuItemHome

        fragmentHomeBinding.run {
            materialToolbarHomeFragment.run {
                textViewHomeToolbarTitle.text = "CampingUs"
                setOnMenuItemClickListener {
                    //장바구니로 가기
                    mainActivity.replaceFragment(MainActivity.CART_FRAGMENT, true, true, null)
                    true
                }
            }
            //인기특가 recyclreView
            recyclerViewPopularSale.run {
                productViewModel.getAllProductDiscountData()
                adapter = PopularSaleAdapter()
                layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL,false)

                //구분선 추가
                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.HORIZONTAL)
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
                layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL,false)

                //구분선 추가
                val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.HORIZONTAL)
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
                val newBundle = Bundle()
                newBundle.putString("saleStatus", "인기 특가")
                mainActivity.replaceFragment(MainActivity.SHOPPING_FRAGMENT, false, true, newBundle)
            }
            //실시간랭킹 더보기 눌렀을 때
            textViewHomeRealTimeRankShowMore.setOnClickListener {
                val newBundle = Bundle()
                newBundle.putString("saleStatus", "실시간랭킹")
                mainActivity.replaceFragment(MainActivity.SHOPPING_FRAGMENT, false, true, newBundle)
            }
            //인기게시판 더보기 눌렀을 때
            textViewHomePopularBoardShowMore.setOnClickListener {
                val sharedPreferences = mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
                if(CustomerUserRepository.checkLoginStatus(sharedPreferences) == false) {
                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.ThemeOverlay_App_MaterialAlertDialog)
                    builder.run {
                        setTitle("로그인 필요")
                        setMessage("로그인이 필요합니다.")
                        setPositiveButton("닫기") { dialogInterface: DialogInterface, i: Int -> }
                        show()
                    }

                }
                else {
                    val boardType: Long = 1L
                    val newBundle = Bundle()
                    newBundle.putLong("moreShow", boardType)
                    mainActivity.replaceFragment(MainActivity.COMUNITY_FRAGMENT, false, true, newBundle)
                }
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

                rowPopularsaleBinding.root.setOnClickListener {
                    val newBundle = Bundle()
                    newBundle.putString("saleStatus", "인기 특가")
                    mainActivity.replaceFragment(MainActivity.SHOPPING_FRAGMENT, false, true, newBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularSaleViewHolder {
            val rowPopularsaleBinding = RowPopularsaleBinding.inflate(layoutInflater)

            rowPopularsaleBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PopularSaleViewHolder(rowPopularsaleBinding)
        }

        override fun getItemCount(): Int {
            return productViewModel.productList.value?.size!!
        }

        override fun onBindViewHolder(holder: PopularSaleViewHolder, position: Int) {
            //holder.imageViewRowPopularSaleProductImage
            holder.textViewRowPopularSaleProductName.text = productViewModel.productList.value?.get(position)?.productName
            holder.textViewRowPopularSaleProductBrand.text = productViewModel.productList.value?.get(position)?.productBrand
            holder.textViewRowPopularSaleProductOriginalPrice.text =
                "정가 ${productViewModel.productList.value?.get(position)?.productPrice?.toString()}원"
            holder.textViewRowPopularSaleProductOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG) //취소선 긋기(글자 중간에 줄 긋기)
            holder.textViewRowPopularSaleProductDiscountPrice.text =
                "할인가 ${productViewModel.productList.value?.get(position)?.productPrice!! * (100L-productViewModel.productList.value?.get(position)?.productDiscountRate!!)}원"
            holder.textViewRowPopularSaleLike.text = productViewModel.productList.value?.get(position)?.productRecommendationCount?.toString()
            Log.d("productImage", "${productViewModel.productList.value?.get(position)?.productImage!!}")

            ProductRepository.getProductFirstImage(productViewModel.productList.value?.get(position)?.productImage!!) { uri ->
                Glide.with(mainActivity).load(uri.result)
                    .override(200, 200)
                    .into(holder.imageViewRowPopularSaleProductImage)
            }

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
                ViewGroup.LayoutParams.WRAP_CONTENT,
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
        inner class PopularBoardViewHolder(rowPopularboardBinding: RowBoardBinding) : RecyclerView.ViewHolder(rowPopularboardBinding.root) {
            val imageViewRowBoardWriterImage : ImageView // 작성자 프로필 사진
            val textViewRowBoardTitle : TextView // 게시글 제목
            val textViewRowBoardWriter : TextView // 게시글 작성자
            val textViewRowBoardLike : TextView // 좋아요 수
            val textVewRowBoardWriteDate : TextView // 글 작성 시간
            val textViewRowBoardComment : TextView // 댓글 수

            init {
                imageViewRowBoardWriterImage = rowPopularboardBinding.imageViewRowBoardWriterImage
                textViewRowBoardTitle = rowPopularboardBinding.textViewRowBoardTitle
                textViewRowBoardWriter = rowPopularboardBinding.textViewRowBoardWriter
                textViewRowBoardLike = rowPopularboardBinding.textViewRowBoardLike
                textVewRowBoardWriteDate = rowPopularboardBinding.textViewRowBoardWriteDate
                textViewRowBoardComment = rowPopularboardBinding.textViewRowBoardComment

                rowPopularboardBinding.root.setOnClickListener {
                    val sharedPreferences = mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
                    if(CustomerUserRepository.checkLoginStatus(sharedPreferences) == false) {
                        val builder = MaterialAlertDialogBuilder(mainActivity, R.style.ThemeOverlay_App_MaterialAlertDialog)
                        builder.run {
                            setTitle("로그인 필요")
                            setMessage("로그인이 필요합니다.")
                            setPositiveButton("닫기") { dialogInterface: DialogInterface, i: Int -> }
                            show()
                        }
                    }
                    else {
                        val readPostIdx =
                            postViewModel.postDataList.value?.get(adapterPosition)?.postIdx
                        val newBundle = Bundle()
                        newBundle.putLong("PostIdx", readPostIdx!!)
                        mainActivity.replaceFragment(
                            MainActivity.POST_READ_FRAGMENT,
                            true,
                            true,
                            newBundle
                        )
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularBoardViewHolder {
            val rowPopularboardBinding = RowBoardBinding.inflate(layoutInflater)

            rowPopularboardBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PopularBoardViewHolder(rowPopularboardBinding)
        }

        override fun getItemCount(): Int {
            val itemCount = min(postPopularList.size, 10)
            return itemCount
        }

        override fun onBindViewHolder(holder: PopularBoardViewHolder, position: Int) {
            if(postViewModel.postDataList.value?.get(position)?.profileImagePath.toString() != "null") {
                CustomerUserRepository.getUserProfileImage(postViewModel.postDataList.value?.get(position)?.profileImagePath!!) {
                    Glide.with(mainActivity)
                        .load(it.result)
                        .into(holder.imageViewRowBoardWriterImage)
                }
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.profileImagePath}")
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.postSubject}")
            }else {
                holder.imageViewRowBoardWriterImage.setImageResource(R.drawable.account_circle_24px)
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.profileImagePath}")
                Log.d("aaaa","$position ${postViewModel.postDataList.value?.get(position)?.postSubject}")
            }

            holder.textViewRowBoardTitle.text = postPopularList[position].postSubject
            holder.textViewRowBoardWriter.text = postPopularList[position].postUserId
            holder.textViewRowBoardLike.text = postPopularList[position].postLiked.toString()
            holder.textVewRowBoardWriteDate.text = postPopularList[position].postWriteDate
            holder.textViewRowBoardComment.text = postPopularList[position].postCommentCount.toString()
        }
    }

    //뒤로가기 버튼 눌렀을 때 동작할 코드 onDetech까지
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}