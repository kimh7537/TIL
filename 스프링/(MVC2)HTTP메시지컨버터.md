# (MVC2)HTTP메시지컨버터

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

<br>

>김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard