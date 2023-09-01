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
import com.test.campingusproject_customer.databinding.RowContractCampsiteBinding
import com.test.campingusproject_customer.databinding.RowPopularsaleBinding
import com.test.campingusproject_customer.dataclassmodel.ContractCampsite
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
    var contractCampsiteList = mutableListOf<ContractCampsite>()

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

        contractCampsiteList = mainActivity.fetchContractCampsite()

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

                addItemDecoration(createDivider())
            }

            //제휴 캠핑장 recyclerView
            recyclerViewContractCampsite.run{
                adapter = ContractCampsiteAdapter()
                layoutManager = LinearLayoutManager(mainActivity,LinearLayoutManager.HORIZONTAL,false)

                addItemDecoration(createDivider())
            }

            //인기 게시글 recyclreView
            recyclerViewPopularBoard.run {
                adapter = PopularBoardAdapter()
                layoutManager = LinearLayoutManager(mainActivity)

                addItemDecoration(createDivider())
            }

            //인기특가 더보기 눌렀을 때
            textViewHomePopularSaleShowMore.setOnClickListener {
                val newBundle = Bundle()
                mainActivity.activityMainBinding.bottomNavigationViewMain.selectedItemId = R.id.menuItemShopping
                newBundle.putString("saleStatus", "인기 특가")
                mainActivity.replaceFragment(MainActivity.SHOPPING_FRAGMENT, false, true, newBundle)
            }

            //제휴캠핑장 더보기 눌렀을 때
            textViewHomeContractCampsiteMore.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.CONTRACT_CAMPSITE_FRAGMENT, true, false, null)
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
            // 가격
            val price = productViewModel.productList.value?.get(position)?.productPrice!!
            // 할인율
            val discountRate = productViewModel.productList.value?.get(position)?.productDiscountRate!!
            // 결과값
            val result = (price - (price * (discountRate * 0.01))).toInt().toString()

            //holder.imageViewRowPopularSaleProductImage
            holder.textViewRowPopularSaleProductName.text = productViewModel.productList.value?.get(position)?.productName
            holder.textViewRowPopularSaleProductBrand.text = productViewModel.productList.value?.get(position)?.productBrand
            holder.textViewRowPopularSaleProductOriginalPrice.text =
                "정가 : $price 원"
            holder.textViewRowPopularSaleProductOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG) //취소선 긋기(글자 중간에 줄 긋기)
            holder.textViewRowPopularSaleProductDiscountPrice.text =
                "할인가 : $result 원"
            holder.textViewRowPopularSaleLike.text = productViewModel.productList.value?.get(position)?.productRecommendationCount?.toString()
            Log.d("productImage", "${productViewModel.productList.value?.get(position)?.productImage!!}")

            ProductRepository.getProductFirstImage(productViewModel.productList.value?.get(position)?.productImage!!) { uri ->
                Glide.with(mainActivity).load(uri.result)
                    .override(200, 200)
                    .into(holder.imageViewRowPopularSaleProductImage)
            }

        }

    }

    //제휴캠핑장 어댑터
    inner class ContractCampsiteAdapter : RecyclerView.Adapter<ContractCampsiteAdapter.ContractCampsiteViewHolder>(){

        inner class ContractCampsiteViewHolder (rowContractCampsiteBinding: RowContractCampsiteBinding) : RecyclerView.ViewHolder(rowContractCampsiteBinding.root){
            val imageViewRowContractCampsiteImage : ImageView
            val textViewRowContractCampsiteName : TextView
            val textViewRowContractCampsiteValue : TextView
            val textViewRowContractCampsitePhone : TextView
            val textViewRowContractCampsiteAddress : TextView

            init {
                imageViewRowContractCampsiteImage = rowContractCampsiteBinding.imageViewRowContractCampsiteImage
                textViewRowContractCampsiteName = rowContractCampsiteBinding.textViewRowContractCampsiteName
                textViewRowContractCampsiteValue = rowContractCampsiteBinding.textViewRowContractCampsiteValue
                textViewRowContractCampsitePhone = rowContractCampsiteBinding.textViewRowContractCampsitePhone
                textViewRowContractCampsiteAddress = rowContractCampsiteBinding.textViewRowContractCampsiteAddress

                rowContractCampsiteBinding.root.setOnClickListener {
                    val newBundle = Bundle()
                    newBundle.putString("latitude", contractCampsiteList[adapterPosition].위도)
                    newBundle.putString("longitude", contractCampsiteList[adapterPosition].경도)
                    mainActivity.replaceFragment(MainActivity.CONTRACT_CAMPSITE_FRAGMENT, true, false, newBundle)
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ContractCampsiteViewHolder {
            val rowContractCampsiteBinding = RowContractCampsiteBinding.inflate(layoutInflater)

            rowContractCampsiteBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return ContractCampsiteViewHolder(rowContractCampsiteBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: ContractCampsiteViewHolder, position: Int) {
            holder.textViewRowContractCampsiteName.text = contractCampsiteList[position].이름
            holder.textViewRowContractCampsiteValue.text = contractCampsiteList[position].형태
            holder.textViewRowContractCampsitePhone.text = contractCampsiteList[position].연락처
            holder.textViewRowContractCampsiteAddress.text = contractCampsiteList[position].주소
            Glide.with(mainActivity).load(contractCampsiteList[position].사진)
                .override(500, 500)
                .centerCrop()
                .into(holder.imageViewRowContractCampsiteImage)
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

    fun createDivider() : MaterialDividerItemDecoration{
        //구분선 추가
        val divider = MaterialDividerItemDecoration(mainActivity, LinearLayoutManager.HORIZONTAL)
        divider.run {
            setDividerColorResource(mainActivity, R.color.subColor)
            dividerInsetStart = 30
            dividerInsetEnd = 30
        }
        return divider
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