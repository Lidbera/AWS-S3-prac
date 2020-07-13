# AWS S3 연동 샘플
Amazon S3 in Spring framework example

<hr>

### AmazonS3Service.class
- 업로드
- ajax를 통해 개수가 정해지지 않은 파일들 업로드(+ home.jsp)
- 파일명으로 경로 가져오기
- 삭제
- UUID를 이용한 파일명 변경

<hr>

### C3Config.class
- 프로퍼티 설정

<hr>

### s3.properties
프로퍼티 파일
- 버킷 이름
- 액세스 키
- 비밀 키
- 저장 경로

<hr>

### upload-context.xml
- MultipartResolver 설정

<hr>
