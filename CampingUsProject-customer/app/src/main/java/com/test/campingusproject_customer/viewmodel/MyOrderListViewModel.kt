package com.test.campingusproject_customer.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.campingusproject_customer.dataclassmodel.OrderModel
import com.test.campingusproject_customer.dataclassmodel.OrderProductModel
import com.test.campingusproject_customer.repository.OrderDetailRepository
import kotlinx.coroutines.runBlocking

class MyOrderListViewModel: ViewModel() {

    val myOrderList = MutableLiveData<MutableList<OrderModel>>()
    val myOrderProductDoubleList = MutableLiveData<MutableList<MutableList<OrderProductModel>>>()
    val load=MutableLiveData<Boolean>(false)
    init {
        myOrderList.value = mutableListOf<OrderModel>()
        myOrderProductDoubleList.value = mutableListOf<MutableList<OrderProductModel>>()
    }


    //프리퍼런스에 내 아이디를 갖고 해당하는 주문목록을 가져오는 메서드
    fun fetchMyOrder(orderUserId: String) {
        val orderList = mutableListOf<OrderModel>()
        val productList = mutableListOf<MutableList<OrderProductModel>>()
        OrderDetailRepository.getOrderInfoByUserId(orderUserId) { order ->
            if (order.result.exists() == true) {
                for (c1 in order.result.children) {

                    val tempList= mutableListOf<OrderProductModel>()

                    val orderUserId1 = c1.child("orderUserId").value as String
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

                    val myOrderInfo = OrderModel(orderUserId1, orderId, orderDate, orderPayment, orderStatus, orderDeliveryRecipent, orderDeliveryContact, orderDeliveryAddress, orderCustomerUserName, orderCustomerUserPhone
                    )
                    orderList.add(myOrderInfo)
                    runBlocking {
//                            getMyOrderProduct(orderId, idx, productList)
                        OrderDetailRepository.getOrderedProductByOrderNum(orderId) { product ->
                            if (product.result.exists() == true) {
                                for (c2 in product.result.children) {
                                    val orderId2 = c2.child("orderId").value as String
                                    val orderProductId = c2.child("orderProductId").value as Long
                                    val orderSellerId = c2.child("orderSellerId").value as String
                                    val orderDate = c2.child("orderDate").value as String
                                    val orderUserId = c2.child("orderUserId").value as String
                                    val orderProductName = c2.child("orderProductName").value as String
                                    val orderProductCount = c2.child("orderProductCount").value as String
                                    val orderProductPrice = c2.child("orderProductPrice").value as String
                                    val orderProductImage = c2.child("orderProductImage").value as String
                                    val orderProductState = c2.child("orderProductState").value as String
                                    val productObj = OrderProductModel(orderId2, orderProductId, orderSellerId, orderDate, orderUserId,
                                        orderProductName, orderProductCount, orderProductPrice, orderProductImage, orderProductState)
                                    tempList.add(productObj)
                                }
                                productList.add(tempList)
                                //이건 무조건 여기 있어야함
                                myOrderProductDoubleList.value = productList
                                Log.d("testt","${myOrderProductDoubleList.value}")
                            }
                        }
                    }
                }
                myOrderList.value = orderList
                //여기서도 null이면 ...?
                Log.d("testt","for문 내부 최종 결과${this.myOrderProductDoubleList.value}")
                load.value=true

            }

        }
//        Log.d("testt","찐찐 최종 결과${productList}")

    }
}