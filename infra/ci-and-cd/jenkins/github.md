# Github

### 토큰 생성

깃허브 에서 Access Token을 생성해준다.\
※ profile -> setting -> [developer setting](https://github.com/settings/tokens) -> repo , admin:repo\_hook



### 토큰 등록

Jenkins > Manage Jenkins > Configure System -> git server 에 토큰을 등록해준다.\
Secret Text에 차례로  Token, Git Id, desc를 작성후 Test connection로 연결을 확인한다

<figure><img src="../../../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>

### 리포지토리

아무 프로젝트나 생성하여 깃 리포지토리 URL(.git)과 설정한 Credential을 설정한뒤\
프로젝트를 빌드하면 해당 Job의 작업공간에 소스파일이 담긴다.

<figure><img src="../../../.gitbook/assets/image (1).png" alt="" width="461"><figcaption></figcaption></figure>



### Web Hook

Git repository 설정에 들어가 add web hook을 선택한뒤 차례대로\
{jenkins\_path}/github-webhook 를 작성후 , push event를 선택하여 추가한다

<figure><img src="../../../.gitbook/assets/image.png" alt="" width="466"><figcaption></figcaption></figure>
