# Object클래스

## ✏️ 로깅
### ✔️ 로그 선언 방법
- `private Logger log = LoggerFactory.getLogger(getClass());`
- `private static final Logger log =LoggerFactory.getLogger(Xxx.class)`
$\rarr$ 클래스 명 넣어서 사용
- `@Slf4j` : 롬복
---

---
## ✏️ Request 매핑
### ✔️ 요청 매핑 종류
**1. 기본 매핑**
```java
    @RequestMapping("/hello-basic")
    public String helloBasic(){
        return "ok";
    }
```
- 다중 설정 가능(`{"/hello-basic", "/hello-go"}`)
- @RequestMapping 에 method 속성으로 HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게 호출
$\rarr$ 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE

> 스프링 부트 3.0 이전 버전: URL(localhost:8080/...) $\rarr$ 매핑(RequestMapping)
- 매핑 정보: `/hello-basic`
- URL 요청 정보: `/hello-basic` `/hello-basic/`
> 
스프링 부트 3.0 이후 버전: URL $\rarr$ 매핑
- 매핑: `/hello-basic` URL 요청: `/hello-basic`
- 매핑: `/hello-basic/` URL 요청: `/hello-basic/`
>
기존에는 마지막에 있는 / (slash)를 제거했지만, 스프링 부트 3.0 부터는 마지막의 / (slash)를 유지한다.
