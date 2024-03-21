# Class클래스

## ✏️ Class 클래스
- 자바의 모든 클래스와 인터페이스는 컴파일 후 class 파일이 생성됨
- Class 클래스는 컴파일 된 class 파일을 로드하여 객체를 동적 로드하고, 정보를 가져오는 메서드가 제공됨

> **동적 로딩**  
>- 컴파일 시에 데이터 타입이 binding 되는 것이 아닌, 실행(runtime) 중에 데이터 타입을 binding 하는 방법
> - 컴파일 시에 타입이 정해지지 않으므로 동적 로딩시 오류가 발생하면 프로그램에 심각한 장애가 발생가능
```java
public static void main(String[] args) throws ClassNotFoundException {
		
		Class c = Class.forName("java.lang.String");
		
		Constructor[] cons = c.getConstructors();
		for(Constructor co : cons) {
			System.out.println(co);
		}
		System.out.println();
		Method[] m = c.getMethods();
		for(Method mth : m) {
			System.out.println(mth);
		}
		
```
- `Class.forName("클래스 이름")` 메서드로 클래스를 동적으로 로드

**다른 방법**
> `Class c = String.class;`

>`String s = new String();`<br>
`Class c = s.getClass();`  //Object 메서드


### ✔️ Class의 newInstance()메서드로 인스턴스 생성
- new 키워드를 사용하지 않고 클래스 정보를 활용하여 인스턴스를 생성
### ✔️ 클래스 정보 알아보기
- reflection 프로그래밍 : Class 클래스를 사용하여 클래스의 정보(생성자, 변수, 메서드)등을 알 수 있고, 인스턴스를 생성하고, 메서드를 호출하는 방식의 프로그래밍
- 로컬 메모리에 객체 없는 경우, 원격 프로그래밍, 객체의 타입을 알 수 없는 경우에 사용

```java
public static void main(String[] args) throws ... {
		
		Class c1 = Class.forName("ch04_Class_class.Person");
		
		Person person = (Person)c1.newInstance();
        //object 반환
		person.setName("Lee");
		System.out.println(person); //Lee
		
		
		
		Class c2 = person.getClass();  
        //object, 사용하려면 이미 인스턴스 생성되어 있어야함
		Person p = (Person)c2.newInstance();
		System.out.println(p); //null
		

		//1과 2는 같음
		//1
		Class[] parameterTypes = {String.class};
		Constructor cons = c2.getConstructor(parameterTypes);
		
		Object[] initargs = {"Kim"};
		Person kimPerson = (Person)cons.newInstance(initargs);
		System.out.println(kimPerson);  //Kim
		
		
		//2
		Person kim2 = new Person("Kim");

	}


```