#SecurityConfig

---
---
### ✏️ 
- `anyRequest()` : 처리하지 못한 나머지 경로
- `().authenticated()` : 로그인한 사용자만 접근 가능
- `().denyAll` : 모든 사용자 접근 불가능

- 상단부터 순서대로 동작하므로 순서에 유의하기


### ✏️ 커스텀 로그인 설정
- `SecurityConfig`를 만들면 커스텀 로그인을 따로 구현해줘야함
