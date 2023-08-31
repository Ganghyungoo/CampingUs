package com.test.campingusproject_customer.viewmodel

import android.support.annotation.MainThread
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.campingusproject_customer.dataclassmodel.OrderModel
import com.test.campingusproject_customer.dataclassmodel.OrderProductModel
import com.test.campingusproject_customer.repository.OrderDetailRepository
import kotlinx.coroutines.runBlocking

class MyOrderListViewModel : ViewModel() {

    val myOrderList = MutableLiveData<MutableList<OrderModel>>()
    val myOrderProductDoubleList = MutableLiveData<MutableList<MutableList<OrderProductModel>>>()
    val load = MutableLiveData<Boolean>(false)


    //프리퍼런스에 내 아이디를 갖고 해당하는 주문목록을 가져오는 메서드


}