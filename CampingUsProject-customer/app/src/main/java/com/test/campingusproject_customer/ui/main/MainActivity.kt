package com.test.campingusproject_customer.ui.main

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.campRingusproject_customer.ui.review.ReviewDetailFragment
import com.test.campingusproject_customer.R
import com.test.campingusproject_customer.databinding.ActivityMainBinding
import com.test.campingusproject_customer.dataclassmodel.ContractCampsite
import com.test.campingusproject_customer.repository.CustomerUserRepository
import com.test.campingusproject_customer.ui.campsite.CampsiteFragment
import com.test.campingusproject_customer.ui.campsite.ContractCampsiteFragment
import com.test.campingusproject_customer.ui.shopping.ShoppingProductFragment
import com.test.campingusproject_customer.ui.comunity.PostWriteFragment
import com.test.campingusproject_customer.ui.comunity.ComunityFragment
import com.test.campingusproject_customer.ui.comunity.PostReadFragment
import com.test.campingusproject_customer.ui.inquiry.InquiryFragment
import com.test.campingusproject_customer.ui.myprofile.ModifyMyPostFragment
import com.test.campingusproject_customer.ui.myprofile.ModifyMyprofileFragment
import com.test.campingusproject_customer.ui.myprofile.MyQuestionDetailFragment
import com.test.campingusproject_customer.ui.myprofile.MyQuestionListFragment
import com.test.campingusproject_customer.ui.myprofile.MyPostListFragment
import com.test.campingusproject_customer.ui.myprofile.MyprofileFragment
import com.test.campingusproject_customer.ui.myprofile.PurchaseHistoryFragment
import com.test.campingusproject_customer.ui.myprofile.ReviewWriteFragment
import com.test.campingusproject_customer.ui.shopping.ShoppingFragment
import com.test.campingusproject_customer.ui.user.AuthFragment
import com.test.campingusproject_customer.ui.user.JoinFragment
import com.test.campingusproject_customer.ui.user.LoginFragment
import com.test.campingusproject_customer.ui.payment.CartFragment
import com.test.campingusproject_customer.ui.payment.OrderDetailFragment
import com.test.campingusproject_customer.ui.payment.PaymentFragment
import com.test.campingusproject_customer.ui.review.ReviewFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    var currentFragment = 0
    var selectMenu = 0


    companion object {
        val HOME_FRAGMENT = "HomeFragment"
        val SHOPPING_FRAGMENT = "ShoppingFragment"
        val COMUNITY_FRAGMENT = "ComunityFragment"
        val MYPROFILE_FRAGMENT = "MyProfileFragment"
        val CAMPSITE_FRAGMENT = "CampsiteFragment"
        val SHOPPING_PRODUCT_FRAGMENT = "ShoppingProductFragment"
        val POST_WRITE_FRAGMENT = "PostWriteFragment"
        val POST_READ_FRAGMENT = "PostReadFragment"
        val LOGIN_FRAGMENT = "LoginFragment"
        val JOIN_FRAGMENT = "JoinFragment"
        val AUTH_FRAGMENT = "AuthFragment"
        val CART_FRAGMENT = "CartFragment"
        val PAYMENT_FRAGMENT = "PaymentFragment"
        val ORDER_DETAIL_FRAGMENT = "OrderDetailFragment"
        val MODIFY_MYPROFILE_FRAGMENT = "ModifyMyprofileFragment"
        val CONTRACT_CAMPSITE_FRAGMENT = "ContractCampsiteFragment"
        val INQUIRY_FRAGMENT = "InquiryFragment"
        val MY_QUESTION_LIST_FRAGMENT = "MyQuestionListFragment"
        val MY_QUESTION_DETAIL_FRAGMENT = "MyQuestionDetailFragment"
        val MY_POST_LIST_FRAGMENT = "MyPostListFragment"
        val MODIFY_MY_POST_FRAGMENT = "ModifyMyPostFragment"
        val PURCHASE_HISTORY_FRAGMENT = "PurchaseHistoryFragment"
        val REVIEW_FRAGMENT = "ReviewFragment"
        val REVIEW_DETAIL_FRAGMENT = "ReviewDetailFragment"
        val REVIEW_WRITE_FRAGMENT = "ReviewWriteFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        //시작하면 홈으로 가기
        replaceFragment(HOME_FRAGMENT, false, true, null)

        activityMainBinding.run {
            bottomNavigationViewMain.run {
                this.selectedItemId = R.id.menuItemHome
                selectMenu = R.id.menuItemHome
                Log.d("aaaa","$selectMenu")
                setOnItemSelectedListener {
                    //선택된 메뉴를 다시 클릭할 때 선택을 넘기는 조건문
                    if (it.itemId == selectedItemId){
                        return@setOnItemSelectedListener false
                    }
                    when (it.itemId) {
                        //홈 클릭
                        R.id.menuItemHome -> {
                            selectMenu = it.itemId
                            Log.d("aaaa","$selectMenu")
                            replaceFragment(HOME_FRAGMENT, false, true, null)
                        }
                        //캠핑장 클릭
                        R.id.menuItemCamping -> {
                            selectMenu = it.itemId
                            Log.d("aaaa","$selectMenu")
                            replaceFragment(CAMPSITE_FRAGMENT,false,true,null)
                        }
                        //쇼핑 클릭
                        R.id.menuItemShopping -> {
                            selectMenu = it.itemId
                            Log.d("aaaa","$selectMenu")
                            replaceFragment(SHOPPING_FRAGMENT, false, true, null)
                        }
                        //커뮤니티 클릭
                        R.id.menuItemComunity -> {
                            val sharedPreferences = getSharedPreferences("customer_user_info", Context.MODE_PRIVATE)
                            if(CustomerUserRepository.checkLoginStatus(sharedPreferences) == false) {
                                replaceFragment(COMUNITY_FRAGMENT, false, true, null)
                                val builder = MaterialAlertDialogBuilder(this@MainActivity, R.style.ThemeOverlay_App_MaterialAlertDialog)
                                builder.run {
                                    setTitle("로그인 필요")
                                    setMessage("로그인이 필요합니다.")
                                    setPositiveButton("닫기") { dialogInterface: DialogInterface, i: Int ->
                                        removeFragment(COMUNITY_FRAGMENT)
                                        selectedItemId = selectMenu
                                    }
                                    show()
                                }
                                Log.d("aaaa","$selectMenu")
                            }
                            else{
                                selectMenu = it.itemId
                                Log.d("aaaa","$selectMenu")
                                val boardType: Long = 1L
                                val newBundle = Bundle()
                                newBundle.putLong("boardType", boardType)
                                replaceFragment(COMUNITY_FRAGMENT, false, true, newBundle)
                            }

                        }
                        //내정보 클릭
                        R.id.menuItemMyProfile -> {
                            selectMenu = it.itemId
                            replaceFragment(MYPROFILE_FRAGMENT, false, true, null)
                        }

                        else -> {
                            replaceFragment(HOME_FRAGMENT, false, true, null)
                        }
                    }
                    true
                }
            }
        }
    }

    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name: String, addToBackStack: Boolean, animate: Boolean, bundle: Bundle?) {
        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // 새로운 Fragment를 담을 변수
        var newFragment = when (name) {
            HOME_FRAGMENT -> HomeFragment()
            SHOPPING_FRAGMENT -> ShoppingFragment()
            SHOPPING_PRODUCT_FRAGMENT -> ShoppingProductFragment()
            COMUNITY_FRAGMENT -> ComunityFragment()
            MYPROFILE_FRAGMENT -> MyprofileFragment()
            CAMPSITE_FRAGMENT-> CampsiteFragment()
            CONTRACT_CAMPSITE_FRAGMENT-> ContractCampsiteFragment()
            POST_WRITE_FRAGMENT -> PostWriteFragment()
            POST_READ_FRAGMENT -> PostReadFragment()
            LOGIN_FRAGMENT -> LoginFragment()
            JOIN_FRAGMENT -> JoinFragment()
            AUTH_FRAGMENT -> AuthFragment()
            CART_FRAGMENT -> CartFragment()
            PAYMENT_FRAGMENT -> PaymentFragment()
            ORDER_DETAIL_FRAGMENT -> OrderDetailFragment()
            INQUIRY_FRAGMENT -> InquiryFragment()
            MODIFY_MYPROFILE_FRAGMENT -> ModifyMyprofileFragment()
            MY_QUESTION_LIST_FRAGMENT -> MyQuestionListFragment()
            MY_QUESTION_DETAIL_FRAGMENT -> MyQuestionDetailFragment()
            MY_POST_LIST_FRAGMENT ->MyPostListFragment()
            MODIFY_MY_POST_FRAGMENT ->ModifyMyPostFragment()
            PURCHASE_HISTORY_FRAGMENT -> PurchaseHistoryFragment()
            REVIEW_FRAGMENT -> ReviewFragment()
            REVIEW_DETAIL_FRAGMENT -> ReviewDetailFragment()
            REVIEW_WRITE_FRAGMENT -> ReviewWriteFragment()

            else -> Fragment()
        }

        newFragment.arguments = bundle

        if (newFragment != null) {

            // Fragment를 교체한다.
            fragmentTransaction.replace(R.id.fragmentContainerMain, newFragment)

            if (animate == true) {
                // 애니메이션을 설정한다.
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name: String) {
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    //인자로 전달된 뷰에 포커스주고 키보드 생성
    fun focusOnView(view:View){
        view.requestFocus()
        thread {
            SystemClock.sleep(500)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        }
    }

    fun fetchContractCampsite(): MutableList<ContractCampsite> {
        val contractList = mutableListOf<ContractCampsite>()
        val contractCampsite1 = ContractCampsite(
            "림스 글램핑", // 이름
            "126.73671595119986 ", // 경도
            "34.60301644590259", // 위도
            "http://limsglamping.modoo.at/", //홈페이지
            "예쁜 정원과 함께하는 글램핑 정원과 함께 힐링할 수 있는 림스 글램핑은 전라남도에서 주최한 예쁜 정원 콘테스트에서 최우수상을 수상한 림스가든 안에 자리하고 있어 동화 속 같은 아름다운 정원에서 감성 글램핑을 즐길 수 있다. 아늑한 글램핑 내부 인테리어와 아이들이 신나게 물놀이할 수 있는 수영장과 트램펄린, 산책 코스까지 완벽한 편의 시설을 자랑한다. 또한 모든 객실은 세스코 관리를 받고 있어 더욱 믿을만하다. 반려견, 숯, 장작 등 개인 화기 및 반려견 동반은 불가하며 근처에 월출산과 가우도는 함께 둘러보기 좋은 관광 명소이다.", // 설명
            "숲", // 주변환경
            "일반야영장,카라반,글램핑",
            "샤워실, 휴게실, 화장실",
            "트렘폴린,물놀이장,놀이터",
            "불가능",
            "https://gocamping.or.kr/upload/camp/100139/thumb/thumb_720_9142QBZ77gMJkUVceP6sChUZ.jpg", // 전화번호
            "전남 강진군 강진읍 해강로 1038-30",
            "342-2234-2322",
            "asdlfkja24dfds"
        )

        val contractCampsite2 = ContractCampsite(
            "호미곶유니의바다 카라반캠핑장", // 이름
            "126.73671595119986 ", // 경도
            "34.60301644590259", // 위도
            "http://www.xn--2-9r8eycu16b70nwja.com/main.php",
            "호미곶 앞바다가 눈앞에 펼쳐지는 카라반  호미곶유니의바다 카라반캠핑장(유니의바다 카라반 호미곶점)은 경북 포항시 남구 호미곶면 강사리에 자리 잡았다. 포항시청을 기점으로 30km가량 떨어졌다. 자동차를 타고 동해안로와 호미로를 번갈아 달리면 닿는다. 도착까지 걸리는 시간은 30분 안팎이다. 호미곶 앞바다가 눈앞에 펼쳐지는 캠핑장에는 카라반 15대가 마련돼 있다. 내부에는 침대, TV, 테이블, 개수대, 취사도구, 조리도구, 화장실, 샤워실 등 일상생활이 가능할 정도의 시설이 완비돼 있다. 주변에는 호미곶해맞이광장이 있어 연계 관광에 나서기 좋다.", // 설명
            "해변", // 주변환경
            "카라반", // 형태
            "매점,샤워실,싱크대",
            "수상레저,낚시,해수욕",
            "불가능",
            "https://gocamping.or.kr/upload/camp/100140/thumb/thumb_720_8297QNOce95heDJ3zC0ckN3i.jpg",
            "경북 포항시 남구 호미곶면 해맞이로46번길 134", // 전화번호
            "324-2434-2342",
            "sdf35232ff"
        )
        val contractCampsite3 = ContractCampsite(
            "(주)웨이브파크", // 이름
            "127.69703122658797", // 경도
            "37.6699140228201", // 위도
            "https://wavepark.co.kr/facilityCaravan",
            "인공 서핑장 웨이브 파크에서 운영하는 카라반 숙소  시흥에 위치한 웨이브파크는 대형 인공 서핑장으로 다양한 물놀이를 즐길 수 있는 레져 시설인데, 카라반 25개 사이트를 함께 운영하고 있다. 카라반은 럭셔리 뷰와 레이크 뷰, VIP로 나눠지는데, 시설 및 뷰의 차이가 있다. 레이크 뷰 쪽이 서해 일몰을 보기더 편하다. 바로 옆 서핑장에서 서핑, 스킨스쿠버, 등의 물놀이를 즐기고, 편리한 카라반에서 서해안의 낙조와 함께 비비큐를 맛보며, 숙박을 할 수 있어 인기다.",
            "해변", // 주변환경
            "카라반", // 형태
            "침대,TV,에어컨,냉장고,유무선인터넷,난방기구,취사도구,내부화장실",
            "수상레저,액티비티", // 놀거리
            "가능", // 애완동물
            "https://gocamping.or.kr/upload/camp/100140/thumb/thumb_720_8297QNOce95heDJ3zC0ckN3i.jpg",
            "경기 시흥시 거북섬둘레길 42 (정왕동)", // 주소
            "031-431-9600", // 사진
            "sFscr456g"
        )

        val contractCampsite4 = ContractCampsite(
            "약수동산", // 이름
            "127.45483463625968", // 경도
            "37.50618034924661", // 위도
            "https://www.instagram.com/yaksudongsan", //홈페이지
            "남한강을 따라 양평시내까지 내려다 보이는 전망좋은  캠핑장  약수동산은 남한강을 따라 양평시내까지 내려다 보이는 전망좋은  캠핑장이다. 남한강이 바로 옆에 있고 봄이면 벚꽃이 만개하는 등, 뷰가 좋은 캠핑장으로 유명하다. 경의중앙선 아신역에서 도보로 15분 거리에 있어 접근성이 뛰어나다. 우백호 존 4개과 배산임수 존 4개이 남한강을 가까이에서 볼 수 있어 인기가 많은데, 특히 배산임수 스페셜 사이트는 명당자리로 예약이 어렵다. 좌청룡 존 7개는 지대가 높아 시야가 트여 있으며, 노을 존 4개는 낮은 지대에 있고, 애견 동반이 가능하다.",//시설소개
            "산",
            "일반야영장",
            "전기,무선인터넷,장작판매,운동시설", // 시설
            "봄꽃여행,가을단풍명소", // 놀거리
            "불가능", // 애완동물
            "https://gocamping.or.kr/upload/camp/7820/thumb/thumb_720_7251NMu6XG2K0Vcx0GAJRi82.jpg", // 사진
            "경기도 양평군 옥천면 옥천창말길 18-80", // 주소
            "353-5655-5433",//전화번호
            "sorigfusrn333"
        )
        val contractCampsite5 = ContractCampsite(
            "더플래츠 글래핑",
            "128.34855029465024",
            "37.10959861701503",
            "https://gpartsalon.imweb.me/",
            "",
            "숲, 산",
            "카라반, 야영장, 글램핑",
            "개별바비큐, 침대방, 2인실, 가족실, 독채, 글램핑, 데크, 파쇄석, 와이파이",
            "수영장, 빔프로젝트, 불멍",
            "가능",
            "https://gocamping.or.kr/upload/camp/100157/thumb/thumb_720_4171F1hFJPWjyR98neEXsjS4.jpg",
            "경기 가평군 상면 수목원로 181-28",
            "010-7384-8008",
            "2fdvkd33"
        )

        val contractCampsite6 = ContractCampsite(
            "원주 칠봉 두루뭉캠핑장",
            "128.36648778479372 ",
            "38.09651788337222",
            "https://gpartsalon.imweb.me/",
            "몽산포해수욕장 인근 깔끔한 캠핑장  솔비치캠핑장은 충남 태안군 남면 신장리에 자리 잡았다. 태안군청을 기점으로 10km가량 떨어졌다. 자동차를 타고 안면대로와 몽산포길을 번갈아 달리면 닿는다. 도착까지 걸리는 시간은 15분 안팎이다. 캠핑장에서 몽산포해수욕장까지 도보로 약 5분이 걸린다. 이 때문에 해수욕을 즐기기 좋다. 캠핑장에는 파쇄석으로 이뤄진 오토캠핑 사이트가 마련돼 있다. 카라반과 트레일러 동반 입장이 가능하다. 주변에는 몽산포항과 안면도쥬라기공원이 있어 연계 여행에 나서기 좋다.",
            "숲, 산, 계곡",
            "글램핑",
            "개별바비큐, 오토캠핑, 바비큐장, 와이파이, 장기숙박, 선착순입장",
            "계곡",
            "가능",
            "https://gocamping.or.kr/upload/camp/100231/thumb/thumb_720_5031zWs3pLvMY7zBvq7xHILf.jpg",
            "강원 원주시 호저면 산현리 513-17",
            "050-7384-8308",
            "ldjxcmnwse"
        )
        val contractCampsite7 = ContractCampsite(
            "담터 오지 글램핑",
            "127.51970993516817 ",
            "35.48276356195954",
            "https://naver.com",
            "담터계곡 상류에 위치한 이색 돔 글램핑장 철원의 담터계곡 상류에 위치한 이색 돔 글램핑장이다. 관리동과 수영장은 리조트처럼 잘 꾸며져 있고, 돔 형태의 글램핑 사이트가 총 9개가 수영장 둘레를 따라 둥글게 배치되어 있다. 사이트마다 야외 개별 바비큐 공간과 테이블이 있고, 숯과 석쇠도 1회 무료 제공된다. 수영장은 일반 수영장과 온수 수영장이 있는데, 낮에도 물놀이하기 좋고, 예쁘지만, 특히 밤에 조명이 무척 아름다워 이국적인 기분을 내기 좋다.",
            "계곡, 산",
            "글램핑",
            "침대,에어컨,냉장고,유무선인터넷,난방기구",
            "여름물놀이,가을단풍명소,걷기길",
            "불가능",
            "https://gocamping.or.kr/upload/camp/100626/thumb/thumb_720_1463Us2FzlIPJ6skhPG0WUZR.jpg",
            "강원 철원군 동송읍 담터길 350",
            "503-2234-4432",
            "xkaxjdhwl"
        )

        val contractCampsite8 = ContractCampsite(
            "지은 캠핑장",
            "127.5497913248667",
            "36.57158844181685",
            "https://m.search.naver.com/search.naver?sm=mtp_hty.top&where=m&query=%EC%95%84%EC%9D%B4%EC%9C%A0",
            "포천 명성산 남쪽에 위치한 규모가 큰 글램핑장 포천 명성산 남쪽에 위치한 규모가 큰 민트 글램핑장은 가족단위는 물론 커플들도 만족할 글램핑장이다. 넓은 천연 잔디마당과 대형 실외 수영장과 온수 수영장, 다양한 놀이시설, 라벤더 가든, 야채 체험장, 숲속 산책로를 갖추고 있다. 넓은 잔디 광장을 따라 설치된 민트 글램핑 사이트는 가족단위로 이용하기 좋고, 라벤더 가든을 조망할 수 있는 라벤더 글램핑 사이트는 커플들에게 인기가 많다. 개별 비비큐가 가능하며, 글램핑 내부는 개별 화장실은 물론, 냉난방 설비, 호텔식 침구까지 사계절 쾌적하다.",
            "계곡, 산",
            "글램핑",
            "침대,에어컨,냉장고,유무선인터넷",
            "계곡 물놀이,산책로,운동장,수상레저,낚시",
            "불가능",
            "https://gocamping.or.kr/upload/camp/119/thumb/thumb_720_0396NsoXOTrOqtQoch9B7Ewf.jpg",
            "경기 포천시 이동면 금강로 5846",
            "400-2345-3322",
            "rrkdklscvl"
        )

        val contractCampsite9 = ContractCampsite(
            "민우 글램핑",
            "128.01267504830628",
            "36.97454420738466",
            "https://www.youtube.com/@mi3nu",
            "주말에만 운영하는 깔끔한 캠핑장  가온오토캠핑장은 경북 포항시 북구 죽장면 일광리에 자리 잡았다. 포항시청을 기점으로 35km가량 떨어졌다. 자동차를 타고 희망대로와 새마을로를 번갈아 달리면 닿는다. 도착까지 걸리는 시간은 35분 안팎이다. 캠핑장에는 데크로 이뤄진 오토캠핑 사이트 8면과 강자갈이 깔린 사이트 23면이 마련돼 있다. 사이트 크기는 가로 9m 세로 9m, 가로 8m 세로 6m, 가로 8m 세로 8m다. 카라반과 트레일러 동반 입장이 가능하다. 2022년 현재 물놀이장을 설치할 예정이다. 주변에는 비학산자연휴양림이 있다.",
            "계곡, 산",
            "글램핑",
            "침대,에어컨,냉장고,유무선인터넷",
            "계곡 물놀이,산책로,운동장,수상레저,낚시",
            "불가능",
            "https://gocamping.or.kr/upload/camp/7103/thumb/thumb_720_9252NdzZEO7fhEVGdNgteCdv.jpg",
            "경기 포천시 이동면 금강로 5846",
            "400-3445-3322",
            "mimiminu"
        )

        val contractCampsite10 = ContractCampsite(
            "용진 글램핑",
            "127.5765247",
            "37.9133831",
            "https://namu.wiki/w/%EC%9D%B4%EC%9A%A9%EC%A7%84",
            "몽산포해수욕장 인근 깔끔한 캠핑장  솔비치캠핑장은 충남 태안군 남면 신장리에 자리 잡았다. 태안군청을 기점으로 10km가량 떨어졌다. 자동차를 타고 안면대로와 몽산포길을 번갈아 달리면 닿는다. 도착까지 걸리는 시간은 15분 안팎이다. 캠핑장에서 몽산포해수욕장까지 도보로 약 5분이 걸린다. 이 때문에 해수욕을 즐기기 좋다. 캠핑장에는 파쇄석으로 이뤄진 오토캠핑 사이트가 마련돼 있다. 카라반과 트레일러 동반 입장이 가능하다. 주변에는 몽산포항과 안면도쥬라기공원이 있어 연계 여행에 나서기 좋다.",
            "계곡, 산",
            "글램핑",
            "침대,에어컨,냉장고,유무선인터넷",
            "계곡 물놀이,산책로,운동장,수상레저,낚시",
            "불가능",
            "https://gocamping.or.kr/upload/camp/7112/thumb/thumb_720_69658K2KCWUZozqzZv8JJbRc.jpg",
            "몽산포 금강로 5846",
            "400-2345-3322",
            "yongjinzzang243"
        )

        val contractCampsite11 = ContractCampsite(
            "동호 글램핑",
            "127.5434061",
            "37.6622328",
            "https://m.search.naver.com/search.naver?sm=mtb_hty.top&where=m&oquery=%EC%9C%A0%EB%8F%99%ED%98%B8&tqi=iM%2FqWlpr4bCssOH4bCdssssssuN-135879&query=%EB%8F%99%ED%98%B8",
            "예쁜 정원과 함께하는 글램핑 정원과 함께 힐링할 수 있는 림스 글램핑은 전라남도에서 주최한 예쁜 정원 콘테스트에서 최우수상을 수상한 림스가든 안에 자리하고 있어 동화 속 같은 아름다운 정원에서 감성 글램핑을 즐길 수 있다. 아늑한 글램핑 내부 인테리어와 아이들이 신나게 물놀이할 수 있는 수영장과 트램펄린, 산책 코스까지 완벽한 편의 시설을 자랑한다. 또한 모든 객실은 세스코 관리를 받고 있어 더욱 믿을만하다. 반려견, 숯, 장작 등 개인 화기 및 반려견 동반은 불가하며 근처에 월출산과 가우도는 함께 둘러보기 좋은 관광 명소이다.",
            "계곡, 산",
            "글램핑",
            "침대,에어컨,냉장고,유무선인터넷",
            "계곡 물놀이,산책로,운동장,수상레저,낚시",
            "불가능",
            "https://gocamping.or.kr/upload/camp/7103/thumb/thumb_720_9252NdzZEO7fhEVGdNgteCdv.jpg",
            "전남 강진군 강진읍 해강로 1038-30",
            "400-2345-3322",
            "donggulVoiceMan"
        )

        val contractCampsite12 = ContractCampsite(
            "길주 글램핑",
            "127.3143217",
            "37.8367557",
            "https://terms.naver.com/entry.naver?docId=1071932&cid=40942&categoryId=39748",
            "수목이 우거진 감성적인 캠핑장  충주 비내캠프 체험학습장(충주 비내오토캠핑장)는 충북 충주시 앙성면 목미리에 자리 잡았다. 충주시청을 기점으로 35km가량 떨어졌다. 자동차를 타고 북부로와 앙암로를 번갈아 달리면 닿는다. 도착까지 걸리는 시간은 30분 안팎이다. 폐교를 리모델링해 감성적인 캠핑장으로 거듭난 공간이다. 수목이 우거진 캠핑장에는 파쇄석으로 이뤄진 일반캠핑 사이트 28면이 마련돼 있다. 사이트 크기는 가로 8m 세로 8m다. 2층으로 만들어진 원두막 캠핑 사이트 7면도 있다. 놀이터, 공원, 도사관, 교육관, 휴게실, 매점 등 부대시설도 알차다. 주변에는 수룡폭포와 봉황자연휴양림이 있다.",
            "계곡, 산",
            "글램핑",
            "침대,에어컨,냉장고,유무선인터넷",
            "계곡 물놀이,산책로,운동장,수상레저,낚시",
            "불가능",
            "https://gocamping.or.kr/upload/camp/100005/thumb/thumb_720_9424zPdiOBNO3Idt0Yr8NXRr.jpg",
            "경기 포천시 이동면 금강로 5846",
            "400-2345-3322",
            "gilju543"
        )
        contractList.add(contractCampsite1)
        contractList.add(contractCampsite2)
        contractList.add(contractCampsite3)
        contractList.add(contractCampsite4)
        contractList.add(contractCampsite5)
        contractList.add(contractCampsite6)
        contractList.add(contractCampsite7)
        contractList.add(contractCampsite8)
        contractList.add(contractCampsite9)
        contractList.add(contractCampsite10)
        contractList.add(contractCampsite11)
        contractList.add(contractCampsite12)
        return contractList
    }
}