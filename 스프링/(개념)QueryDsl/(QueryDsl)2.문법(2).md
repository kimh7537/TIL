#(QueryDsl)1.문법(2)

---
---
## ✏️ `프로젝션 결과반환`
### ✔️ `기본`

```java
 @Test
public void simpleProjection(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
}

@Test
public void tupleProjection(){
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username=" + username);
            System.out.println("age=" + age);
        }
}
```
- 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
- 프로젝션 대상이 둘 이상이면 `튜플`이나 `DTO`로 조회

---
### ✔️ `DTO`
```java
@Data
public class MemberDto {
        private String username;
        private int age;

        public MemberDto() {
        }

        public MemberDto(String username, int age) {
                this.username = username;
                this.age = age;
        }
}
```
- 파라미터가 있는 생성자가 있으면, 기존 생성자는 자동으로 생성안됨
- 필요하면 수동으로 만들어주기

#### ✨ `JPQL`
```java
@Test
public void findDtoByJPQL(){
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
}
```
- new명령어 사용하고 package이름 다 적어야함
- 생성자 방식만 지원


#### ✨ `프로퍼티 접근 - Setter`
```java
@Test
public void findDtoBySetter(){
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class, //setter를 통해
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
}
```
- 기본 생성자가 없으면 오류 발생

#### ✨ `필드 직접 접근`
```java
@Test
public void findDtoByField(){
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
}
```


#### ✨ `별칭이 다를 때`
```java
@Data
public class UserDto {

    private String name;
    private int age;

    public UserDto() {
    }

    public UserDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```
```java
@Test
public void findUserDtoByField(){

        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub),"age")
                ))
                .from(member)
                .fetch();
}
```
- 프로퍼티나, 필드 접근 생성 방식에서 DTO의 필드와 이름이 다를때 사용
- `ExpressionUtils.as(source,alias)` : 필드나, 서브 쿼리에 별칭 적용
- `username.as("memberName")` : 필드에 별칭 적용
- `UserDto`의 필드 `name`과 Member엔티티의 `username` 매칭 

#### ✨ `생성자 사용`
```java
@Test
public void findDtoByConstructor(){
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
}
```

```java
@Test
public void findUserDtoByConstructor(){
        List<UserDto> result = queryFactory
                .select(Projections.constructor(UserDto.class, //생성자는 타입으로 확인함(이름 아님)
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
}
```
- 생성자는 타입으로 매칭을 하므로, `UserDto`의 필드 `name`과 Member엔티티의 `username` 매칭시킴

---
### ✔️ `프로젝션과 결과 반환 - @QueryProjection`
```java
@Data
public class MemberDto {
        private String username;
        private int age;
        
        public MemberDto() {
        }

        @QueryProjection
        public MemberDto(String username, int age) {
                this.username = username;
                this.age = age;
        }
}
```
```java
@Test
public void findDtoByQueryProjection(){
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age)) 
                //생성자랑 비슷해보이지만 생성자는 런타임오류, 이건 컴파일 오류
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
}
```
- 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법
- 다만 DTO에 QueryDSL 어노테이션을 유지해야 하는 점과 DTO까지 Q 파일을 생성해야 하는 단점 존재

---
---
## ✏️ `동적쿼리`
### ✔️ `BooleanBuilder 사용`

```java
@Test
public void dynamicQuery_BooleanBuilder(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
}

private List<Member> searchMember1(String usernameCond, Integer ageCond){

        BooleanBuilder builder = new BooleanBuilder();
        if(usernameCond != null){
            builder.and(member.username.eq(usernameCond));
        }

        if(ageCond != null){
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
}
```
- `and`, `or`등 사용 가능

---
### ✔️ `Where 다중 파라미터 사용`
```java
@Test
public void dynamicQuery_WhereParam(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
}

private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
//                .where(usernameEq(usernameCond), ageEq(ageCond)) //null은 무시됨, 아무역할 하지 않음
                .where(allEq(usernameCond, ageCond))
                .fetch();
}

private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond == null ? null : member.username.eq(usernameCond);
}

private BooleanExpression ageEq(Integer ageCond){
        return ageCond != null ? member.age.eq(ageCond) : null;
}

private BooleanExpression allEq(String usernameCond, Integer ageCond){
        return usernameEq(usernameCond).and(ageEq(ageCond));
}
```
- `where` 조건에 `null` 값은 무시됨
- 메서드를 다른 쿼리에서도 재활용 할 수 있음
- 쿼리 자체의 가독성이 높아짐
- `각 부분을 조합하는 것이 가능함 -> 매우 큰 장점`

---
---
## ✏️ `수정 삭제 벌크 연산`
```java
@Test
@Commit
public void bulkUpdate(){
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        em.flush();
        em.clear();

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
}

/*
* 연산하기(add, multiply)
*/
@Test
public void bulkAdd(){
        long count = queryFactory
                .update(member)
//                .set(member.age, member.age.add(1))
                .set(member.age, member.age.multiply(2))
                .execute();
}

/*
* 쿼리 한번으로 대량 데이터 삭제
*/
@Test
public void bulkDelete(){
        queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
}
```
- 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에 배치 쿼리를 실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전

---
---
## ✏️ `SQL function 호출하기`

```java
//member M으로 변경하는 replace 함수 사용
@Test
public void sqlFunction(){
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace', {0}, {1}, {2})",
                        member.username, "member", "M"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
}

//소문자로 변경해서 비교
@Test
public void sqlFunction2(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
//                .where(member.username.eq(
//                        Expressions.stringTemplate("function('lower', {0})", member.username)))
                .where(member.username.eq(member.username.lower()))
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
}
```
- SQL function은 JPA와 같이 Dialect에 등록된 내용만 호출할 수 있음
- lower 같은 ansi 표준 함수들은 querydsl이 상당부분 내장하고 있음

---
**distinct**
```java
List<String> result = queryFactory
        .select(member.username).distinct()
        .from(member)
        .fetch();
```
- JPQL의 distinct와 같음


