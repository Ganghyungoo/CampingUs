package com.test.campingusproject_seller.ui.sellstate

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.test.campingusproject_seller.R
import com.test.campingusproject_seller.databinding.FragmentSellStateBinding
import com.test.campingusproject_seller.databinding.RowSellStateOrderHistoryItemBinding
import com.test.campingusproject_seller.dataclassmodel.OrderProductModel
import com.test.campingusproject_seller.repository.ProductRepository
import com.test.campingusproject_seller.ui.main.MainActivity
import com.test.campingusproject_seller.viewmodel.SellerStateViewModel

class SellStateFragment : Fragment() {
    lateinit var fragmentSellStateBinding: FragmentSellStateBinding
    lateinit var mainActivity: MainActivity
    lateinit var sellerStateViewModel: SellerStateViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentSellStateBinding = FragmentSellStateBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        sellerStateViewModel = ViewModelProvider(mainActivity)[SellerStateViewModel::class.java]
        sellerStateViewModel.run {
            orderProductList.observe(mainActivity) {
                if (it != null) {
                    (fragmentSellStateBinding.recyclerViewSellState.adapter as? OrderHistoryAdapter)?.updateItemList(
                        it
                    )
                }
            }
        }
        val pref = mainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        sellerStateViewModel.fetchOrderProduct(pref.getString("userId",null).toString())

        fragmentSellStateBinding.run {
            toolbarSellState.run {
                title = "판매현황"

                inflateMenu(R.menu.menu_sellstate)

                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.itemNotification -> { // 알림화면으로 넘어가기
                            mainActivity.replaceFragment(
                                MainActivity.NOTIFICATION_MAIN_FRAGMENT,
                                true,
                                true,
                                null
                            )
                        }
                    }
                    false
                }
            }

            recyclerViewSellState.run {
                val itemDecoration=ItemDecoration()
                adapter = OrderHistoryAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
                addItemDecoration(itemDecoration)
            }
        }
        return fragmentSellStateBinding.root
    }


    // 주문내역 아이템
    inner class OrderHistoryAdapter :
        RecyclerView.Adapter<SellStateFragment.OrderHistoryAdapter.OrderHistoryViewHolder>() {
        private var itemList: List<OrderProductModel> = emptyList()
        fun updateItemList(newList: List<OrderProductModel>) {
            this.itemList = newList
            notifyDataSetChanged() // 갱신
        }

        inner class OrderHistoryViewHolder(rowSellStateOrderHistoryItemBinding: RowSellStateOrderHistoryItemBinding) :
            RecyclerView.ViewHolder(rowSellStateOrderHistoryItemBinding.root) {
            val progressBarRow:ProgressBar
            val textViewOrderId: TextView
            val textViewOrderDate: TextView
            val textViewSellStateProductName: TextView
            val imageViewProductImage: ImageView
            val textViewSellStateProductPrice: TextView
            val textViewSellStateProductNumber: TextView
            val buttonSellStateSend: Button

            init {
                progressBarRow = rowSellStateOrderHistoryItemBinding.progressBarRow
                textViewOrderId = rowSellStateOrderHistoryItemBinding.textViewOrderId
                textViewOrderDate = rowSellStateOrderHistoryItemBinding.textViewOrderDate
                textViewSellStateProductName =
                    rowSellStateOrderHistoryItemBinding.textViewSellStateProductName
                imageViewProductImage = rowSellStateOrderHistoryItemBinding.imageViewProductImage
                textViewSellStateProductPrice =
                    rowSellStateOrderHistoryItemBinding.textViewSellStateProductPrice
                textViewSellStateProductNumber =
                    rowSellStateOrderHistoryItemBinding.textViewSellStateProductNumber
                buttonSellStateSend = rowSellStateOrderHistoryItemBinding.buttonSellStateSend
                rowSellStateOrderHistoryItemBinding.buttonSellStateSend.setOnClickListener {
                    //발송하기 클릭 시
                    //상태 변경한다음 가존 함수 호출해야함
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
            val rowSellStateOrderHistoryItemBinding =
                RowSellStateOrderHistoryItemBinding.inflate(layoutInflater)
            val orderHistoryViewHolder = OrderHistoryViewHolder(rowSellStateOrderHistoryItemBinding)

            rowSellStateOrderHistoryItemBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return orderHistoryViewHolder
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
            holder.textViewOrderId.text=itemList[position].orderUserId
            holder.textViewOrderDate.text=itemList[position].orderDate
            holder.textViewSellStateProductName.text=itemList[position].orderProductName
            holder.textViewSellStateProductPrice.text=itemList[position].orderProductPrice
            holder.textViewSellStateProductNumber.text=itemList[position].orderProductCount
            ProductRepository.getProductFirstImage(itemList[position].orderProductImage) {uri->
                //글라이드 라이브러리로 이미지 표시
                //이미지 로딩 완료되거나 실패하기 전까지 프로그래스바 활성화
                Glide.with(mainActivity).load(uri.result)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            holder.progressBarRow.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            holder.progressBarRow.visibility = View.GONE
                            return false
                        }

                    })
                    .override(200, 200)
                    .into(holder.imageViewProductImage)
            }
        }
    }


    inner class ItemDecoration: RecyclerView.ItemDecoration(){
        val padding = 10 // 여백을 5로 설정

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = padding
        }
    }
}