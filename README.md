![header](https://capsule-render.vercel.app/api?type=waving&height=200&color=80471C&text=CampingUs🏕&section=header&reversal=false)
## Contributors
<table>
    <tr align="center">
        <td><B>강현구<B></td>
        <td><B>이지은<B></td>
        <td><B>장용진<B></td>
        <td><B>김민우<B></td>
        <td><B>유동호<B></td>  
    </tr>
    <tr align="center">
        <td>
            <img src="https://github.com/Ganghyungoo.png?size=120">
            <br>
            <a href="https://github.com/Ganghyungoo"><I>Ganghyungoo</I></a>
        </td>
        <td>
          <img src="https://github.com/nueijeel.png?size=120">
            <br>
            <a href="https://github.com/nueijeel"><I>nueijeel</I></a>
        </td>
        <td>
            <img src="https://github.com/YonjjinJang.png?size=120">
            <br>
            <a href="https://github.com/YonjjinJang"><I>YonjjinJang</I></a>
        </td>
        <td>
            <img src="https://github.com/DoReMinWoo.png?size=120">
            <br>
            <a href="https://github.com/DoReMinWoo"><I>DoReMinWoo</I></a>
        </td>
        <td>
            <img src="https://picsum.photos/120/120">
            <br>
            <a href="https://github.com/y-d-h"><I>y-d-h</I></a>
        </td>
    </tr>
</table>  


# CampingUs  
**CampingUs는 캠핑러들을 위한 올인원 캠핑 정보 쇼핑몰 앱입니다.📲  
판매자용 앱과 구매자용 앱으로 손쉽게 제품을 등록 및 문의를 관리하며 다양한 제품을 구매할 수 있습니다!  
커뮤니티를 통해 다른 사람들과 실시간으로 캠핑 정보를 공유 및 소통하며 전국의 캠핑장 위치 및 상세정보를 상세하게 확인해보세요!**

## 판매자 앱(주요 화면)
![image](https://github.com/Ganghyungoo/CampingUs/assets/104668071/249f0a4f-00a2-4202-a022-c4ee7d975748)
## 구매자 앱(주요 화면)
![image](https://github.com/Ganghyungoo/CampingUs/assets/104668071/3daceacb-f364-44df-94f2-c14016fe94f5)
![image](https://github.com/Ganghyungoo/CampingUs/assets/104668071/4cc558be-cbeb-47b9-9213-2a7d8cfccea1)

## :snowman: Language, libraries and tools used :snowman:
| What | How |
| --- | --- |
| 🗣 Language | [Kotlin](https://kotlinlang.org/) |
| 🎭 User Interface (Android) | [Material Design 3](https://m3.material.io/components/buttons/) |
| 🏗 Architecture | MVVM |
| 🌊 Async | [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) |
| 🌐 Networking | [Retrofit](https://square.github.io/retrofit/), [Gson](https://github.com/google/gson) |
| ☁ BackEnd Platform |[Firebase RealTime Database](https://firebase.google.com/docs/database?hl=ko)|

## DB 구조
1.제품 관련 테이블  
![image](https://github.com/Ganghyungoo/CampingUs/assets/104668071/392507eb-9ad9-499f-9cb5-a4653c09509d)
  

2.유저 관련 테이블
![image](https://github.com/Ganghyungoo/CampingUs/assets/104668071/567ee5bb-0f5f-4bcd-bfde-7d51d8c9edf4)


3.캠핑장 및 커뮤니티 테이블
![image](https://github.com/Ganghyungoo/CampingUs/assets/104668071/fb3f8e69-0e15-4166-9bc4-4a609232184f)

## Derectory 구조
1.판매자 앱
```
📱 app
┣ 📂 manifest
┃
┣ 📂 java
┃ ┣ 📦 package
┃   ┣ 📂 viewmodel
┃   ┣ 📂 dataclassmodel
┃   ┣ 📂 repository
┃   ┣ 📂 ui
┃     ┣ 📂 main
┃     ┣ 📂 injury
┃     ┣ 📂 myInfo
┃   	┣ 📂 Notification
┃     ┣ 📂 product
┃     ┣ 📂 sellState
┃     ┣ 📂 sellStateDetail
┃     ┣ 📂 updateMyInfo
┃     ┣ 📂user
┃ 
┣ 📂 res(resource)
┃ ┣ 📂 drawable
┃ ┣ 📂 layout
┃ ┣ 📂 mipmap
┃ ┣ 📂 values
┃ ┣ 📂 xml
┃ ┣ 📂 menu
┃
┣ 🐘 Gradle Scripts
```

2.구매자 앱
```
📱 app
┣ 📂 manifest
┃
┣ 📂 java
┃ ┣ 📦 package
┃   ┣ 📂 viewmodel
┃   ┣ 📂 dataclassmodel
┃   ┣ 📂 repository
┃   ┣ 📂 ui
┃     ┣ 📂 main
┃     ┣ 📂 campsite
┃     ┣ 📂 injuiry
┃   	┣ 📂 myProfile
┃     ┣ 📂 payment
┃     ┣ 📂 review
┃     ┣ 📂 shopping
┃     ┣ 📂 user
┃     ┣ 📂user
┃ 
┣ 📂 res(resource)
┃ ┣ 📂 drawable
┃ ┣ 📂 layout
┃ ┣ 📂 mipmap
┃ ┣ 📂 values
┃ ┣ 📂 xml
┃ ┣ 📂 menu
┃
┣ 🐘 Gradle Scripts
```



