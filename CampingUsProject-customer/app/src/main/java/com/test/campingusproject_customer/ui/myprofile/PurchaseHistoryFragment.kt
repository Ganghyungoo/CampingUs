package com.test.campingusproject_customer.ui.myprofile

import android.content.Context
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
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.CampsiteViewModel
import com.test.campingusproject_customer.viewmodel.MyOrderListViewModel
import kotlinx.coroutines.runBlocking

class PurchaseHistoryFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentPurchaseHistoryBinding: FragmentPurchaseHistoryBinding
    lateinit var myOrderListViewModel: MyOrderListViewModel
    lateinit var productList:MutableList<MutableList<OrderProductModel>>


    val maxIdx:Int by lazy{
        productList.size
    }
    companion object{
        var nowIdx=0
    }


    var state = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        mainActivity = activity as MainActivity
        fragmentPurchaseHistoryBinding = FragmentPurchaseHistoryBinding.inflate(layoutInflater)
        myOrderListViewModel = ViewModelProvider(mainActivity)[MyOrderListViewModel::class.java]
        myOrderListViewModel.run {
            myOrderList.observe(mainActivity) {
                Log.d("testt", "오더ㅋㅋㅋㅋ${it.size}")
//                val adapter=fragmentPurchaseHistoryBinding.recyclerViewPurchaseHistory.adapter as PurchaseHistoryAdapter
//                adapter.notifyDataSetChanged()

                // 리사이클러 뷰

            }
            myOrderProductDoubleList.observe(mainActivity) {
                Log.d("testt", "제품ㅋㅋㅋㅋ${it.size}")
                productList=it

                Log.d("testt", "아이디엑스${myOrderList.value?.size!!}")
            }
//            nowIdx=0
            load.observe(mainActivity){

            }
        }
        val sharedPreferences =
            mainActivity.getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)

        myOrderListViewModel.fetchMyOrder(sharedPreferences.getString("customerUserId", null)!!)


        fragmentPurchaseHistoryBinding.run {

            fragmentPurchaseHistoryBinding.recyclerViewPurchaseHistory.run {
                adapter = PurchaseHistoryAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }

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

    // 구매내역 어댑터
    inner class PurchaseHistoryAdapter() :
        RecyclerView.Adapter<PurchaseHistoryFragment.PurchaseHistoryAdapter.PurchaseHistoryViewHolder>() {

        inner class PurchaseHistoryViewHolder(rowPurchaseHistoryBinding: RowPurchaseHistoryBinding) :
            RecyclerView.ViewHolder(rowPurchaseHistoryBinding.root) {
            val textViewRowPurchaseHistoryDate: TextView
            val buttonRowPurchaseHistory: Button
            val layoutInner:LinearLayout
            var nowIdx2=0

            init {

                textViewRowPurchaseHistoryDate =
                    rowPurchaseHistoryBinding.textViewRowPurchaseHistoryDate
                buttonRowPurchaseHistory = rowPurchaseHistoryBinding.buttonRowPurchaseHistory
                val bindObject=rowPurchaseHistoryBinding.LayoutInner
                rowPurchaseHistoryBinding.root.setOnClickListener{
                    Log.d("testt","가보자${myOrderListViewModel.myOrderProductDoubleList.value?.get(1)}")
                }
                Log.d("testt","가보자${productList}")

                    if(nowIdx2 != maxIdx){
                        for(idx in 0 until productList.get(nowIdx2).size){
                            val innerProduct=RowPurchaseHistoryItemBinding.inflate(layoutInflater)
                            innerProduct.textViewRowPurchaseHistoryItemName.text=productList.get(nowIdx2)?.get(idx)?.orderProductName
                            innerProduct.textViewRowPurchaseHistoryItemNumber.text=productList.get(nowIdx2)?.get(idx)?.orderProductCount
                            innerProduct.textViewRowPurchaseHistoryItemPrice.text=productList.get(nowIdx2)?.get(idx)?.orderProductPrice
                            innerProduct.textViewRowPurchaseHistoryItemStateDone.text=productList.get(nowIdx2)?.get(idx)?.orderProductState
                            Log.d("testt","ㄱㄱㄱㄱ${idx}}")


//                    innerProduct.textViewRowPurchaseHistoryItemName.text=productList[position].get(idx).orderProductName
                            bindObject.addView(innerProduct.root)
                        }
                        nowIdx2=nowIdx2+1
                    }
                layoutInner=bindObject
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
            return myOrderListViewModel.myOrderList.value?.size!!

        }

        override fun onBindViewHolder(holder: PurchaseHistoryViewHolder, position: Int) {
//            holder.textViewRowPurchaseHistoryDate.text = orderList[position].orderDate
            holder.textViewRowPurchaseHistoryDate.text = "으아아아아"
            holder.buttonRowPurchaseHistory.setOnClickListener {
//                mainActivity.replaceFragment(MainActivity.ORDER_DETAIL_FRAGMENT, true, true, null)
                Log.d("testt","${myOrderListViewModel.myOrderProductDoubleList.value}")
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

}
