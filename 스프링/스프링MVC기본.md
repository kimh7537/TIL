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
### ✔️ HTTP 요청 데이터

#### ✨ 쿼리 파라미터/HTML Form [요청 파라미터(request parameter) 조회]

`http://localhost:8080/request-param-v1?username=hello&age=20`
: `?`이전의 주소를 `RequestMapping`함 

**Version 1**
```java
@Slf4j
@Controller
/**
* 반환 타입이 없으면서 이렇게 응답에 값을 직접 집어넣으면, view 조회X
*/
@RequestMapping("/request-param-v1")
public void requestParamV1(HttpServletRequest request, HttpServletResponse
response) throws IOException {
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));
    
    log.info("username={}, age={}", username, age);
    
    response.getWriter().write("ok");
}
```

**Version 2**
```java
/**
* @RequestParam 사용
* - 파라미터 이름으로 바인딩
* @ResponseBody 추가
* - View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력
* -> @RestController와 비슷한 기능
*/
@ResponseBody
@RequestMapping("/request-param-v2")
public String requestParamV2(
    @RequestParam("username") String memberName,
    @RequestParam("age") int memberAge) {
    
    log.info("username={}, age={}", memberName, memberAge);
    return "ok";
}
```

**Version 3**
```java
/**
* @RequestParam 사용
* HTTP 파라미터 이름이 변수 이름과 같으면 @RequestParam(name="xx") 생략 가능
*/
@ResponseBody
@RequestMapping("/request-param-v3")
public String requestParamV3(
    @RequestParam String username,
    @RequestParam int age) {
    
    log.info("username={}, age={}", username, age);
    return "ok";
}
```

**Version 4**
```java
/**
* @RequestParam 사용
* String, int 등의 단순 타입이면 @RequestParam 도 생략 가능
*/
@ResponseBody
@RequestMapping("/request-param-v4")
public String requestParamV4(String username, int age) {
    log.info("username={}, age={}", username, age);
    return "ok";
}
```
- `String` , `int` , `Integer` 등의 단순 타입이면 `@RequestParam` 생략 가능
- `@RequestParam` 애노테이션을 생략하면 스프링 MVC는 내부에서 `required=false` 를 적용

**Version 5**
```java
@ResponseBody
@RequestMapping("/request-param-required")
public String requestParamRequired(
@RequestParam(required = true) String username,
@RequestParam(required = false) Integer age) {

    log.info("username={}, age={}", username, age);
    return "ok";
}
```
- `@RequestParam.required` 기본값은 파라미터 필수( true )
- `/request-param-required` 요청
  - username 이 없으므로 400 예외가 발생
- `/request-param-required?username=`
  - 파라미터 이름만 있고 값이 없는 경우 빈문자("")로 입력받음
- `/request-param-required` 요청
  - `@RequestParam(required = false) int age`
  - null 을 int 에 입력하는 것은 불가능(500 예외 발생)
  - null 을 받을 수 있는 `Integer` 로 변경
  
**Version 6**
```java
@ResponseBody
@RequestMapping("/request-param-default")
public String requestParamDefault(
@RequestParam(required = true, defaultValue = "guest") String username,
@RequestParam(required = false, defaultValue = "-1") int age) {

    log.info("username={}, age={}", username, age);
    return "ok";
}
```
- 파라미터에 값이 없을때, `defaultValue` 를 사용하면 기본 값 적용 가능
- 이미 기본 값이 있기 때문에 `required` 의미 없음
- `defaultValue` 는 빈 문자의 경우에도 설정한 기본 값이 적용된다.
  - `/request-param-default?username=` : 결과값(username=guest)
  

**Version 7**
```java
@ResponseBody
@RequestMapping("/request-param-map")
public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
    log.info("username={}, age={}", paramMap.get("username"),
    paramMap.get("age"));
    return "ok";
}
```
- `@RequestParam Map`
- `@RequestParam MultiValueMap` key 중복 일때 사용하기

**Version8**
```java
/**
* 참고: model.addAttribute(helloData) 코드도 함께 자동 적용됨
*/
@ResponseBody
@RequestMapping("/model-attribute-v1")
public String modelAttributeV1(@ModelAttribute HelloData helloData) {
    log.info("username={}, age={}", helloData.getUsername(),
    helloData.getAge());
    return "ok";
}
```
> - `HelloData` 클래스는 변수로 `String username`, `int age` 가지고 있음
- 롬복 `@Data` 적용해 주기
`@Getter` , `@Setter` , `@ToString` , `@EqualsAndHashCode` , `@RequiredArgsConstructor` 를 자동으로 적용

`@ModelAttribute` 실행 방법
1. HelloData 객체를 생성
2. 요청 파라미터(ex. username)의 이름으로 HelloData 객체의 프로퍼티를 찾고 setter(ex. setUsername())를 호출해서 파라미터의 값을 입력(바인딩)
>**프로퍼티**
getXxx -> xxx(프로퍼티)
setXxx -> xxx(프로퍼티)
**바인딩 오류**
`age=hello`처럼 숫자가 들어가야 할 곳에 문자 넣으면 `BindException`발생 

**Version 9**
```java
/**
* @ModelAttribute 생략 가능
*/
@ResponseBody
@RequestMapping("/model-attribute-v2")
public String modelAttributeV2(HelloData helloData) {
    log.info("username={}, age={}", helloData.getUsername(),
    helloData.getAge());
    return "ok";
}
```
`@ModelAttribute` 는 생략 가능

>생략할 때 규칙(생략된 것)
- `String` , `int` , `Integer` 같은 단순 타입 = `@RequestParam`
- 나머지 = `@ModelAttribute` (argument resolver 로 지정해둔 타입 외)

---
#### ✨ HTTP 요청 메시지 - 텍스트
HTTP 메시지 바디를 통해 데이터가 직접 넘어오는 경우 `@RequestParam`, `@ModelAttribute` 사용 불가(쿼리 파라미터가 아님)

**Version 1**
```java
@PostMapping("/request-body-string-v1")
public void requestBodyString(HttpServletRequest request,
HttpServletResponse response) throws IOException {
    ServletInputStream inputStream = request.getInputStream();
    String messageBody = StreamUtils.copyToString(inputStream,
    StandardCharsets.UTF_8);
    
    response.getWriter().write("ok");
}
```

**Version 2**
```java
@PostMapping("/request-body-string-v2")
public void requestBodyStringV2(InputStream inputStream, Writer responseWriter)
throws IOException {
    String messageBody = StreamUtils.copyToString(inputStream,
    StandardCharsets.UTF_8);
    
    responseWriter.write("ok");
}
```


**Version 3**
```java
/**
* - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
*   request/response 둘 다
*/
@PostMapping("/request-body-string-v3")
public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity){
 								//RequestEntity<String> httpEntity 변경 가능
	String messageBody = httpEntity.getBody();
    return new HttpEntity<>("ok");
    //return new ResponseEntity<>("ok", HttpStatus.CREATED); 변경 가능
}
```
`HttpEntity`
- HTTP header, body 정보를 조회
- `view` 조회 불가능(응답시 메시지 바디 정보 반환)
- `RequestEntity`


**Version 4**
```java
/**
* - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
*   request/response 둘 다
*/
@ResponseBody
@PostMapping("/request-body-string-v4")
public String requestBodyStringV4(@RequestBody String messageBody) {
    log.info("messageBody={}", messageBody);
    return "ok";
}
```
`@RequestBody`
- HTTP 메시지 바디 정보를 조회
`@ResponseBody`
- 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달(view 사용x)

> 
요청 파라미터를 조회하는 기능: `@RequestParam` , `@ModelAttribute`
HTTP 메시지 바디를 직접 조회하는 기능: `@RequestBody`

---
#### ✨ HTTP 요청 메시지 - JSON
**Version 1**
```java
ServletInputStream inputStream = request.getInputStream();
String messageBody = StreamUtils.copyToString(inputStream,
StandardCharsets.UTF_8);

HelloData data = objectMapper.readValue(messageBody, HelloData.class);
```

**Version 2**
```java
/**
* - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
*   request/response 둘 다
*/
@ResponseBody
@PostMapping("/request-body-json-v2")
public String requestBodyJsonV2(@RequestBody String messageBody) throws
IOException {
    HelloData data = objectMapper.readValue(messageBody, HelloData.class);
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```

**Version 3**
```java
/**
* HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 
(content type:application/json)
*/
@ResponseBody
@PostMapping("/request-body-json-v3")
public String requestBodyJsonV3(@RequestBody HelloData data) {
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return "ok";
}
```
**`@RequestBody`는 생략 불가능**
- @RequestBody 를 생략하면 `@ModelAttribute`가 적용됨



**Version 4**
```java
@ResponseBody
@PostMapping("/request-body-json-v4")
public String requestBodyJsonV4(HttpEntity<HelloData> httpEntity) {
    HelloData data = httpEntity.getBody();
    return "ok";
}
```

**Version 5**
```java
/**
* - HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 적용
*/
@ResponseBody
@PostMapping("/request-body-json-v5")
public HelloData requestBodyJsonV5(@RequestBody HelloData data) {
    log.info("username={}, age={}", data.getUsername(), data.getAge());
    return data;
}
```
`(content type:application/json)`
`(Accept: application/json)`

`@RequestBody` 요청
JSON 요청 -> HTTP 메시지 컨버터 ->  객체
`@ResponseBody` 응답
객체 ->  HTTP 메시지 컨버터 -> JSON 응답


---
## ✏️ HTTP 응답
### ✔️ HTTP 응답 데이터

#### ✨ 1. 정적 리소스
- `/static`, `public`, `/resources`, `/META-INF/resources`
다음 디렉토리에 있는 정적 리소스 제공 가능
- `src/main/resources/static/basic/hello-form.html` 이 경로에 파일이 있음
- `http://localhost:8080/basic/hello-form.html` 이렇게 실행 가능

---
#### ✨ 2. 뷰 템플릿

**response/hello.html 렌더링**
```html
...
<body>
<p th:text="${data}">empty</p>
</body>
...
```

**Version 1**
```java
@RequestMapping("/response-view-v1")
public ModelAndView responseViewV1() {
    ModelAndView mav = new ModelAndView("response/hello")
            .addObject("data", "hello!");

    return mav;
}
```

**Version 2**
```java
@RequestMapping("/response-view-v2")
public String responseViewV2(Model model) {
    model.addAttribute("data", "hello");
    return "response/hello";
}
```

**Version 3**
```java
@RequestMapping("/response/hello")  //경로의 이름이 뷰의 이름이랑 같으면 생략 가능
public void responseViewV3(Model model) {
    model.addAttribute("data", "hello");
}
```
- `@ResponseBody` 가 없으면 `response/hello` 로 뷰 리졸버가 실행되어서 뷰를 찾고, 렌더링
- `@ResponseBody` 가 있으면 뷰 리졸버를 실행하지 않고, HTTP 메시지 바디에 직접 response/hello 라는 문자가 입력
<br>
- Void를 반환
`@Controller` 를 사용하고, `HttpServletResponse`, `OutputStream(Writer)` 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 요청 URL을 참고해서 논리 뷰 이름으로 사용
요청 URL: `/response/hello`
실행: `templates/response/hello.html`
**명시성이 떨어지므로 잘 사용하지 않음**

<br>

---
#### ✨ 3. HTTP API, 메시지 바디에 직접 입력
**Version 1(문자)**
```java
@GetMapping("/response-body-string-v1")
public void responseBodyV1(HttpServletResponse response) 
throws IOException {
	response.getWriter().write("ok");
}
```

**Version 2(문자)**
```java
@GetMapping("/response-body-string-v2")
public ResponseEntity<String> responseBodyV2() {
	return new ResponseEntity<>("ok", HttpStatus.OK);
}
```


**Version 3(문자)**
```java
@ResponseBody
@GetMapping("/response-body-string-v3")
public String responseBodyV3() {
	return "ok";
}
```
- `@ResponseBody` 를 사용하면 view를 사용하지 않고, HTTP 메시지 컨버터를 통해서 HTTP 메시지를 직접 입력할 수 있음. `ResponseEntity` 도 동일한 방식으로 동작

---
**Version 1(JSON)**
```java
@GetMapping("/response-body-json-v1")
public ResponseEntity<HelloData> responseBodyJsonV1() {
	HelloData helloData = new HelloData();
	helloData.setUsername("userA");
	helloData.setAge(20);
	return new ResponseEntity<>(helloData, HttpStatus.OK);
}
```

**Version 2(JSON)**
```java
@ResponseStatus(HttpStatus.OK)
@ResponseBody
@GetMapping("/response-body-json-v2")
public HelloData responseBodyJsonV2() {
	HelloData helloData = new HelloData();
	helloData.setUsername("userA");
	helloData.setAge(20);
	return helloData;
}
```
- `@ResponseStatus(HttpStatus.OK)` 애노테이션을 사용해서 응답 설정 가능

> - **`@RestController`**
해당 컨트롤러에 모두 `@ResponseBody` 가 적용됨 
`@ResponseBody`, `@Controller`가 적용되어 있음
- @ResponseBody 는 클래스 레벨에 두면 전체 메서드에 적용됨 


---
## ✏️ HTTP 메시지 컨버터
### ✔️ HTTP 메시지 컨버터 사용 원리

**@ResponseBody 사용 구조**
![](https://velog.velcdn.com/images/w009981/post/01b8a85f-cfe5-4943-b30a-0f0207c1e46c/image.png)

**스프링 MVC가 HTTP 메시지 컨버터 적용하는 경우**
HTTP 요청: `@RequestBody` , `HttpEntity(RequestEntity)` 
HTTP 응답: `@ResponseBody` , `HttpEntity(ResponseEntity)` 

#### ✨ 1. 메시지 컨버터 종류
- `ByteArrayHttpMessageConverter` : `byte[]` 데이터를 처리
**클래스 타입: `byte[]` , 미디어타입: `*/*`**
요청 ex) `@RequestBody byte[] data` + `content-type`
응답 ex) `@ResponseBody return byte[]` + `Accept` 
응답 $\rarr$ 쓰기 미디어타입(Response에 content-type이렇게 작성됨)`application/octet-stream`

- `StringHttpMessageConverter` : `String` 문자로 데이터를 처리
**클래스 타입: `String` , 미디어타입: `*/*`**
요청 ex) `@RequestBody String data` + `content-type`
응답 ex) `@ResponseBody return "ok"` + `Accept` 
응답 $\rarr$ 쓰기 미디어타입 `text/plain`

- `MappingJackson2HttpMessageConverter` : `application/json`
**클래스 타입: 객체 또는 HashMap , 미디어타입 application/json 관련**
요청 ex) `@RequestBody HelloData data` + `content-type`
응답 ex) `@ResponseBody return helloData` + `Accept`  
응답 $\rarr$ 쓰기 미디어타입 `application/json` 관련

<br>

#### ✨ 2. 메시지 컨버터 적용 방법
**HTTP 요청 데이터 읽기**
1. HTTP 요청이 오고, 컨트롤러에서 `@RequestBody` , `HttpEntity` 파라미터를 사용
2. 메시지 컨버터가 메시지를 읽을 수 있는지 확인하기 위해 canRead() 를 호출
- 대상 클래스 타입을 지원하는가.
ex) `@RequestBody` 의 대상 클래스 (`byte[]` , `String` , `HelloData` 등)
- HTTP 요청의 `Content-Type` 미디어 타입을 지원하는가.
ex) `text/plain` , `application/json` , `*/*`' 등

<br>
3. canRead() 조건을 만족하면 read() 를 호출해서 객체 생성하고, 반환(컨트롤러 파라미터로 반환)
<br>
<br>

**HTTP 응답 데이터 생성**
1. 컨트롤러에서 `@ResponseBody` , `HttpEntity` 로 값이 반환됨
2. 메시지 컨버터가 메시지를 쓸 수 있는지 확인하기 위해 canWrite() 를 호출
- 대상 클래스 타입을 지원하는가.
ex) return의 대상 클래스 (`byte[]` , `String` , `HelloData` 등)
- HTTP 요청의 `Accept` 미디어 타입을 지원하는가.
(더 정확히는 `@RequestMapping` 의 `produces`)
ex) `text/plain` , `application/json` , `*/*` 등

<br>
3. canWrite() 조건을 만족하면 write() 를 호출해서 HTTP 응답 메시지 바디에 데이터를 생성
<br><br>

>
```
content-type: application/json
@RequestMapping
void hello(@RequestBody HelloData data) {}
```
1. `@RequestBody` 클래스 `HelloData`
2. 미디어 타입 체크 : `application/json`
3. `MappingJackson2HttpMessageConverter`

---
### ✔️ 요청매핑 핸들러 어댑터
#### ✨ 1. 동작 방식
- HTTP 메시지 컨버터는 `@RequestMapping`을 처리하는 핸들러 어댑터인 `RequestMappingHandlerAdapter`에서 동작

![](https://velog.velcdn.com/images/w009981/post/97e55cd7-3d9c-47f3-89c5-5a9740bcc98d/image.png)

**`ArgumentResolver`**
- 파라미터를 유연하게 처리할 수 있게 도와줌
(ex. `@RequestParam`, `@ModelAttribute`와 같은 애노테이션/
`@RequestBody`, `HttpEntity`와 같은 HTTP 메시지 처리 등)
- `RequestMappingHandlerAdapter` 는 `ArgumentResolver` 를 호출해서 컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성
$\rarr$ `supportParamter()`를 내부적으로 호출해서 지원 가능 여부 체크하고 `resolveArgument()` 호출해서 실제 객체 생성
- 파리미터의 값이 모두 준비되면 컨트롤러를 호출하면서 값을 넘겨줌

**`ReturnValueHandler`**
- `ArgumentResolver` 와 비슷하게 동작함

---
#### ✨ 2. HTTP 메시지 컨버터
![](https://velog.velcdn.com/images/w009981/post/17f6f18b-9d7d-4322-80cb-c9ff5469ce62/image.png)
**요청**
- `@RequestBody`와 `HttpEntity` 를 처리하는 `ArgumentResolver` 가 존재
- `ArgumentResolver` 가 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성

**응답**
- `@ResponseBody` 와 `HttpEntity` 를 처리하`ReturnValueHandler` 가 존재
- HTTP 메시지 컨버터를 호출해서 응답 결과를 생성

> - `@RequestBody` `@ResponseBody`
`RequestResponseBodyMethodProcessor`(ArgumentResolver)사용
- `HttpEntity`
`HttpEntityMethodProcessor`(ArgumentResolver)사용

---

>김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard

---





---


<br>

>김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard
