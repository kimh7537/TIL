# (MVC)1.MVC구조이해

## ✏️ 스프링 MVC 구조
![](https://velog.velcdn.com/images/w009981/post/2da8b78c-b913-4dd4-a14e-c1d94046841f/image.png)
- 직접 만든 MVC프레임워크와 진짜 스프링 MVC 비교

`FrontController` - `DispatcherServlet`

`handlerMappingMap` - `HandlerMapping`

`MyHandlerAdapter` - `HandlerAdapter`

`ModelView` - `ModelAndView`

`viewResolver` - `ViewResolver`(인터페이스)

`MyView` - `View`(인터페이스)


### ✔️ DispatcherServlet 구조
- 서블릿으로 동작함
- 상속받아서 동작함

`DispatcherServlet` -> `FrameworkServlet` -> `HttpServletBean` -> `HttpServlet`
- 모든 경로("/")에 대해서 매핑함(더 자세한 경로가 우선순위 높음)

**동작 흐름**
1. `DispatcherServlet.doDispatch()`가 호출됨
2. 핸들러 조회(요청 URL에 매핑된 핸들러를 조회함)
3. 핸들러 어댑터 조회(핸들러를 실행할 수 있는 어댑터 조회)
4. 핸들러 어댑터 실행/핸들러 실행/ModelAndView반환
6. 뷰 리졸버를 통해 뷰 찾기
7. View 반환(뷰의 논리 이름을 물리 이름으로 바꾸고, 뷰 객체 반환)
8. 뷰 렌더링

---
### ✔️ 핸들러 매핑과 핸들러 어댑터
**핸들러 매핑, 핸들러 어댑터 조회 순서**
(모든 핸들러와 핸들러 매핑은 아님)
> **HandlerMapping**<br>
0 = `RequestMappingHandlerMapping` : 애노테이션 기반의 컨트롤러인 `@RequestMapping`에서 사용<br>
>1 = `BeanNameUrlHandlerMapping` : 스프링 빈의 이름으로 핸들러 찾기

> **HandlerAdapter**<br>
0 = `RequestMappingHandlerAdapter` : 애노테이션 기반의 컨트롤러인 `@RequestMapping`에서 사용<br>
1 = `HttpRequestHandlerAdapter` : `HttpRequestHandler` 처리<br>
2 = `SimpleControllerHandlerAdapter` : `Controller` 인터페이스(애노테이션X, 과거에 사용) 처리

#### ✨ Controller 인터페이스(과거)
```java
@Component("/springmvc/old-controller")  //스프링 빈의 이름을 url로
public class OldController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("new-form");
    }
}
```
1. 핸들러 매핑으로 핸들러 조회
- 빈 이름으로 핸들러를 찾아야 하므로 `BeanNameUrlHandlerMapping`이 실행 성공하고 `OldController` 핸들러를 반환
2. 핸들러 어댑터 조회
- `SimpleControllerHandlerAdapter` 어댑터 반환

#### ✨ HttpRequestHandler (과거)
```java
@Component("/springmvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
        System.out.println("MyHttpRequestHandler.handleRequest");
    }
}
```
1. 핸들러 매핑으로 핸들러 조회
- 빈 이름으로 핸들러를 찾아야 하므로 `BeanNameUrlHandlerMapping`이 실행 성공하고 `MyHttpRequestHandler` 핸들러를 반환
2. 핸들러 어댑터 조회
- `HttpRequestHandlerAdapter` 어댑터 반환

---
### ✔️ 뷰 리졸버
**뷰 리졸버 조회 순서**
(모든 뷰 리졸버는 아님)
> 1 = `BeanNameViewResolver` : 빈 이름으로 뷰를 찾아서 반환<br>
2 = `InternalResourceViewResolver` : JSP를 처리할 수 있는 뷰를 반환한다.

**OldController**
```java
@Component("/springmvc/old-controller")  //스프링 빈의 이름을 url로
public class OldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("new-form");
    }
}
```
**application.properties**
```
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```
1. 핸들러 어댑터에서 `new-form` 이라는 논리 뷰 획득
2. `ViewResolver` 호출
   - `BeanNameViewResolver` 는 new-form 이라는 이름의 스프링 빈으로 등록된 뷰를 찾아야 하지만 없음
3. 뷰 리졸버가 `InternalResourceViewResolver` 반환
4. 뷰 - `InternalResourceView`
   - JSP처럼 포워드 forward() 를 호출해서 처리할 수 있는 경우에 사용
5. `view.render()`
   - `InternalResourceView`가 `forward()` 를 사용해 JSP 실행
---
## ✏️ 스프링 MVC 실전

### ✔️ MVC1
```java
@Controller
//@Component
//@RequestMapping
public class MemberForm {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process(){
        return new ModelAndView("new-form");
    }
}
```
- `@Controller` 스프링이 자동으로 스프링 빈으로 등록됨
(내부에 `@Component`가 있어 스캔됨)
- `@RequestMapping`: 요청 정보를 매핑함(URL이 호출되면 실행됨)
(메서드의 이름(`process`)는 임의로 지어도 상관없음)
- **`RequestMappingHandlerMapping` 은 스프링 빈 중에서 `@RequestMapping` 혹은 `@Controller`가 클래스 레벨에 있는 경우 매핑 정보로 인식함**
```
@Component //스프링 빈으로 등록
@RequestMapping
```
- 이렇게 사용해도 `@Controller`와 같은 기능을 발휘함

> - **스프링 부트 3.0 이상부터는 클래스 레벨에 `@RequestMapping`이 있어도 스프링 컨트롤러로 인식하지 않음**
>- **`RequestMappingHandlerMapping` 은 `@Controller`만 인식하게 바뀜**

```java

@Controller
public class MemberSave {
...
    @RequestMapping("/springmvc/v1/members/save")
    public ModelAndView process(HttpServletRequest request, HttpServletResponse response) {

        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mv = new ModelAndView("save-result");
        mv.addObject("member", member);

        return mv;
    }

}
```
- `mv.addObject()` : `ModelAndView`에 데이터 추가할때 사용

```java
@Controller
public class MemberList {
...
    @RequestMapping("/springmvc/v1/members")
    public ModelAndView process() {

        List<Member> members = memberRepository.findAll();

        ModelAndView mv = new ModelAndView("members");
        mv.addObject("members", members);

        return mv;
    }
}
```


---
### ✔️ MVC2
```java

@Controller
@RequestMapping("/springmvc/v2/members")
public class SpringMemberControllerV2 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @RequestMapping("/new-form")
    public ModelAndView newForm() {
        return new ModelAndView("new-form");
    }

    @RequestMapping("/save")
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {

        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelAndView mav = new ModelAndView("save-result");
        mav.addObject("member", member);

        return mav;
    }

    @RequestMapping
    public ModelAndView members() {

        List<Member> members = memberRepository.findAll();

        ModelAndView mav = new ModelAndView("members");
        mav.addObject("members", members);

        return mav;
    }
}
```
- 클래스 레벨에 `@RequestMapping`이 있으면 메서드 레벨의 주소와 조합됨

---
### ✔️ MVC3
```java

@Controller
@RequestMapping("/springmvc/v3/members")
public class SpringMemberControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @GetMapping("/new-form")
    public String newForm() {
        return "new-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam("username") String username, 
    @RequestParam("age") int age, Model model) {

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);

        return "save-result";
    }

    @GetMapping
    public String members(Model model) {

        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);

        return "members";
    }
}

```
- Model을 파라미터로 받음
- ViewName을 직접 반환함
- HTTP 메서드를 구분할 수 있음
---
> 김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard