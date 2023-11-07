# 스프링MVC기본

## ✏️ 로깅
### ✔️ 로그 선언 방법
- `private Logger log = LoggerFactory.getLogger(getClass());`
- `private static final Logger log =LoggerFactory.getLogger(Xxx.class)`
$\rarr$ 클래스 명 넣어서 사용
- `@Slf4j` : 롬복
---
### ✔️로그 사용/특징

```java
//@Slf4j
@RestController
public class LogTestController {
	private final Logger log = LoggerFactory.getLogger(getClass());
    //롬복 사용하면 생략 가능
    
    @RequestMapping("/log-test")
    public String logTest() {
    
        String name = "Spring";
        
        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.info(" info log={}", name);
        log.warn(" warn log={}", name);
        log.error("error log={}", name);
        
        //로그를 사용하지 않아도 a+b 계산 로직이 먼저 실행됨, 이 방식으로 사용하면 X
        log.debug("String concat log=" + name);
        return "ok";
    }
}
```
> `@RestController`
반환 값으로 뷰를 찾지 않음. HTTP메시지 바디에 바로 입력.(JSON)
`@Controller`
반환 값이 String일 때, 뷰를 찾고 렌더링 됨

- 로그 출력(시간, 로그 레벨, 프로세스 ID, 쓰레드 명, 클래스 명, 로그 메시지)
- 로그 레벨
`TRACE > DEBUG > INFO > WARN > ERROR`
    - 개발 서버는 DEBUG 출력
    - 운영 서버는 INFO 출력

> **application.properties 에서 설정 가능**
```java
#전체 로그 레벨 설정(기본 info)
logging.level.root=info
#hello.springmvc 패키지와 그 하위 로그 레벨 설정
logging.level.hello.springmvc=debug
```

> **올바른 로그 사용법**
`log.debug("String concat log=" + name)`
로그 출력 레벨을 info로 설정해도 해당 코드에 있는 `""+name`가 실행됨. 자바 언어는 문자를 먼저 더하고 메서드를 호출함
`log.debug(debug log={}", name)`
로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않음. 
연산 자체가 발생하지 않음.

- 출력 모양을 조정 가능
- 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있음
- 성능도 일반 System.out보다 좋음 (내부 버퍼링, 멀티 쓰레드 등등)


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

---
**2. 메서드 매핑**
```java
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
        return "ok";
    }
    /**
     * 편리한 축약 애노테이션 (코드보기)
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        return "ok";
    }
 ```
 
 ---
 **3. PathVariable(경로 변수)**
 ```java
    /**
     * 변수명이 같으면 생략 가능
     * @PathVariable("userId") String userId -> @PathVariable userId
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data){  
    //@PathVariable String userId 이렇게 생략도 가능
        log.info("mappingPath userId={}", data);
        return "ok";
    }

    /**
     * PathVariable 사용 다중
     */
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable String userId, 
    @PathVariable Long orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }
```
---
**4. 파라미터 조건 매핑**
```java
    /**
     * 파라미터로 추가 매핑
     * params="mode",
     * params="!mode"
     * params="mode=debug"
     * params="mode!=debug" (! = )
     * params = {"mode=debug","data=good"}
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        return "ok";
    }
```
- 특정 파라미터가 있는지 확인하는 조건
`http://localhost:8080/mapping-param?mode=debug`

---
**5. 헤더 조건 매핑**
```java
    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",
     * headers="!mode"
     * headers="mode=debug"
     * headers="mode!=debug" (! = )
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        return "ok";
    }
```
- 특정 HTTP 헤더가 요청에 있으면 처리
---
**6. 미디어 타입 조건 매핑(consume/produce)**
```java
    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * consumes="application/json"
     * consumes="!application/json"
     * consumes="application/*"
     * consumes="*\/*"
     * consumes = MediaType.APPLICATION_JSON_VALUE
     * consumes = {"text/plain", "application/*"}
     */
    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
        return "ok";
    }

    /**
     * Accept 헤더 기반 Media Type
     * produces = "text/html"
     * produces = "!text/html"
     * produces = "text/*"
     * produces = "*\/*"
     * produces = {"text/plain", "application/*"}
	 * produces = MediaType.TEXT_PLAIN_VALUE
	 * produces = "text/plain;charset=UTF-8"
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")  
    //클라이언트가 받아들일 수 있는것
    public String mappingProduces() {
        return "ok";
    }
```
- HTTP 요청의 `Content-Type`, `Accept`를 기반으로 매핑
- `Accept`는 요청 이후 나중에 클라이언트가 받을 데이터의 형식을 의미함

---
*****
## ✏️ HTTP 요청
### ✔️ 헤더 조회

```java
@RequestMapping("/headers")
public String headers(HttpServletRequest request, 
HttpServletResponse response,
HttpMethod httpMethod,
Locale locale,
@RequestHeader MultiValueMap<String, String> headerMap, //모든 헤더 조회
@RequestHeader("host") String host, 
//String host의 변수 이름 변경 가능/ 특정 헤더 조회
@CookieValue(value = "myCookie", required = false) String cookie
) {
    log.info("request={}", request);
    log.info("response={}", response);
    log.info("httpMethod={}", httpMethod);
    log.info("locale={}", locale);
    log.info("headerMap={}", headerMap);
    log.info("header host={}", host);
    log.info("myCookie={}", cookie);
    return "ok";
}
```
**결과**
![](https://velog.velcdn.com/images/w009981/post/cc0e7eea-5997-410c-ba1d-291d6159f8f2/image.png)

> `MultiValueMap`
하나의 키에 여러 값을 받을 수 있음
`keyA=value1&keyA=value2`
```java
MultiValueMap<String, String> map = new LinkedMultiValueMap();
map.add("keyA", "value1");
map.add("keyA", "value2");
>
List<String> values = map.get("keyA"); //return 값이 List
```

---
### ✔️ 쿼리 파라미터/ HTML Form





---


<br>

>김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard
