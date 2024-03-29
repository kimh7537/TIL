# (MVC)4.스프링MVC기본

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

> 스프링 부트 3.0 이전 버전: URL(localhost:8080/...) $\rarr$ 매핑(RequestMapping)<br>
>- 매핑 정보: `/hello-basic`
>- URL 요청 정보: `/hello-basic` `/hello-basic/`
> 
>스프링 부트 3.0 이후 버전: URL $\rarr$ 매핑
>- 매핑: `/hello-basic` URL 요청: `/hello-basic`
>- 매핑: `/hello-basic/` URL 요청: `/hello-basic/`
>
>기존에는 마지막에 있는 / (slash)를 제거했지만, 스프링 부트 3.0 부터는 마지막의 / (slash)를 유지한다.

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
>- 롬복 `@Data` 적용해 주기
`@Getter` , `@Setter` , `@ToString` , `@EqualsAndHashCode` , `@RequiredArgsConstructor` 를 자동으로 적용

`@ModelAttribute` 실행 방법
1. HelloData 객체를 생성
2. 요청 파라미터(ex. username)의 이름으로 HelloData 객체의 프로퍼티를 찾고 setter(ex. setUsername())를 호출해서 파라미터의 값을 입력(바인딩)
>**프로퍼티**<br>
getXxx -> xxx(프로퍼티)<br>
setXxx -> xxx(프로퍼티)<br>
**바인딩 오류**<br>
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
>- `String` , `int` , `Integer` 같은 단순 타입 = `@RequestParam`
>- 나머지 = `@ModelAttribute` (argument resolver 로 지정해둔 타입 외)

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
요청 파라미터를 조회하는 기능: `@RequestParam` , `@ModelAttribute`<br>

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

`@RequestBody` 요청<br>
JSON 요청 -> HTTP 메시지 컨버터 ->  객체

`@ResponseBody` 응답<br>
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
- Void를 반환<br>
`@Controller` 를 사용하고, `HttpServletResponse`, `OutputStream(Writer)` 같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 요청 URL을 참고해서 논리 뷰 이름으로 사용<br>
요청 URL: `/response/hello`<br>
실행: `templates/response/hello.html`<br>
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



<br>

>김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard
