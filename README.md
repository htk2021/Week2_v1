# Week2_v1
몰입캠프 2주차 프로젝트

# 독서 기록장: "읽을래요"

# 팀원 황태경 김서경

# 탭 0 : 로그인 & 회원가입

카카오 계정 혹은 읽을래요 전용 계정을 생성해서 앱을 이용할 수 있습니다.

앱을 다운로드 받고 처음 로그인을 한 뒤에는, 로그아웃 하기 전까지 계정을 기억해 앱에 접속할 때마다 자동으로 기존 계정에 로그인합니다.

<img src="https://github.com/htk2021/Week2_v1/assets/138096893/33c39c8a-4123-4330-a9ab-7d6a0fe51668.jpg" width="200" height="400"/>

# 탭 1 : 프로필
유저의 닉네임, 아이디(혹은 이메일), 사진을 불러와서 프로필에 보여줍니다. 유저가 작성한 피드(책 후기, 리뷰)들은 프로필 하단에 위치합니다.

추가 버튼을 누르면 후기를 추가할 수 있는 화면으로 이어지고, 도서를 검색하면 비슷한 이름의 도서 목록을 확인할 수 있습니다.

도서를 선택하면 상세정보 페이지로 이어지며 해당 페이지에서 '작성'을 누르면 다시 후기 작성 페이지로 돌아갑니다.

후기 작성 페이지에는 선택한 도서의 기본 정보들이 자동으로 등록되고, 추가적으로 적고자 하는 내용들을 작성할 수 있습니다.

인상깊은 구절의 경우 직접 타이핑해서 작성할 수도 있고, OCR을 이용해 원하는 페이지를 촬영하여 텍스트를 추출해서 원하는 부분을 복사&붙여넣기 할 수도 있습니다.

작성 후 완료 버튼을 누르면 작성한 후기가 DB에 저장되고, 프로필이 업데이트됩니다.

작성한 리뷰들은 클릭해서 상세정보를 확인하고, 수정할 수 있습니다. 또한 길게 클릭하여 삭제할 수 있습니다.

프로필에서 팔로워, 팔로잉을 누르면 나와 팔로워, 팔로잉 관계에 있는 계정들의 목록을 확인할 수 있습니다.

우측 상단의 설정 탭을 누르면 닉네임과 비밀번호를 수정할 수 있습니다. 

<img src="https://github.com/htk2021/Week2_v1/assets/138096893/64135d12-9179-4590-a36e-bb9e233a6b2f.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/2330d0cb-4400-436d-86d0-869d17bf5f74.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/14cb599d-4b2f-4e85-a187-d58c839b7da3.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/a9640781-3ecf-482d-b2ea-9e770a5229a1.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/57d7748a-d47b-485c-bffd-d6482307a586.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/50358649-4bd7-47f0-b36d-d80e47ffffd6.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/5d518f39-72b2-4978-8d85-24e88b0091e7.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/d2869a0f-efef-485c-b490-9e7096db9fca.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/68db73cf-387b-4453-9872-3d662551160f.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/927bc6bc-2ebd-4145-8746-cdbd60c45444.jpg" width="200"/>


# 탭 2 : 피드 
유저가 팔로우하고 있는 계정의 피드들이 먼저 상단에 노출되며, 하단에는 무관한 유저의 피드들도 보이게끔 구성하였습니다.

피드를 클릭하면 상세 정보를 확인할 수 있고, 계정을 클릭해 작성한 유저의 프로필을 볼 수 있습니다.

상단의 검색 창에서는 직접 다른 유저를 검색할 수 있습니다. 여기서 각 유저 요소를 클릭하면 해당 유저의 프로필로 이어집니다.

<img src="https://github.com/htk2021/Week2_v1/assets/138096893/8f9c9e9c-576c-41c0-b3f8-2a082c2d52a2.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/c8955215-59fd-4c9a-8aa8-19e417924fcb.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/05341e95-13c9-4399-8982-749594979188.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/77c02cf0-45d7-4283-a5cf-1f78a26cb1ce.jpg" width="200"/>
<img src="https://github.com/htk2021/Week2_v1/assets/138096893/418fb86b-0fbb-496a-aa99-1eae3024cf0b.jpg" width="200"/>


# 탭 3 : 검색
책 제목으로 검색하여 해당 책에 대한 리뷰들을 모아서 확인할 수 있습니다.

이전 탭에서들과 마찬가지로, 리뷰를 클릭하면 상세 정보를 확인하거나 리뷰를 작성한 유저의 프로필로 이동할 수 있습니다.

<img src="https://github.com/htk2021/Week2_v1/assets/138096893/e8b34ac2-4a41-44a9-8af7-c3d882bb52c3.jpg" width="200"/>


# 개선사항

-OCR을 이용해 텍스트를 추출하는 과정에서 Tessaract의 한글 인식률이 매우 저조하였습니다. 실제로 앱을 운영하기 위해서는 유료로 ML Kit를 이용해야 할 것입니다.

-설정 탭에서 프로필 사진을 수정하는 기능이 미구현 상태입니다. 구글 Drive를 이용해 사진을 드라이브에 저장하고, 사진의 URL링크를 DB에 저장하는 방식으로 프로필 사진 수정 기능을 추가하고자 합니다.
