package com.test.campingusproject_customer.ui.myprofile

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentPurchaseHistoryBinding
import com.test.campingusproject_customer.databinding.RowPurchaseHistoryBinding
import com.test.campingusproject_customer.databinding.RowPurchaseHistoryItemBinding
import com.test.campingusproject_customer.dataclassmodel.OrderModel
import com.test.campingusproject_customer.dataclassmodel.OrderProductModel
import com.test.campingusproject_customer.repository.OrderDetailRepository
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.CampsiteViewModel
import com.test.campingusproject_customer.viewmodel.MyOrderListViewModel
import kotlinx.coroutines.runBlocking

class PurchaseHistoryFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentPurchaseHistoryBinding: FragmentPurchaseHistoryBinding
    lateinit var myOrderListViewModel: MyOrderListViewModel
    lateinit var orderList:MutableList<OrderModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        fragmentPurchaseHistoryBinding = FragmentPurchaseHistoryBinding.inflate(layoutInflater)
        myOrderListViewModel = ViewModelProvider(mainActivity)[MyOrderListViewModel::class.java]

        //사용자의 구매내역을 위한 아이디 얻기
        val sharedPreferences = mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
        val myId=sharedPreferences.getString("customerUserId",null)


        //콜백을 통해서 orderList가 완전히 초기화 되었을 때 리사이클러뷰 띄우기
        fetchMyOrderList(myId!!) { fetchedOrderList ->
            orderList= fetchedOrderList as MutableList<OrderModel>
            fragmentPurchaseHistoryBinding.recyclerViewPurchaseHistory.run {
                adapter = PurchaseHistoryAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }


        fragmentPurchaseHistoryBinding.run {



            // 툴바
            toolbarPayment.run {
                //백버튼 설정
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PURCHASE_HISTORY_FRAGMENT)
                }
            }

        }

        return fragmentPurchaseHistoryBinding.root
    }

    fun fetchMyOrderList(orderUserId: String, callback: (List<OrderModel>) -> Unit) {
        val orderList = mutableListOf<OrderModel>()
        OrderDetailRepository.getOrderInfoByUserId(orderUserId) { order ->
            if (order.result.exists() == true) {
                for (c1 in order.result.children) {
                    val tempList= mutableListOf<OrderProductModel>()
                    val orderUserId1 = c1.child("orderUserId").value as String
                    val orderId = c1.child("orderId").value as String
                    val orderDate = c1.child("orderDate").value as String
                    val orderPayment = c1.child("orderPayment").value as String
                    val orderStatus = c1.child("orderStatus").value as String
                    val orderDeliveryRecipent = c1.child("orderDeliveryRecipent").value as String
                    val orderDeliveryContact = c1.child("orderDeliveryContact").value as String
                    val orderDeliveryAddress = c1.child("orderDeliveryAddress").value as String
                    val orderCustomerUserName = c1.child("orderCustomerUserName").value as String
                    val orderCustomerUserPhone = c1.child("orderCustomerUserPhone").value as String

                    val myOrderInfo = OrderModel(orderUserId1, orderId, orderDate, orderPayment, orderStatus, orderDeliveryRecipent, orderDeliveryContact, orderDeliveryAddress, orderCustomerUserName, orderCustomerUserPhone
                    )
                    orderList.add(myOrderInfo)
                }
                callback(orderList) // orderList가 준비되었을 때 콜백 호출
            }
        }
    }

    fun getProductDataByOrderId(orderId: String,callback: (List<OrderProductModel>) -> Unit){
        val productTempList = mutableListOf<OrderProductModel>()
        OrderDetailRepository.getOrderedProductByOrderNum(orderId) { product ->
            if (product.result.exists() == true) {
                for (c2 in product.result.children) {
                    val orderId2 = c2.child("orderId").value as String
                    val orderProductName = c2.child("orderProductName").value as String
                    val orderProductCount = c2.child("orderProductCount").value as String
                    val orderProductPrice = c2.child("orderProductPrice").value as String
                    val orderProductImage = c2.child("orderProductImage").value as String
                    val orderProductState = c2.child("orderProductState").value as String
                    val orderProductId = c2.child("orderProductId").value as Long
                    val orderSellerId = c2.child("orderSellerId").value as String
                    val orderDate = c2.child("orderDate").value as String
                    val orderUserId = c2.child("orderUserId").value as String

                    val productObj = OrderProductModel(
                        orderId2, orderProductId, orderSellerId, orderDate, orderUserId,
                        orderProductName, orderProductCount, orderProductPrice, orderProductImage,
                        orderProductState)

                    productTempList.add(productObj)
                }
                callback(productTempList)
            }
        }
    }

    // 구매내역 어댑터
    inner class PurchaseHistoryAdapter() : RecyclerView.Adapter<PurchaseHistoryFragment.PurchaseHistoryAdapter.PurchaseHistoryViewHolder>() {

        inner class PurchaseHistoryViewHolder(rowPurchaseHistoryBinding: RowPurchaseHistoryBinding) :
            RecyclerView.ViewHolder(rowPurchaseHistoryBinding.root) {
            //구매 날짜 텍스트
            val textViewRowPurchaseHistoryDate: TextView
            //주문 상세보기 버튼
            val buttonRowPurchaseHistory: Button
            //내부에 제품 쌓는 레이아웃
            var layoutInner:LinearLayout


            init {
                //구매날짜 초기화
                textViewRowPurchaseHistoryDate = rowPurchaseHistoryBinding.textViewRowPurchaseHistoryDate
                //상세보기 버튼 초기화
                buttonRowPurchaseHistory = rowPurchaseHistoryBinding.buttonRowPurchaseHistory
                //레이아웃 초기화
                val addViewLayout= rowPurchaseHistoryBinding.LayoutInner


                layoutInner=rowPurchaseHistoryBinding.LayoutInner
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): PurchaseHistoryViewHolder {
            val rowPurchaseHistoryBinding = RowPurchaseHistoryBinding.inflate(layoutInflater)
            val PurchaseHistoryViewHolder = PurchaseHistoryViewHolder(rowPurchaseHistoryBinding)

            rowPurchaseHistoryBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return PurchaseHistoryViewHolder
        }

        override fun getItemCount(): Int {
            return orderList.size

        }

        override fun onBindViewHolder(holder: PurchaseHistoryViewHolder, position: Int) {

            holder.textViewRowPurchaseHistoryDate.text = orderList[position].orderDate
            holder.buttonRowPurchaseHistory.setOnClickListener {
                val bundle=Bundle()
                bundle.putString("orderId",orderList[position].orderId)
                mainActivity.replaceFragment(MainActivity.ORDER_DETAIL_FRAGMENT,true,false,bundle)
            }
            //콜백으로
            getProductDataByOrderId(orderList[position].orderId){
                val item= holder.layoutInner
                for (product in it){
                    val productItem=RowPurchaseHistoryItemBinding.inflate(layoutInflater)
                    productItem.textViewRowPurchaseHistoryItemName.text=product.orderProductName
                    productItem.textViewRowPurchaseHistoryItemPrice.text=product.orderProductPrice
                    productItem.textViewRowPurchaseHistoryItemNumber.text=product.orderProductCount
                    productItem.textViewRowPurchaseHistoryItemStateDone.text=product.orderProductState
                    if (productItem.textViewRowPurchaseHistoryItemStateDone.text=="결제 완료"){
                        productItem.textViewRowPurchaseHistoryItemReview.visibility=View.GONE
                    }else{
                        if(product.reviewState){
                            productItem.textViewRowPurchaseHistoryItemReview.text="리뷰 작성 완료"
                            productItem.textViewRowPurchaseHistoryItemReview.setTextColor(Color.BLUE)
                        }
                        productItem.textViewRowPurchaseHistoryItemReview.text="리뷰 작성하기"
                        productItem.textViewRowPurchaseHistoryItemReview.setTextColor(Color.RED)
                        productItem.textViewRowPurchaseHistoryItemReview.setOnClickListener {
                            val bundle=Bundle()
                            bundle.putString("orderId",product.orderId)
                            bundle.putString("productName",product.orderProductName)
                            mainActivity.replaceFragment(MainActivity.REVIEW_WRITE_FRAGMENT,true,false,bundle)
                        }
                    }


                    val params = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    item.addView(productItem.root,params)
                }
                holder.layoutInner=item
            }




        }
    }


//    // 구매내역 아이템 어댑터
//    inner class PurchaseHistoryItemAdapter() :
//        RecyclerView.Adapter<PurchaseHistoryFragment.PurchaseHistoryItemAdapter.PurchaseHistoryItemViewHolder>() {
//
//        inner class PurchaseHistoryItemViewHolder(rowPurchaseHistoryItemBinding: RowPurchaseHistoryItemBinding) :
//            RecyclerView.ViewHolder(rowPurchaseHistoryItemBinding.root) {
//            val imageViewRowPurchaseHistoryItemProduct: ImageView
//            val textViewRowPurchaseHistoryItemName: TextView
//            val textViewRowPurchaseHistoryItemPrice: TextView
//            val textViewRowPurchaseHistoryItemNumber: TextView
//            val textViewRowPurchaseHistoryItemState: TextView
//            val textViewRowPurchaseHistoryItemStateDone: TextView
//            val textViewRowPurchaseHistoryItemReview: TextView
//
//            init {
//                imageViewRowPurchaseHistoryItemProduct =
//                    rowPurchaseHistoryItemBinding.imageViewRowPurchaseHistoryItemProduct
//                textViewRowPurchaseHistoryItemName =
//                    rowPurchaseHistoryItemBinding.textViewRowPurchaseHistoryItemName
//                textViewRowPurchaseHistoryItemPrice =
//                    rowPurchaseHistoryItemBinding.textViewRowPurchaseHistoryItemPrice
//                textViewRowPurchaseHistoryItemNumber =
//                    rowPurchaseHistoryItemBinding.textViewRowPurchaseHistoryItemNumber
//                textViewRowPurchaseHistoryItemState =
//                    rowPurchaseHistoryItemBinding.textViewRowPurchaseHistoryItemState
//                textViewRowPurchaseHistoryItemStateDone =
//                    rowPurchaseHistoryItemBinding.textViewRowPurchaseHistoryItemStateDone
//                textViewRowPurchaseHistoryItemReview =
//                    rowPurchaseHistoryItemBinding.textViewRowPurchaseHistoryItemReview
//
//            }
//        }
//
//        override fun onCreateViewHolder(
//            parent: ViewGroup,
//            viewType: Int,
//        ): PurchaseHistoryItemViewHolder {
//            val rowPurchaseHistoryItemBinding =
//                RowPurchaseHistoryItemBinding.inflate(layoutInflater)
//            val purchaseHistoryItemViewHolder =
//                PurchaseHistoryItemViewHolder(rowPurchaseHistoryItemBinding)
//
//            rowPurchaseHistoryItemBinding.root.layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            return purchaseHistoryItemViewHolder
//        }
//
//        override fun getItemCount(): Int {
//            return 5
//        }
//
//        override fun onBindViewHolder(holder: PurchaseHistoryItemViewHolder, position: Int) {
//            holder.textViewRowPurchaseHistoryItemName.text = "장작"
//            holder.textViewRowPurchaseHistoryItemPrice.text = "15000원"
//            holder.textViewRowPurchaseHistoryItemNumber.text = "3개"
//            if (state) {
//                holder.textViewRowPurchaseHistoryItemState.visibility = View.GONE
//                holder.textViewRowPurchaseHistoryItemStateDone.visibility = View.VISIBLE
//                holder.textViewRowPurchaseHistoryItemReview.visibility = View.VISIBLE
//            } else {
//                holder.textViewRowPurchaseHistoryItemState.visibility = View.VISIBLE
//                holder.textViewRowPurchaseHistoryItemStateDone.visibility = View.GONE
//                holder.textViewRowPurchaseHistoryItemReview.visibility = View.GONE
//            }
//
//            holder.textViewRowPurchaseHistoryItemReview.setOnClickListener {
//                mainActivity.replaceFragment(MainActivity.REVIEW_WRITE_FRAGMENT, true, true, null)
//            }
//        }
//    }

    override fun onDetach() {
        super.onDetach()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility=View.VISIBLE
    }
}
