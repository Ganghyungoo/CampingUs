package com.test.campingusproject_customer.ui.campsite

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.FragmentCampsiteBinding
import com.test.campingusproject_customer.databinding.RowPostReadBinding
import com.test.campingusproject_customer.databinding.RowSearchedCampsiteBinding
import com.test.campingusproject_customer.dataclassmodel.CampsiteInfo
import com.test.campingusproject_customer.dataclassmodel.Response
import com.test.campingusproject_customer.ui.comunity.PostReadFragment
import com.test.campingusproject_customer.ui.main.MainActivity
import com.test.campingusproject_customer.viewmodel.CampsiteViewModel
import org.w3c.dom.Text
import java.io.Closeable

class CampsiteFragment : Fragment(), OnMapReadyCallback, Overlay.OnClickListener {
    lateinit var fragmentCampsiteBinding: FragmentCampsiteBinding
    lateinit var mainActivity: MainActivity
    lateinit var callback: OnBackPressedCallback
    lateinit var naverMap: NaverMap
    lateinit var locationSource: FusedLocationSource
    lateinit var campsiteViewModel:CampsiteViewModel
    lateinit var myLatitude:String
    lateinit var myLongitude:String
    //캠핑장 데이터 리스트
     var campList= mutableListOf<CampsiteInfo>()
    //불러온 데이터의 특징
    lateinit var dataState: Response
    //현재 내가 갖고있는 데이터 갯수
    var mydataNum=0
    //서버에 있는 데이터 총 갯수
    var cloudDataNum:Int=-1
    //서버에 명령할 데이터 페이지번호
    var page:Int=1
    //데이터를 다 가져왔는지 알고있는 Boolean값
    var isAlldata=false


    //권한 코드
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용되었으면
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            } else {
                // 권한이 거부 되었으면
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentCampsiteBinding = FragmentCampsiteBinding.inflate(layoutInflater)

        mainActivity = activity as MainActivity

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        campsiteViewModel= ViewModelProvider(mainActivity)[CampsiteViewModel::class.java]
        campsiteViewModel.run {
            campSites.observe(mainActivity){
                campList.addAll(it.item)
                Log.d("testt",campList.toString())
                if(campList.size==cloudDataNum){
                    addMaker()
                }
            }
            dataInfo.observe(mainActivity){
                dataState = it.response

                //페이지를 넘길지 말지 구분하기 위해 구하는 검색된 데이터의 총 갯수
                cloudDataNum = dataState.body.totalCount

                //검색된 데이터들을 모두 가져오기 위해서 통신 할때마다 가져온 데이터의 갯수를 누적해서 더한다
                mydataNum+=dataState.body.numOfRows

                //만약 서버데이터가 내가 갖고있는 데이터보다 크면 반복
                Log.d("testt","검색된 데이터 총 갯수:${cloudDataNum}")
                Log.d("testt","지금 내가 갖고있는 데이터 수:${mydataNum}")

                page += 1
                if (cloudDataNum != mydataNum) {
                    //서버에서 데이터 불러오기
                    campsiteViewModel.fetchCampsites(page,myLatitude,myLongitude)
                    Log.d("testt", "while문 동작 page:${page}")
                    Thread.sleep(3000) // 5초 딜레이
                }else{
//                    Log.d("testt","여기서 마크 추가할건데..")
//                    addMaker()
//                    isAlldata=true
                }

            }

        }









        //비동기로 네이버 객체를 얻어온다
        val fm = mainActivity.supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        //OnMapReadyCallback등록
        mapFragment.getMapAsync(this)

        fragmentCampsiteBinding.run {
            //제휴 캠핑장 플로팅 바 클릭시
            buttonContractCampsite.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.CONTRACT_CAMPSITE_FRAGMENT, false, false, null)
            }
            //서치바 리사이클러뷰 설정
            recyclerViewCampListResult.run {
                adapter=SearchedCampsiteAdapter()
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

        }

        return fragmentCampsiteBinding.root
    }

    //지도객체가 callback되면 호출되는 메서드
    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        //내 위치 맵에 설정
        naverMap.locationSource = locationSource
        //확대 축소 버튼 안보이게
        naverMap.uiSettings.isZoomControlEnabled = false
        //나침반 안보이게 삭제
        naverMap.uiSettings.isCompassEnabled = false
        //내 위치 버튼 위치 커스텀
        val locationButton = fragmentCampsiteBinding.buttonMyLocation
        locationButton.map = naverMap

        //권한 확인 및 승인되지 않은 경우 요청
        if (ContextCompat.checkSelfPermission(
                mainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow

            //네이버 맵에 위치가 찍히면 최초로 나의 위도 경도를 얻어온다.
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                //location nullable 대응
                if (location != null) {
                    myLatitude = location.latitude.toString()
                    myLongitude = location.longitude.toString()
                    //데이터 1차 호출
                    campsiteViewModel.fetchCampsites(page,myLatitude,myLongitude)
                } else {
                    //위도 경도 정보를 불러오지 못했을 때
                }
            }

        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }


        naverMap.addOnLocationChangeListener { location ->
            naverMap.locationTrackingMode
        }
    }

    fun addMaker(){
        for (info in campList){
            val marker= Marker()
            marker.position= LatLng(info.위도.toDouble(),info.경도.toDouble())
            marker.tag=info
            marker.map=naverMap
            Log.d("testt","마커 추가 중")
        }
    }

    //뒤로가기 버튼 눌렀을 때 동작할 코드 onDetech까지
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.removeFragment(MainActivity.CAMPSITE_FRAGMENT)
                mainActivity.activityMainBinding.bottomNavigationViewMain.selectedItemId = R.id.menuItemHome

                campsiteViewModel.resetData()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
        //프래그 먼트 종료 시 위치 업데이트 중단
        locationSource.deactivate()
        //프래그먼트 종료 시 뷰모델 데이터 초기화
        campsiteViewModel.resetData()

        //프래그먼트 종료시 내부 맵 프래그먼트의 getMapAsync도 종료시키기 위해 맵 프래그먼트 수동 종료
        val mapFragment =
            mainActivity.supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
        mapFragment?.let {
            mainActivity.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    inner class SearchedCampsiteAdapter : RecyclerView.Adapter<SearchedCampsiteAdapter.SearchedCampsiteViewHolder>(){
        inner class SearchedCampsiteViewHolder(rowSearchedCampsiteBinding: RowSearchedCampsiteBinding) : RecyclerView.ViewHolder(rowSearchedCampsiteBinding.root) {
            val imageViewCampsite:ImageView
            val textViewCampsiteName:TextView
            val textViewCampsiteAdress:TextView
            init {
                imageViewCampsite=rowSearchedCampsiteBinding.imageViewSearchedCampsite
                textViewCampsiteName=rowSearchedCampsiteBinding.textViewSearchedCampsiteName
                textViewCampsiteAdress=rowSearchedCampsiteBinding.textViewSearchedCampsiteAdress

            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): SearchedCampsiteViewHolder {
            val rowSearchedCampsiteBinding=RowSearchedCampsiteBinding.inflate(layoutInflater)
            val searchedCampsiteViewHolder=SearchedCampsiteViewHolder(rowSearchedCampsiteBinding)

            rowSearchedCampsiteBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return searchedCampsiteViewHolder
        }

        override fun getItemCount(): Int {
            return 33
        }

        override fun onBindViewHolder(holder: SearchedCampsiteViewHolder, position: Int) {
            holder.imageViewCampsite.setImageResource(R.drawable.camping_24px)
            holder.textViewCampsiteName.text="강현구의 캠핑장"
            holder.textViewCampsiteAdress.text="인천 서구 명가골로 34번길 7-2 1층"
        }


    }

    override fun onClick(p0: Overlay): Boolean {
        TODO("Not yet implemented")
    }
}