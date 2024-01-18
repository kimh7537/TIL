# (MVC)3.로깅


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
반환 값으로 뷰를 찾지 않음. HTTP메시지 바디에 바로 입력.(JSON)<br>
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

> **올바른 로그 사용법**<br>
`log.debug("String concat log=" + name)`<br>
로그 출력 레벨을 info로 설정해도 해당 코드에 있는 `""+name`가 실행됨. 자바 언어는 문자를 먼저 더하고 메서드를 호출함<br>
`log.debug(debug log={}", name)`<br>
로그 출력 레벨을 info로 설정하면 아무일도 발생하지 않음.<br>
연산 자체가 발생하지 않음.

- 출력 모양을 조정 가능
- 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있음
- 성능도 일반 System.out보다 좋음 (내부 버퍼링, 멀티 쓰레드 등등)


---

<br>

>김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard