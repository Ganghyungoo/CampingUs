package com.test.campingusproject_seller.ui.sellstatedetail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.test.campingusproject_seller.databinding.FragmentSellStateDetailBinding
import com.test.campingusproject_seller.databinding.RowSellStateOrderHistoryBinding
import com.test.campingusproject_seller.dataclassmodel.OrderModel
import com.test.campingusproject_seller.dataclassmodel.OrderProductModel
import com.test.campingusproject_seller.repository.OrderProductRepository
import com.test.campingusproject_seller.repository.ProductRepository
import com.test.campingusproject_seller.ui.main.MainActivity
import com.test.campingusproject_seller.ui.sellstate.SellStateFragment
import kotlinx.coroutines.runBlocking

class SellStateDetailFragment : Fragment() {
    lateinit var fragmentSellStateDetailBinding: FragmentSellStateDetailBinding
    lateinit var mainActivity: MainActivity
    lateinit var orderProductObj: OrderProductModel
    var orderModels = mutableListOf<OrderModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSellStateDetailBinding = FragmentSellStateDetailBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        //주문 제품에 대한 정보얻어온다
        val orderProduct = arguments?.getSerializable("orderproduct")
        //주문 제품 객체
        orderProductObj = orderProduct as OrderProductModel
        Log.d("testt", "$orderProductObj")


        fun getOrderInfo(callback: (List<OrderModel>) -> Unit) {
            OrderProductRepository.getOrderInfoByOrderId(orderProductObj.orderId) {
                if (it.result.exists()) {
                    val orderModelList = mutableListOf<OrderModel>()
                    for (c1 in it.result.children) {
                        val orderUserId = c1.child("orderUserId").value as String
                        val orderId = c1.child("orderId").value as String
                        val orderDate = c1.child("orderDate").value as String
                        val orderPayment = c1.child("orderPayment").value as String
                        val orderStatus = c1.child("orderStatus").value as String
                        val orderDeliveryRecipent =
                            c1.child("orderDeliveryRecipent").value as String
                        val orderDeliveryContact = c1.child("orderDeliveryContact").value as String
                        val orderDeliveryAddress = c1.child("orderDeliveryAddress").value as String
                        val orderCustomerUserName =
                            c1.child("orderCustomerUserName").value as String
                        val orderCustomerUserPhone =
                            c1.child("orderCustomerUserPhone").value as String

                        val orderModel = OrderModel(
                            orderUserId,
                            orderId,
                            orderDate,
                            orderPayment,
                            orderStatus,
                            orderDeliveryRecipent,
                            orderDeliveryContact,
                            orderDeliveryAddress,
                            orderCustomerUserName,
                            orderCustomerUserPhone
                        )
                        orderModelList.add(orderModel)

                    }
                    callback(orderModelList)
                }

            }
        }







        fragmentSellStateDetailBinding.run {
            toolbarSellStateDetail.run {
                title = "판매현황 상세보기"

                // 백버튼
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.SELL_STATE_DETAIL_FRAGMENT)
                    mainActivity.activityMainBinding.bottomNavigationViewMain.visibility =
                        View.VISIBLE
                }
            }
            getOrderInfo() {
                textViewSellStateDetailPayType.text = it[0].orderPayment
                textViewSellStateDetailPayDate.text = it[0].orderDate
                textViewSellStateDetailProductPrice.text = orderProductObj.orderProductPrice
                textViewSellStateDetailOrderName.text = it[0].orderCustomerUserName
                textViewSellStateDetailOrderPhonNumber.text = it[0].orderCustomerUserPhone
                textViewSellStateDetailRecipientName.text = it[0].orderDeliveryRecipent
                textViewSellStateDetailRecipientPhonNumber.text = it[0].orderDeliveryContact
                textViewSellStateDetailRecipientAddress.text = it[0].orderDeliveryAddress
                textViewSellStateDetailOrderProductName.text=orderProductObj.orderProductName
                textViewSellStateDetailOrderProductNumber.text=orderProductObj.orderProductPrice
                textViewSellStateDetailOrderProductNumber.text=orderProductObj.orderProductCount
                ProductRepository.getProductFirstImage(orderProductObj.orderProductImage) { uri ->
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
                                progressBarRow.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                progressBarRow.visibility = View.GONE
                                return false
                            }

                        })
                        .override(200, 200)
                        .into(imageViewSellStateDetailOrderProductImage)
                }
            }


        }
        return fragmentSellStateDetailBinding.root
    }
}