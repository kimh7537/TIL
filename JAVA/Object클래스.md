# Object클래스

## ✏️ 개념
- 자바의 모든 클래스는 Object 클래스를 상속 받음
- 클래스가 아무런 상속을 받지 않으면 자동으로 `extend Object`를 삽입해 Object 클래스를 상속
```java
class A{

}

class A extends Object{

}
```
- 모든 클래스는 Object 타입으로 선언 가능
> Object a = new A(); <br>
Object b = new B();


---
## ✏️ `toString()`
### ✔️ 오버라이드 하기 전
- 객체 정보를 문자열로 리턴하는 메서드(`패키지명.클래스명@해시코드`)
- `println()` 메서드는 객체를 출력하는 자동으로 객체 내부의 `toString()` 메서드를 호출함
```java
A a = new A();
System.out.println(a);  //패키지.클래스명@해시코드
System.out.println(a.toString());  //둘 다 같은 의미
```

### ✔️ 오버라이드 하고난 후
- 객체의 정보를 String으로 바꾸어서 사용
```java
class Book{
	private String title;
	private String author;
	
	public Book(String title, String author) {
		this.title = title;
		this.author = author;
	}

	@Override
	public String toString() {
		return title + ", " + author;
	}
}

public class BookTest {
	public static void main(String[] args) {
		
		Book book = new Book("데미안", "헤르만 헤세");
		
		System.out.println(book);
		System.out.println(book.toString());

		String str = new String("test");  //String class는 이미 toString이 오버라이딩되어 있음
		System.out.println(str);
	}
}
```
결과
>데미안, 헤르만 헤세<br>
데미안, 헤르만 헤세<br>
test
- `String`이나 `Integer` 클래스는 이미 재정의 되어 있음

---
## ✏️ `equals()`
### ✔️ 오버라이드 하기 전
- 두 인스턴스의 주소 값을 비교하여 true/false를 반환
- `==` 연산자와 동일한 기능
```java
A a1 = new A("hello");
A a2 = new A("hello");
System.out.println(a1 == a2);       //false
System.out.println(a1.equals(a2));  //false
```

### ✔️ 오버라이드 하고난 후
- 재정의 하여 두 인스턴스가 논리적으로 동일함의 여부를 구현함
- 인스턴스가 다르더라도 논리적으로 동일한 경우 true를 반환하도록 재정의 할 수 있음
```java
public class Student{
	private int studentNum;
	private String studentName;
	
	public Student(int studentNum, String studentName) {
		this.studentNum = studentNum;
		this.studentName = studentName;
	}
	
	public void setStudentName(String name) {
		this.studentName = name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Student) {
			Student std = (Student)obj;
			if(this.studentNum == std.studentNum) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
}
```
```java
Student std1 = new Student(100, "Lee");
Student std2 = new Student(100, "Lee");
Student std3 = std1;
System.out.println(std1 == std2);   //두 개는 다른 객체라 false
System.out.println(std1 == std3);   //true
System.out.println(std1.equals(std2));   //논리적으로 같은 값이라 true, equals overriding해서 값으로 비교
```

---
## ✏️ `hashCode()`
### ✔️ 오버라이드 하기 전
- `hashCode()`는 인스턴스의 저장 주소를 반환함
- 힙메모리에 인스턴스가 저장되는 방식이 hash 방식
- 자료의 특정 값(키 값)에 대한 저장 위치를 반환해주는 해시 함수를 사용
- 두 인스턴스가 같으면 `equals()`(오버라이딩 안함)의 반환 값이 true
<br> $\rarr$ 동일한 `hashCode()` 값을 반환
### ✔️ 오버라이드 하고난 후
- 논리적으로 동일함을 위해 equals() 메서드를 재정의 하였다면 hashCode()메서드도 재정의 하여 동일한 hashCode 값이 반환되도록 한다

```java
@Override
	public int hashCode() {
		return studentNum;
	}
```
```java
System.out.println(std1.hashCode());  //equals재정의 되어있지 않은데 true면 hashcode는 동일한 값을 반환한
System.out.println(std2.hashCode());  //equals가 재정의 되어있는데 true면 hashcode도 재정의해서 동일한 값을 반환하게 해줌
System.out.println(System.identityHashCode(std1));
System.out.println(System.identityHashCode(std2));
```
- `System.identityHashCode()` : 해쉬코드 값을 반환해줌(오버라이딩한 후 필요한 경우에 사용)
---
## ✏️ `clone()`
- 객체의 원본을 복제하는데 사용하는 메서드
- 생성과정의 복잡한 과정을 반복하지 않고 복제 할 수 있음
- 해당 클래스의 clone() 메서드의 사용을 허용한다는 의미로 cloneable 인터페이스를 명시해 줌
```java
public class Student implements Cloneable{
    ...
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
```
```java
Student std1 = new Student(100, "Lee");
std1.setStudentName("Kim");
Student copyStudent = (Student)std1.clone();  //clone의 return 타입은 object
System.out.println(copyStudent.toString());   //clone은 object를 복제
//결과: 100, kim
```

    
