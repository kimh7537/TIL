#SQL(1)

---
## ✏️ 테이블 생성, 수정
### ✔️ Domain Type
- `char(n)`: 길이가 n인 string
- `varchar(n)`: 최대 길이 n인 string
- `int`
- `smallint`
- `numeric(p,d)`: 총 p자리 소수점 이하 d자리
ex. numeric(3,1) -> 44.5
- `real, double precision`: Floating point and double-precision floating point numbers
- `float(n)`: Floating point number, with user-specified precision of at least n digits
---
### ✔️ Create Table
```sql
create table instructor (
    ID char(5),
    name varchar(20),
    dept_name varchar(20),
    salary numeric(8,2)
) ;
```
**Types of integrity constraints**

• `primary key (A1, ..., An )`<br>
• `foreign key (Am, ..., An ) references r` <br>
    -> (Am..An): r의 primary key<br>
• `not null`

```sql
create table takes (
    ID varchar(5),
    course_id varchar(8),
    sec_id varchar(8),
    semester varchar(6),
    year numeric(4,0),
    grade varchar(2),
    primary key (ID, course_id, sec_id, semester, year) ,
    foreign key (ID) references student,
    foreign key (course_id, sec_id, semester, year) references section
) ;
```
- `not null`이 없으면 `null`가능
- `primary key`는 자동으로 `not null`/테이블 당 1개
- 일반적으로 `foreign key`는 `null`이 될 수 없음 -> 참조하는 테이블에서 속성이 `primary key`이기 때문
- 이름이 같지 않으면 새로운 구문 추가 가능
-> ...department(dept_name)
---
### ✔️ 테이블 업데이트 구문들
#### ✨ DML

`Insert`<br>
• `insert into instructor values ('10211', 'Smith', 'Biology', 66000);`

`Delete`<br>
§ `delete from student;`: 테이블은 남기지만 모든 tuple제거

---
#### ✨ DDL
`Drop Table`<br>
• `drop table r;` : 테이블과 tuple모두 제거

`Alter Table`<br>
• `alter table r add A D;`
- A속성 추가하고 도메인 타입(D) 선언
- 새로운 속성은 `null`로 추가됨

• `alter table r drop A;`
- column A를 제거
- 지우는건 신중해야함(copy해서 추가하는 방식이 더 좋음)

---
---
## ✏️ 기본쿼리
```sql
select A1, A2, ..., An
from r1, r2, ..., rm
where P ;
```
### ✔️ `select`
```sql
select distinct dept_name
from instructor
// 중복이 없는형태

select all dept_name
from instructor ;
//중복 가능형태(default가 all)

select *
from instructor ;

select ID, name, salary/12 as monthly_salary
from instructor;
```

### ✔️ `where`
- `and, or, and not`
- `<, <=, >, >=, =, <>(!=)`
- `between`( a <= where <= b)
- `row constructors` <br>
  : and를 많이 쓰는 상황 해소(a=100 and b>= 200 and ...)
```sql
select name
from instructor
where dept_name =＇Comp. Sci.' and salary > 70000 ;


select name
from instructor
where salary between 90000 and 100000 ;


select name, course_id
from instructor, teaches
where (instructor.ID, dept_name) = (teaches.ID,'Biology');
```

### ✔️ `from`
- Cartesian product `instructor X teaches`
```sql
select *
from instructor, teaches ;
```

For common attributes (e.g., ID), the attributes in the resulting table
are renamed using the relation name (e.g., instructor.ID)


### ✔️ `rename operation`
Find the names of all instructors who have a **higher salary** than some instructor in 'Comp. Sci.'.

```sql
select distinct T.name
from instructor as T, instructor as S
where T.salary > S.salary and S.dept_name = 'Comp. Sci.’ ;
```

### ✔️ `string operations`
• `percent ( % )` : substring 매칭<br>
• `underscore ( _ )` 
```sql
select name
from instructor
where name like '%dar%’ ;
```

• Match the string “100%”<br>
`like '100\%' escape '\'`<br>
• `'Intro%'` Intro로 시작하는 string 매칭<br>
• `'%Comp%'` Comp를 부분문자열로 가진것 매칭<br>
• `'_ _ _'`  글자가 3개인것 매칭<br>
• `'_ _ _ %'` 적어도 3개의 문자로된 문자열 매칭<br>


### ✔️ `order by`
```sql
select distinct name
from instructor
order by name desc;
```
- `desc`: 내림차순
- `asc` : 오름차순
- default는 오름차순

### ✔️ `set operations`
- Find courses that ran in Fall 2017 `or` in Spring 2018
```sql
(select course_id from section where sem = 'Fall' and year = 2017)
union
(select course_id from section where sem = 'Spring' and year = 2018) ;
```

- Find courses that ran in Fall 2017 `and` in Spring 2018
```sql
(select course_id from section where sem = 'Fall' and year = 2017)
intersect
(select course_id from section where sem = 'Spring' and year = 2018) ;
```

-  Find courses that ran in Fall 2017 `but not in` Spring 2018
```sql
(select course_id from section where sem = 'Fall' and year = 2017)
except
(select course_id from section where sem = 'Spring' and year = 2018) ;
```
- sql은 multiset허용(중복 가능)
- 그러나 집합 연산자를 사용하면 집합이 됨({a,a,a} U {b} -> {a,b})
- 집합 연산자는 연산자 앞 뒤의 두 집합이 양립가능해야함(속성이 같음..)

> * sql: default(중복 가능) <-> select distinct(중복 불가능)<br>
> * 집합연산자: default(중복 불가능) <-> union all..(중복 가능)

- 집합연산자는 기본적으로 중복을 허용안함
- 중복을 사용하고 싶으면 다음 사용하기

• `union all`<br>
• `intersect all`<br>
• `except all`<br>


### ✔️ `Null values`
- `null`은 `unknown` or `없는 값`

1. `산술 연산식`(+,-,*,/)은 입력값이 NULL이면 NULL<br>
• Example: `5 + null returns null`

<br>

2. sql은 NULL값을 포함한 `비교연산`-> `unknown`(is Null/is not Null아님)<br>
• Example: `5 < null or null <> null or null = null -> unknown`

<br>

3. `Boolean operations(and, or, not)`
- `where`에서 사용가능
- 결과가 `unknown`라면 `false`로 처리함 

•  `and` : <br>
`(true and unknown) = unknown`<br>
`(false and unknown) = false`<br>
`(unknown and unknown) = unknown`<br>
• `or` : <br>
`(unknown or true) = true`<br>
`(unknown or false) = unknown`<br>
`(unknown or unknown) = unknown `<br>
• `not unknown = unknown`<br>

<br>

4. `is null`, `is not null`
- `null`인지 체크할때 사용
```sql
select name
from instructor
where salary is null ;
```
- `a is null -> true or false`
- `a <= null -> unknown`


---
