# 개발 전 세팅해야 할 것
1. Intelij > settings > Editor > Code Style > Java -> **Scheme : Google configuration 설정**
2. Intelij > settings > Plugins > **CheckStyle-IDEA 설치** -> Naver check rules 설정
3. Intelij > settings > Plugins > **CodeMetrics 설치**

> 빌드 시에 자동으로 checkstyle을 확인하도록 설정해두었습니다. <br>
> checkstyle을 모두 맞추어야 build가 성공적으로 진행됩니다.


## Code Style google Style로 설정하는 법 
1. [파일 다운]https://google.github.io/styleguide/javaguide.html
2. intellij-java-google-style.xml 을 다운받아 해당 프로젝트 경로에 저장합니다.
3. Editor > Code Style > Java > 더보기 > Import Scheme > IntelliJ Style IDEA Code Style xml

---
# 개발 서버 돌리는 법
1. 로컬 데이터베이스 설정

```docker-compose up -d ```

로컬 디비를 사용하기 위해 postgresql 컨테이너를 실행합니다.
```
POSTGRES_DB=aloc
POSTGRES_PASSWORD=password
POSTGRES_USER=postgres
```
위 설정을 따라 로컬 디비가 세팅됩니다.

2. 우측 `gradle -> build -> build`
3. 우측 `gradle -> application -> bootrun`

[swagger 접속]http://localhost:8080/swagger-ui/index.html

docker buildx build --platform linux/amd64,linux/arm64 -t bae4614/aloc-spring:v2.1 --push .
docker run -d --name aloc-spring -p 8080:8080 --network shared-network bae4614/aloc-spring:v2.2
