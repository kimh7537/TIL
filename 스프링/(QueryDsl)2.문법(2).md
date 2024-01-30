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
### ✔️ `BooleanBuilder 사용`


---
```java
List<String> result = queryFactory
        .select(member.username).distinct()
        .from(member)
        .fetch();
```
- JPQL의 distinct와 같음