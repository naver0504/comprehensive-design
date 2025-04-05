![Group 27](https://github.com/user-attachments/assets/6c05aa48-92a3-409b-af24-1945a17d6f42)

### **Back-End Developer**                        
---
- 개발: 2024.07 ~ 2024.12
- 개발 인원: 백엔드 개발 1명, 프론트 엔드 개발 1명, AI 모델 개발 2명

### 🛠 기술 스택

---
- Spring, Spring Boot, Spring Batch, Spring Data JPA, MySQL
- Git, Docker, AWS EC2, GitHub Actions

### 📖 서비스 내용

---

- 다양한 검색 조건으로 130만 건의 서울시 아파트 매매 정보와 이상 거래 여부를 손쉽게 확인
- 아파트 실거래 상세 정보와 AI 모델의 예측 거래가를 조회
- 최근 1년간의 실거래가와 AI 예측 가격을 그래프로 시각화하여 비교 분석 및 확인

### 🎛️ 시스템 아키텍쳐

---
![budda](https://github.com/user-attachments/assets/6bbdfee3-8502-49c7-8836-f9ab0e8ab3bb)

### 🙋‍♂️ 역할

---

1. **아파트 실거래가 데이터 수집 및 데이터베이스 저장**
    
    [**국토교통부의 아파트 매매 실거래가 API**](https://www.data.go.kr/data/15126469/openapi.do#/API%20%EB%AA%A9%EB%A1%9D/getRTMSDataSvcAptTrade)를 활용하여 **2006년부터 현재까지**의 **서울시 아파트 실거래 데이터**를 데이터베이스에 저장하는 배치 프로그램을 구현했습니다.
    
    - **아파트 실거래 API**를 통해 수집한 데이터를 처리하는 과정에서, 각 데이터에 대해 **법정동 코드와 관련된 엔티티**를 **매핑**할 때 매건마다 **추가적인 Database I/O**가 발생하여 배치 작업에 **부정적인 영향**을 주고 있었습니다.

      이를 해결하기 위해, **법정동 코드 관련 엔티티**를 `CustomCacheRepository`를 통해 **배치 처리 시작** 시점에 **한 번만 조회**하여 **캐싱**하도록 개선하였습니다. 이러한 접근 방식으로 **총 83,377**건의 데이터를 저장하는 데 소요되던 시간을 **기존 2분 6초**에서 **54**초로 단축하는 **성능 개선 효과**를 가져왔습니다.
    - API 호출과 데이터 읽기 로직을 처리하기 위해 `ItemStreamReader`를 커스텀하게 구현했습니다. 해당 페이지의 **데이터 저장 성공** 시 `ExecutionContext`를 활용하여 관련 **페이지 정보를 업데이트**함으로써, 배치 작업이 중단되는 상황이 생기더라도 **재시작 시 누락 없이 저장**할 수 있었습니다.
2. **좌표 저장**
카카오 API를 활용해 각 **실거래 아파트 정보**를 담고 있는 엔티티에 **좌표 데이터를 저장**하는 기능을 구현했습니다. 아래의 방식으로 **83,377개 데이터**를 처리하는 데 **20분 530초** 걸리던 작업 시간을 **34.330초**로 대폭 단축했습니다.
    - 기존의 `JpaPagingItemReader` 대신 NoOffset 방식을 적용한 `ItemReader`를 사용하여 약 135,000개의 데이터를 읽는 속도를 기존 1분에서 24초로 단축했습니다. 이를 통해 대략 60%의 성능 향상을 달성할 수 있었습니다.
    - 좌표를 가져오기 위해 API 요청을 하는 과정에서 **매 Chunk마다 최대 1000개의 HTTP 요청**이 발생할 수 있는 상황이였습니다. **동일한 주소**에 대해서는 항상 **동일한 좌표**를 반환하므로, 동일한 주소는 **API 호출을 생략**하고 **캐싱된 좌표**를 반환하도록 설계했습니다. 이를 통해 **불필요한 API 요청**을 줄였습니다.
            
        또한 `ThreadPool`을 생성하여** 병렬 처리**하도록 설계했습니다. 이 과정에서 `poolSize`를 5 이상으로 설정하면 **과도한 API 요청**으로 인해 **HTTP 429(Too Many Requests)** 에러가 발생하는 문제가 있어, `poolSize`를 4로 조정하여 안정적으로 처리하도록 구현했습니다.
    - **AsyncProcessor**로부터 받은 결과 데이터들의 작업이 끝날 때까지 **대기**한 후, **유효한 좌표**만 **필터링**하여 처리하도록 했습니다. **좌표 데이터**를 데이터베이스에 **효율적으로** 저장하기 위해 **JDBC Execute Batch**를 사용했으며, **여러 개의 쿼리**를 **한 번**에 묶어서 데이터베이스로 전송함으로써 **Database I/O 작업**을 **최소화**했습니다. 
3. **아파트 실거래가 데이터 조회**
    
   **데이터베이스**에 저장된 아파트 실거래 데이터를 **다양한 필터링 조건**과 **정렬 조건**을 활용하여 **원하는 데이터**를 조회할 수 있도록 구현했습니다.
    
    - **조회 과정**에서 130만 건이 넘는 데이터를 보유한 테이블 간의 **Join**으로 인해, 일정 페이지 이상 조회 시 **응답 시간이 2초**를 초과하는 성능 저하 문제가 발생하였습니다. 이를 해결하기 위해 우선 사용자의 **필터 조건**에 따라 **불필요한 Join을 동적으로 제거**하도록 쿼리를 개선하였습니다.

      더불어 **Join 및 필터 조건**에 사용되는 컬럼에 대해 **적절한 인덱스를 설정**하고, **Covering Index 기법을 응용**하여 먼저 조회에 **필수적인 ID 목록**만을 추출한 후, 이를 기반으로 **IN 쿼리 방식**으로 쿼리 구조를 **재설계**하였습니다. 이때 ID만을 가져오는 쿼리의** 실행 계획**이 **Using index**가 주가 되도록 변경된 것이 **주요 성능 개선 요인**이 되었습니다. 이는 **약 50%의 조회 성능 개선 효과**를 가져왔습니다.

### 🧐 느낀 점

---

아파트 정보를 정규화하지 못한 점이 가장 아쉬웠습니다. [**국토교통부의 공동주택 단지 목록제공 API**](https://www.data.go.kr/data/15057332/openapi.do)를 활용해 아파트 정보를 먼저 저장하고, 이후 [**아파트 매매 실거래가 API**](https://www.data.go.kr/data/15126469/openapi.do#/API%20%EB%AA%A9%EB%A1%9D/getRTMSDataSvcAptTrade)를 통해 거래 금액과 거래 일자 등의 실거래 내역을 저장하려 했습니다. 하지만 E편한세상과 이편한세상처럼 두 API에서 제공하는 아파트 이름이 불일치하는 경우가 많았고, 실거래가 API에서는 아파트 이름 없이 동 정보만 표시된 경우도 많아 정규화를 완료하지 못했습니다.

**성능 개선**을 위해 여러 가지 시도를 하면서 **기업들의 기술 블로그나 유튜브 발표 영상** 등을 많이 참고하게 됐습니다. 단순히 방법만 따라 하기보다는, **왜** 이런 방법들이 성능을 높여주는지를 하나씩 찾아보면서 자연스럽게 **CS적인 개념**들도 더 깊게 이해할 수 있었던 것 같습니다. 결국 **성능 문제**는 단순한 코드 수정이 아니라, **운영체제나 DB, 네트워크**와 같은 **전반적인 컴퓨터 과학 지식**이 바탕이 돼야 해결할 수 있다는 걸 느꼈습니다.

### 👀 서비스 화면
---
![image](https://github.com/user-attachments/assets/85d39dd6-19bc-40f9-b046-c36f16ad80d2)
![image](https://github.com/user-attachments/assets/b29d1486-28ba-4715-85d9-5cfffc97192d)


