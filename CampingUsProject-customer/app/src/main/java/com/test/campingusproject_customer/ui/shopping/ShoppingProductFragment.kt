package com.test.campingusproject_customer.ui.shopping

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentShoppingProductBinding
import com.test.campingusproject_customer.dataclassmodel.CartModel
import com.test.campingusproject_customer.repository.CartRepository
import com.test.campingusproject_customer.databinding.RowProductImageBinding
import com.test.campingusproject_customer.repository.ProductRepository
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.ProductViewModel

class ShoppingProductFragment : Fragment() {
    lateinit var fragmentShoppingProductBinding: FragmentShoppingProductBinding
    lateinit var mainActivity: MainActivity

    // 상품 뷰모델
    lateinit var productViewModel: ProductViewModel

    // 이미지 변수
    var productImages = mutableListOf<Uri>()

    // 다음 화면으로 넘겨줄 번들
    val newBundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShoppingProductBinding = FragmentShoppingProductBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE

        // 회원 이름 가져오기
        val sharedPreferences = mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
        val productUserName = sharedPreferences.getString("customerUserName", null)!!

        // 상품 뷰모델 객체 생성
        productViewModel = ViewModelProvider(mainActivity)[ProductViewModel::class.java]

        // 번들 객체로 position 값 가져와 상품 id 가져오기
        val position = arguments?.getInt("adapterPosition")!!
        val productId = productViewModel.productList.value?.get(position)!!.productId

        // 뷰모델 상품 정보 가져오기
        productViewModel.getOneProductData(productId)

        productViewModel.run {
            productName.observe(mainActivity) {
                fragmentShoppingProductBinding.textViewShoppingProductName.setText(it)
            }
            productInfo.observe(mainActivity) {
                fragmentShoppingProductBinding.textViewShoppingProductExplanationDetailContent.setText(it)
            }
            productCount.observe(mainActivity) {
                fragmentShoppingProductBinding.textViewShoppingProductNumber.setText("남은 수량 : $it 개")

                if(it == 0L){
                    Log.d("countTest", "zero")
                    fragmentShoppingProductBinding.buttonShoppingProductToBuy.isEnabled = false
                    fragmentShoppingProductBinding.buttonShoppingProductToBuy.setTextColor(Color.GRAY)
                    fragmentShoppingProductBinding.buttonShoppingProductToCart.isEnabled = false
                    fragmentShoppingProductBinding.buttonShoppingProductToCart.setTextColor(Color.GRAY)
                }
            }
            productSellerId.observe(mainActivity){
                getProductSellerName(it)
            }
            productSellerName.observe(mainActivity){
                fragmentShoppingProductBinding.textViewShoppingProductSellerName.setText(it)
            }
            productDiscountRate.observe(mainActivity) {
                if(productDiscountRate.value == 0L) {
                    fragmentShoppingProductBinding.textViewShoppingProductSale.visibility = View.INVISIBLE
                    fragmentShoppingProductBinding.textViewShoppingProductPrice.setText("${productPrice.value} 원")
                } else {
                    fragmentShoppingProductBinding.textViewShoppingProductSale.visibility = View.VISIBLE
                    val result = (productPrice.value!! - (productPrice.value!! * (productDiscountRate.value!! *0.01))).toInt()
                    fragmentShoppingProductBinding.textViewShoppingProductPrice.setText("$result 원")
                }
            }
            productCategory.observe(mainActivity) {
                fragmentShoppingProductBinding.textViewShoppingProductCategory.setText(it)
            }

            productImageList.observe(mainActivity) { uriList ->
                productImages = uriList
                fragmentShoppingProductBinding.recyclerViewShoppingProductImage.adapter?.notifyDataSetChanged()
            }
        }

        fragmentShoppingProductBinding.run {
            //툴바
            toolbarShoppingProduct.run {
                title = "쇼핑"
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.SHOPPING_PRODUCT_FRAGMENT)
                }
            }

            // 장바구니 담기 클릭시 다이얼로그
            buttonShoppingProductToCart.run {
                setOnClickListener { // 버튼 클릭시 다이얼로그
                    MaterialAlertDialogBuilder(mainActivity, R.style.ThemeOverlay_App_MaterialAlertDialog).run {
                        val cartModel = CartModel(sharedPreferences.getString("customerUserId", null).toString(), productId, 1)
                        CartRepository.addCartData(cartModel) {

                        }

                        setTitle("장바구니 담기 완료")
                        setMessage("장바구니로 이동하시겠습니까?")
                        setPositiveButton("쇼핑 계속하기") { dialogInterface: DialogInterface, i: Int ->
                            mainActivity.removeFragment(MainActivity.SHOPPING_PRODUCT_FRAGMENT)
                        }
                        setNegativeButton("장바구니로 이동") { dialogInterface: DialogInterface, i: Int ->
                            mainActivity.replaceFragment(MainActivity.CART_FRAGMENT, true, true, null)
                        }
                        show()
                    }
                }
            }

            // 구매 버튼 클릭시 이동
            buttonShoppingProductToBuy.run {
                setOnClickListener {
                    val newBundle = Bundle()
                    val strArray = arrayListOf<String>("1", productViewModel.productName.value.toString(),
                        productViewModel.productPrice.value.toString(), productViewModel.productDiscountRate.value.toString(),
                        productViewModel.productImage.value.toString(), productViewModel.productId_.value.toString(),
                        productViewModel.productSellerId.value.toString()
                    )
                    newBundle.putStringArrayList("strArray", strArray)
                    mainActivity.replaceFragment(MainActivity.PAYMENT_FRAGMENT, true, true, newBundle)
                }
            }

            // 리뷰버튼 클릭시 화면 이동
            buttonToggleShoppingProductReview.run {
                setOnClickListener {
                    newBundle.run {
                        putLong("productId", productId)
                    }
                    mainActivity.replaceFragment(MainActivity.REVIEW_FRAGMENT, true, true, newBundle)
                }
            }


            // 플로팅 버튼 클릭시 문의등록 화면 이동
            floatingActionButtonShoppingProductInquiry.run {
                setOnClickListener {
                    newBundle.run {
                        putLong("productId", productId)
                        putString("productName", productViewModel.productName.value)
                        putString("productImage", productViewModel.productImage.value)
                    }
                    mainActivity.replaceFragment(MainActivity.INQUIRY_FRAGMENT, true, true, newBundle)
                }
            }

            // 여러 이미지 출력하기
            recyclerViewShoppingProductImage.run {
                adapter = ProductImageAdapter()
                // 리사이클러뷰 가로로 사용하기
                layoutManager = LinearLayoutManager(mainActivity, RecyclerView.HORIZONTAL, false)
            }

        }
        return fragmentShoppingProductBinding.root
    }

    // 이미지 리사이클러뷰
    inner class ProductImageAdapter : RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder>(){
        inner class ProductImageViewHolder (rowProductImageBinding: RowProductImageBinding) :
            RecyclerView.ViewHolder (rowProductImageBinding.root){
            var imageViewRowProductImage : ImageView

            init {
                imageViewRowProductImage = rowProductImageBinding.imageViewRowProductImage
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
            val rowProductImageBinding = RowProductImageBinding.inflate(layoutInflater)

            return ProductImageViewHolder(rowProductImageBinding)
        }

        override fun getItemCount(): Int {
            return productImages.size
        }

        override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
            //글라이드 라이브러리로 recycler view에 이미지 출력
            Glide.with(mainActivity).load(productImages[position])
                .override(600, 600)
                .into(holder.imageViewRowProductImage)
        }
    }
}