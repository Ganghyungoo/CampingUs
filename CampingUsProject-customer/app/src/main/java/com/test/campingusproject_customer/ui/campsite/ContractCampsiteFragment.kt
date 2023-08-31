package com.test.campingusproject_customer.ui.campsite

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.DialogContractMarkerBinding
import com.test.campingusproject_customer.databinding.DialogMarkerBinding
import com.test.campingusproject_customer.databinding.FragmentContractCampsiteBinding
import com.test.campingusproject_customer.dataclassmodel.CampsiteInfo
import com.test.campingusproject_customer.dataclassmodel.ContractCampsite
import com.test.campingusproject_customer.ui.main.MainActivity

class ContractCampsiteFragment : Fragment(), OnMapReadyCallback, Overlay.OnClickListener {
    lateinit var fragmentContractCampsiteBinding: FragmentContractCampsiteBinding
    lateinit var mainActivity: MainActivity
    lateinit var contractNaverMap: NaverMap
    lateinit var callback: OnBackPressedCallback
    lateinit var contractCampsiteList: MutableList<ContractCampsite>
    var markerList = mutableListOf<Marker>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentContractCampsiteBinding = FragmentContractCampsiteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentContractCampsiteBinding.toolbarContractCampsite.run {
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {

                //프래그먼트 종료시 내부 맵 프래그먼트의 getMapAsync도 종료시키기 위해 제휴 맵 프래그먼트 수동 종료
                val mapFragment =
                    mainActivity.supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
                mapFragment?.let {
                    mainActivity.supportFragmentManager.beginTransaction().remove(it).commit()
                }
                mainActivity.replaceFragment(MainActivity.CAMPSITE_FRAGMENT, false, false, null)
            }
        }
        contractCampsiteList = mainActivity.fetchContractCampsite()

        //비동기로 네이버 객체를 얻어온다
        val fm = mainActivity.supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        //OnMapReadyCallback등록
        mapFragment.getMapAsync(this)

        return fragmentContractCampsiteBinding.root
    }

    override fun onMapReady(p0: NaverMap) {
        contractNaverMap = p0

        try{
            val latitude = arguments?.getString("latitude")
            val longitude = arguments?.getString("longitude")
            contractNaverMap.run {
                val cameraPosition = CameraPosition(
                    LatLng(latitude?.toDouble()!!, longitude?.toDouble()!!), // 대상 지점
                    20.0, // 줌 레벨
                )
                this.cameraPosition = cameraPosition
            }
        }catch (e:NullPointerException){
            contractNaverMap.run {
                val cameraPosition = CameraPosition(
                    LatLng(37.863271880138626, 127.55924650457162), // 대상 지점
                    5.0, // 줌 레벨
                )
                this.cameraPosition = cameraPosition
            }
        }
        addMaker()
        Log.d("testt", "제휴캠핑장")
    }

    //해당 프래그먼트가 액티비티에 붙을 때 바텀 네비 삭제
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val main = activity as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //프래그먼트 종료시 내부 맵 프래그먼트의 getMapAsync도 종료시키기 위해 제휴 맵 프래그먼트 수동 종료
                val mapFragment =
                    main.supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
                mapFragment?.let {
                    main.supportFragmentManager.beginTransaction().remove(it).commit()
                }
                main.replaceFragment(MainActivity.CAMPSITE_FRAGMENT, false, false, null)

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onStop() {
        super.onStop()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
        //해당 프래그먼트가 액티비티에서 떨어져 나갈 때 바텀 네비 다시 생성
        //mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE

//        //프래그먼트 종료시 내부 맵 프래그먼트의 getMapAsync도 종료시키기 위해 제휴 맵 프래그먼트 수동 종료
//        val mapFragment =
//            mainActivity.supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
//        mapFragment?.let {
//            mainActivity.supportFragmentManager.beginTransaction().remove(it).commit()
//        }

    }

    fun addMaker() {
        for (info in contractCampsiteList) {
            val marker = Marker()
            marker.position = LatLng(info.위도.toDouble(), info.경도.toDouble())
            marker.tag = info
            marker.map = contractNaverMap
            marker.zIndex = 1
            marker.icon = OverlayImage.fromResource(R.drawable.icons8_camping_48)
            markerList.add(marker)
            marker.setOnClickListener(this)
            Log.d("testt", "마커 추가 중")
        }
    }

    override fun onClick(p0: Overlay): Boolean {
        val dialogBinding = DialogContractMarkerBinding.inflate(layoutInflater)
        if (p0 is Marker) {
            val marker = p0.tag as ContractCampsite
            val builder = MaterialAlertDialogBuilder(
                mainActivity,
                R.style.ThemeOverlay_App_MaterialAlertDialog
            ).apply {
                setView(dialogBinding.root)
                setTitle("제휴 캠핑장 정보")
                Glide.with(mainActivity).load(marker.사진).error(R.drawable.error_24px)
                    .into(dialogBinding.imageViewCampsite)
                dialogBinding.textViewCampsiteName.text = marker.이름

                if (marker.주소.isEmpty()) {
                    dialogBinding.textViewCampsiteAddress.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteAddress.text = marker.주소
                }

                if (marker.연락처.isEmpty()) {
                    dialogBinding.textViewCampsiteNumber.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteNumber.text = marker.연락처
                }

                if (marker.설명.isEmpty()) {
                    dialogBinding.textViewCampsiteDetail.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteDetail.text = marker.설명
                }

                if (marker.환경.isEmpty()) {
                    dialogBinding.textViewCampsiteEnv.visibility = View.GONE
                    dialogBinding.tv1.visibility = View.GONE
                    dialogBinding.tv2.visibility = View.GONE
                    dialogBinding.l1.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteEnv.text = marker.환경
                }

                if (marker.형태.isEmpty()) {
                    dialogBinding.textViewCampsiteForm.visibility = View.GONE
                    dialogBinding.tv3.visibility = View.GONE
                    dialogBinding.tv4.visibility = View.GONE
                    dialogBinding.l2.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteForm.text = marker.형태
                }

                if (marker.편의시설.isEmpty()) {
                    dialogBinding.textViewCampsiteFacilities.visibility = View.GONE
                    dialogBinding.tv5.visibility = View.GONE
                    dialogBinding.tv6.visibility = View.GONE
                    dialogBinding.l3.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteFacilities.text = marker.편의시설
                }

                if (marker.놀거리.isEmpty()) {
                    dialogBinding.textViewCampsiteFun.visibility = View.GONE
                    dialogBinding.tv7.visibility = View.GONE
                    dialogBinding.tv8.visibility = View.GONE
                    dialogBinding.l4.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteFun.text = marker.놀거리
                }

                if (marker.애완동물.isEmpty()) {
                    dialogBinding.textViewCampsiteAnimal.visibility = View.GONE
                    dialogBinding.tv9.visibility = View.GONE
                    dialogBinding.tv10.visibility = View.GONE
                    dialogBinding.l5.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteAnimal.text = marker.애완동물
                }

                if (marker.홈페이지.isEmpty()) {
                    dialogBinding.textViewCampsiteUrl.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteUrl.run {
                        text = marker.홈페이지
                        setTextColor(Color.BLUE)
                        setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(marker.홈페이지))
                            startActivity(intent)
                        }
                    }
                }
                if (marker.제휴코드.isEmpty()) {
                    dialogBinding.textViewCampsiteCode.visibility = View.GONE
                } else {
                    dialogBinding.textViewCampsiteCode.run {
                        text = marker.제휴코드
                        setTextColor(Color.RED)
                    }
                }
                setPositiveButton("닫기", null)
                setNegativeButton("예약하러 가기",null)
            }
            builder.show()
        }
        return false
    }
}