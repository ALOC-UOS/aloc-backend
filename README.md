## local 에서 빌드하기 OR 실행해서 스웨거 확인하기

1. main 브랜치 클론하기
2. src/main/resoureces 폴더 생성 후 `application.yml` , `application-jwt.yml` , `application-test.yml` 파일 추가하기(하단에 코드 있음)
3. 로컬 DB 셋팅
    1. docker, docker compose 설치
    2. `docker compose up -d` 로 db 실행 → dockerfile 
4. 빌드/실행
    1. `build` : 우측 gradle>build>build OR `./gradlew build` 
    2. `실행` : 우측 gradle > application > bootRun OR `./gradlew bootrun` 
        1. http://localhost:8080/swagger-ui/index.html → 로컬 스웨거 주소
5. 그 후 개발 시 브랜치 파서 하기~~
