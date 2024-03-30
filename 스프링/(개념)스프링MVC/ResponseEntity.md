# ResponseEntity

---
---
# ✏️ ResponseEntity vs DTO

- HTTP body에 메시지를 담는 방법은 2가지
    - ResponseEntity
        - view 조회 불가능(응답시 메시지 바디 부분에 정보 반환)
        - 따라서 @RestController 필요없음
    - @RestController + DTO
        - @ResponseStatus 를 통해 HTTP 상태 코드 반환 가능
        - *@ResponseStatus(value = HttpsStatus.OK) -> 200인 경우 생략 가능*

---

# ✏️ ResponseEntity의 응답 방법

- 상태 코드와 응답 헤더 및 응답 본문을 클라이언트에 전달할 수 있음

**1번 유형**

- 상태코드 OK와 body를 한번에 사용하는 방법
- body가 없을 때, `build()` 를 사용

```java
@GetMapping("/")
public ResponseEntity<Hello> test(){
	Hello hello = helloService.getHello();
	return ResponseEntity.ok(hello)
}
```

```java
@PostMapping
public ResponseEntity<Void> save(final @RequestBody BookmarkRequest bookmarkRequest) {
    bookmarkService.save(bookmarkRequest);
    return ResponseEntity.created().build();
}
```

**2번 유형**

- status와 body를 이용하는 방법
- body가 없을 때, `build()` 를 사용
- `NO_CONTENT` : 204 , `NOT_FOUND()` : 404 , `BAD_REQUEST` : 400 , etc…

```java
@GetMapping("/")
public ResponseEntity<Hello> test(){
	Hello hello = helloService.getHello();
	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
}
```

```java
@GetMapping("/properties")
public ResponseEntity<FindPropertyResponse> findProperty(@RequestParam(name = "zip-code") String zipCode){
    return ResponseEntity.status(HttpStatus.OK).body(propertyService.findProperties(zipCode));
}
```

**3번 유형**

```java
@GetMapping("/")
public ResponseEntity<Hello> test(){
	Hello hello = helloService.getHello();
	return new ResponseEntity<>(hello, (header), HttpStatus.valueOf(200));
}
```

```java
@GetMapping("/")
public ResponseEntity<Hello> test(){
	Hello hello = helloService.getHello();
	return new ResponseEntity<>(hello, HttpStatus.OK);
}
```

## ✔️ 1. Return에서 생성자보다 빌더 패턴을 사용하기

**생성자 패턴**

```java
return new ResponseEntity(body, headers, HttpStatus.valueOf(200));
```

**빌더 패턴**

```java
return ResponseEntity.ok
	.headers(headers)
	.body(body);
```

## ✔️ 2. ResponseEntity의 Body 타입을 명시하기

```java
public ResponseEntity getUser(){
...
```

- 메소드의 return에서 ResponseEntity 바디 타입을 명시하지 않으면 Object클래스로 Return을 받음

```java
public ResponseEntity<Object> getUsers() {
    List<User> users = userService.getUsers();
    return ResponseEntity.ok(users);
}
ResponseEntity<Objects> response = restTemplate.getForEntity("/users", Objects.class);
List<User> users = (List<User>) response.getBody();
```

- 타입이 여러 종류가 있다면 **와일드 카드<?>**를 사용하기
- 와일드카드나 Object는 return한 후 불필요한 형변환이 발생할 수 있음

```java
@GetMapping("/")
public ResponseEntity<T> getUser() {
    T user = userService.getUser();
    return ResponseEntity.ok(user);
}
```

- 따라서 **<T> 타입 파라미터**를 사용하기(컴파일 시에 자동 형변환을 해줌)