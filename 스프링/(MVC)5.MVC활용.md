# (MVC)5.MVC활용

## ✏️ 간단한 웹페이지 구조
![](https://velog.velcdn.com/images/w009981/post/bb8a839c-72ff-43fd-8fe5-579cd5ed26e1/image.png)


### ✔️ MVC 활용
```java
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

//상품목록 보여주기
    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

//상품 상세 정보 보여주기
    @GetMapping("/{itemId}")   //{itemId}의 itemId는 변경가능
    public String item(@PathVariable long itemId, Model model){
        Item item = itemRepository.findById(itemId) ;
        model.addAttribute("item", item);
        return "basic/item";
    }



// 상품 등록 폼
    @GetMapping("/add")
    public String addForm() {
        return "/basic/addForm";
    }
// 상품 저장 후 상품 상세 보여주기
//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model) {

        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);

        return "/basic/item";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);
//        model.addAttribute("item", item); 자동 추가 생략 가능

        return "/basic/item";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
//        Item -> item;
//        model.addAttribute("item", item);

        return "/basic/item";
    }

//    @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);

        return "/basic/item";
    }

//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);

        return "redirect:/basic/items/" + item.getId();
    }

    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/items/{itemId}";
    }


// 상품 수정 폼
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item );

        return "basic/editForm";
    }
// 상품 수정 후 상품 상세 보여주기
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item){
        itemRepository.update(itemId, item);

        return "redirect:/basic/items/{itemId}";
    }


// 테스트 데이터
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}

```
#### ✨ **`@RequiredArgsConstructor`**
- `final` 이 붙은 멤버변수만 사용해서 생성자를 자동으로 만들어줌
```java
public BasicItemController(ItemRepository itemRepository) {
	this.itemRepository = itemRepository;
}
```
- 생성자가 1개이므로 해당 생성자에 `@Autowired` 로 의존관계를 주입해줌

<br>

#### ✨ **`@ModelAttribute`**
**기능 1. 요청 파라미터 처리**
- `Item` 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법`(setXxx)`으로 입력

**기능 2. Model 추가**
- 모델(Model)에 `@ModelAttribute`로 지정한 객체를 자동으로 넣어줌
- `@ModelAttribute` 에 지정한 `name(value)`속성을 모델 이름으로 지정

>`@ModelAttribute("hello") Item item` 이름을 hello 로 지정
>`model.addAttribute("hello", item);` 모델에 hello 이름으로 저장

- `@ModelAttribute` 의 이름을 생략하면 모델에 저장될 때 클래스명을 소문자로 변경해서 등록<br>
ex.) `@ModelAttribute` 클래스명 -> 모델에 자동 추가되는 이름
`Item` -> `item`
`HelloWorld` ->  `helloWorld`


### ✔️ PRG
![](https://velog.velcdn.com/images/w009981/post/6769eef6-85e6-4a01-8197-a93578826b46/image.png)
![](https://velog.velcdn.com/images/w009981/post/4c538c4f-79a3-4f2c-8a44-5f99ba6e04bd/image.png)


**브라우저의 새로 고침은 마지막에 서버에 전송한 데이터를 다시 전송**
- 상품 등록 폼에서 데이터를 입력하고 저장을 선택하면 POST /add + 상품 데이터를 서버로 전송
- 이 상태에서 새로 고침을 또 선택하면 마지막에 전송한 POST /add + 상품 데이터를 서버로 다시 전송

**`redirect:/...`**
- `"redirect:/basic/items/" + item.getId()` 
URL에 변수를 더해서 사용하는 것은 URL 인코딩이 안되기 때문에 위험함
- `redirect:/basic/items/{itemId}`
컨트롤러에 매핑된 `@PathVariable` 의 값은 redirect 에도 사용 할 수 있음

**`RedirectAttributes`**
- URL 인코딩도 해주고, pathVarible, 쿼리 파라미터까지 처리

```java
redirectAttributes.addAttribute("itemId", savedItem.getId());
redirectAttributes.addAttribute("status", true);
...
return "redirect:/basic/items/{itemId}";
```
- `pathVariable` 바인딩: `{itemId}`
- 나머지는 쿼리 파라미터로 처리: `?status=true`
- `http://localhost:8080/basic/items/3?status=true`






---
## ✏️ 타임리프 간단한 문법

- 정적 리소스(`resources/static`에 있는 html파일)은 직접 열어도 동작함<br>
ex. `http://localhost:8080/html/items.html`

### ✔️ 타임리프 간단히 알아보기
#### ✨ 타임리프 사용 선언
`<html xmlns:th="http://www.thymeleaf.org">`
<br>

#### ✨ 속성 변경
**`th:href`**
`th:href="@{/css/bootstrap.min.css}"`
- `href="value1"` 을 `th:href="value2"` 의 값으로 변경
- 만약 값이 없다면 `th:xxx`를 사용해 새로 생성
- `th:xxx` 가 붙은 부분은 서버사이드에서 렌더링 되고, 기존 것을 대체함
- `th:xxx` 이 없으면 기존 html의 `xxx` 속성이 그대로 사용됨
- HTML을 파일로 직접 열었을 때, `th:xxx` 가 있어도 웹 브라우저는 `th:` 속성을 알지 못하므로 무시, 따라서 HTML 파일 보기를 유지하면서 템플릿 기능도 가능

<br>

**`th:onclick`**
- 상품 등록 폼으로 이동

`onclick="location.href='addForm.html'"`<br>
`th:onclick="|location.href='@{/basic/items/add}'|"`

<br>

**`th:value`**
`th:value="${item.id}"`
- 모델에 있는 item 정보를 획득하고 프로퍼티 접근법으로 출력
(`item.getId()`)
- value 속성을 th:value 속성으로 변경

<br>

**`th:action`**
- HTML form에서 action에 값이 없으면 현재 URL에 데이터를 전송<br>
ex. `<form action="item.html" th:action method="post">`
값이 없으므로 현재 URL을 다시 사용

상품 등록 폼: GET /basic/items/add<br>
상품 등록 처리: POST /basic/items/add


<br>

#### ✨ URL 링크 표현식
**`@{...}`**
1. `th:href="@{/css/bootstrap.min.css}"`
2. `th:href="@{/basic/items/{itemId}(itemId=${item.id})}"`
- 경로 변수(`{itemId}`) 뿐만 아니라 쿼리 파라미터도 생성가능<br>
ex.) 
`th:href="@{/basic/items/{itemId}(itemId=${item.id}, query='test')}"`<br>
생성 링크: `http://localhost:8080/basic/items/1?query=test`

3  `th:href="@{|/basic/items/${item.id}|}"`
   - 리터럴 문법사용

<br>


#### ✨ 리터럴 대체 - |...|
`<span th:text="'Welcome to our application, ' + ${user.name} + '!'">`<br>
`<span th:text="|Welcome to our application, ${user.name}!|">`


**location.href='/basic/items/add'**<br>
`th:onclick="'location.href=' + '\'' + @{/basic/items/add} + '\''"`<br>
`th:onclick="|location.href='@{/basic/items/add}'|"`
  

<br>

#### ✨ 반복 출력 - th:each
  
`<tr th:each="item : ${items}">`
- 모델에 포함된 `items` 컬렉션 데이터가 `item` 변수에 하나씩
포함되고, 반복문 안에서 item 변수 사용가능
  
<br>

  
#### ✨ 변수 표현식 - ${...}
`<td th:text="${item.price}">10000</td>`
- 모델에 포함된 값이나, 타임리프 변수로 선언한 값을 조회
- 프로퍼티 접근법을 사용 ( `item.getPrice()` )
- 10000을 `${item.price}` 의 값으로 변경

<br>

#### ✨ 기타
`th:if` : 해당 조건이 참이면 실행<br>
`${param.status}` : 타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능
  
  
> - 타임리프는 순수 HTML 파일을 웹 브라우저에서 열어도 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를 확인할 수 있음
>- 순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 **네츄럴 템플릿(natural templates)**이라 부름
  
  
  



  

---
> 김영한 인프런 강의 '스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술' 참조
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard
