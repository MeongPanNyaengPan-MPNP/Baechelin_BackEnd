
![임시 커버](https://user-images.githubusercontent.com/90380269/181488434-e1e7c2df-dcee-4f1e-83d0-12041663da59.png)

# Bae-Chelin (배슐랭)

<br>

### “사회적 교통 약자도 편하게 식사하세요”

<br>
<br>

배슐랭은 사회적 교통 약자가 보다 쉽게 이용할 수 있는 배리어 프리 식당이나 카페 등 가게 정보를 알려주고 사용자 위치 맞춤 추천 서비스를 제공합니다.

사회적 교통 약자분들 위한 음식점 정보와 유저들의 생생한 방문 경험이 담긴 리뷰들로 편하게 방문해보세요! 

<br>

배슐랭이 사회적 교통약자분들이 편하게 살아갈 수 있도록 한 걸음 나아가겠습니다.

<br>
<br>

👩‍🦼 [배슐랭 바로가기](https://bae-chelin.com/)

<br>

![Line 1](https://user-images.githubusercontent.com/90380269/181489532-4bbb5041-8de1-4ac9-89b2-9400e577ddd2.png)

<br>

# Background

![background](https://user-images.githubusercontent.com/90380269/181490264-a9c64413-8c59-4ad7-863f-2e423e705858.png)

<br>


<b>배리어 프리 (Barrier-free)</b>는 사회적 약자가 생활하기에 불편함을 주는 물리전, 제도적 장벽을 제거하자는 운동입니다.

생활공간에서 장애인과 비장애인 간의 장벽을 없애자는 움직임이 증가하고 있지만 아직도 **사회적 교통약자**는 **행복을 추구할 기본적인 권리**를 보장받지 못하고 있습니다.

<br>
<br>

**배슐랭**에서는 **배리어 프리 가게들을 알려주고, 위치 기반 맛집 정보 서비스를 제공**해 사회적 교통약자분들의 보다 편리한 가게 이용을 가능하게 합니다.


<br>

# Features

### 🏘 내 주변에 있는 배리어 프리 가게
    
- 사용자의 **현재 위치**를 기반으로 주위의 배리어 프리 가게를 한눈에 확인할 수 있습니다.
    
<br>

### 🤔 다른 사람들은 어느 가게를 많이 갔을까?
    
- **별점 순, 북마크 순**으로 인기 가게들을 확인할 수 있습니다.
    
- 다른 사람들의 **실시간 후기**를 확인할 수 있습니다.
    
<br>    

### ☑️ 나한테 필요한 배리어 프리 시설은?
    
- 내가 원하는 **배리어 프리 시설별**(휠체어 경사로, 엘레베이터, 전용 화장실, 전용 주차장, 출입구 단차)로 가게들을 확인할 수 있습니다.
    
<br>    

### 🗺 가게들을 지도로 한눈에 확인
    
- 가게 카드를 클릭하면 상세 페이지에서 위치를 **지도**로 한 눈에 확인할 수 있습니다.
    
- 사용자의 **현재 위치**를 기반으로 주위의 배리어 프리 가게를 **지도**로 한 눈에 확인할 수 있습니다.
    
<br>    

### 🔖 가고 싶은 가게를 북마크
    
- 가고 싶거나 저장해두고 싶은 가게를 **북마크** 하고, **폴더별**로 확인할 수 있습니다.
    
<br>    

### 📄 내 경험을 공유하기
    
- 방문 후기를 작성해서 다른 사용자들에게 **별점과 리뷰를 공유**할 수 있습니다.
    
<br>

### 🔍 찾고 싶은 가게를 검색하기
    
- **지역과 키워드**로 찾고 싶은 가게를 **검색**할 수 있습니다.

    
<br>
<br>

![Line 1](https://user-images.githubusercontent.com/90380269/181489532-4bbb5041-8de1-4ac9-89b2-9400e577ddd2.png)

# Project

시연영상

발표자료

[팀 노션](https://planet-punishment-427.notion.site/99-7-4-a179c828bbbf47aea74d2bea85f47372)    

<br>

## Project Timeline

`2022-06-24` ~  `2022-08-01` (6주)

<br>

## Tech Stack

### Language

- JAVA


### Database
- MySQL

### Deploy

- Github Actions
- Code Deploy
- Nginx
- AWS EC2
- AWS S3
- AWS RDS

### Tech

- Spring Boot
- Spring Security
- Spring Batch
- QueryDSL
- Spring Data JPA
- JWT
- OAuth2.0
- Jsoup
- Swagger
- WebClient & RestTemplate

### Others
- Public API
- Kakao Maps API


<br>

## Project Design

### Service Architecture

<img width="2343" alt="MPNP Architecture" src="https://user-images.githubusercontent.com/90380269/181498046-80e24864-074e-4b41-a561-db9183989d29.png">

<br>

### ERD

![배슐랭_ERD](https://user-images.githubusercontent.com/90380269/181498144-bb8968c8-f6be-44a6-8fca-6a9dba3bd8bf.png)


### API

[API 설계](https://planet-punishment-427.notion.site/API-829b965bd9ed4347ab51701fcf1d3896)


<br>

## Development

### Core Tech

<details>
<summary>회원가입 / 로그인 / 사용자 인증</summary>
<div markdown="1">

<ul>
&nbsp; &nbsp; &nbsp; &nbsp;<li>카카오, 네이버, 구글 소셜로그인으로 별도 회원가입 과정 없이 간편하게 가입</li>
&nbsp; &nbsp; &nbsp; &nbsp;<li>JWT 토큰 인증방식을 통해 로그인 인증 관리 및 Access / Refresh Token을 활용하여 로그인 기간 관리, 보안 강화</li>
</ul>

</div>
</details>

<br>

<details>
<summary>배리어 프리 시설 및 가게 사진 데이터 수집</summary>
<div markdown="1">

<ul>
&nbsp; &nbsp; &nbsp; &nbsp;<li>Public API를 활용하여 배리어 프리 시설 데이터 수집</li>
&nbsp; &nbsp; &nbsp; &nbsp;<li>Jsoup을 활용하여 가게 사진 데이터 크롤링</li>
&nbsp; &nbsp; &nbsp; &nbsp;<li>Webclient를 활용하여 API 호출 성능 개선</li>
</ul>

</div>
</details>

<br>

<details>
<summary>대용량 데이터 관리</summary>
<div markdown="1">

<ul>
&nbsp; &nbsp; &nbsp; &nbsp;<li>Spring Batch와 Scheduler를 활용하여 대용량 데이터를 주기적으로 수집 및 업데이트</li>
</ul>

</div>
</details>

<br>

<details>
<summary>CI / CD와 무중단 배포</summary>
<div markdown="1">

<ul>
&nbsp; &nbsp; &nbsp; &nbsp;<li>Github Actions와 Code delpoy를 활용하여 CI/CD 구현</li>
&nbsp; &nbsp; &nbsp; &nbsp;<li>Nginx를 활용하여 무중단 배포 구현</li>
</ul>

</div>
</details>

<br>

### Trouble Shooting

[WebClient & RestTemplate](https://planet-punishment-427.notion.site/WebClient-RestTemplate-1412e5edae0d4dac9a89cde89658812c)

[Refresh Token 적용](https://planet-punishment-427.notion.site/Refresh-Token-fee659681d4a4c2483a495a3ad567173)

[가게 검색 기능](https://planet-punishment-427.notion.site/6db6471276004027be4a0784de4964ed)


<br>

![Line 1](https://user-images.githubusercontent.com/90380269/181489532-4bbb5041-8de1-4ac9-89b2-9400e577ddd2.png)

# Team MPNP - Backend

<br>

팀 멍판냥판 백엔드 개발진들🧡


| [진유진🔰](https://github.com/Anna-Jin) | [김선현](https://github.com/kokoa322) | [정소이](https://github.com/JSoi) |                                                                                                            
| :---------------------------------: | :----------------------------------: | :-----------------------------: |
| <img src="https://user-images.githubusercontent.com/90380269/181727981-603a62e6-bc9d-4b69-ae87-5bf876bbc1c8.png" alt="진유진" width="200px"/> |  <img src="https://user-images.githubusercontent.com/90380269/181737024-7c476a43-c276-4d99-b756-8bfc14d189a1.jpeg" alt="김선현" width="200px"/> | <img src="https://user-images.githubusercontent.com/90380269/181737361-4e7f4996-6426-4a11-9be0-0ad8bd16a182.png" alt="정소이" width="200px" /> |
| `Spring Security` `JWT` <br> `OAuth2.0` `소셜 로그인` <br> `가게 검색` `가게 상세 조회` <br> `가게 등록` `관리자 페이지` | `Spring Batch` `Scheduler` <br> `리뷰 작성, 삭제, 수정, 조회` <br> `북마크 및 북마크 폴더 생성, 삭제` <br> `최근 등록한 북마크 조회` | `Public API` `Kakao Map API` <br> `WebClient` `데이터수집 및 가공` <br> `가게 위치, 카테고리, 태그별 리스트 조회` <br> `현재 위치 조회` `회원 정보 조회`|

