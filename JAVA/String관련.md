# String관련

## ✏️ String
### ✔️ String 선언하기

>String str1 = new String("abc");<br>

>String str2 = "abc";
<br>String str2 = "abc";

1. 힙 메모리에 인스턴스로 생성하는 경우
2. 상수 풀에 있는 주소를 참조하는 방법

힙 메모리는 생성될때마다 다른 주소 값을 가지지만, 상수 풀의 문자열은 모두 같은 주소 값을 가짐

```java
public static void main(String[] args) {
		String str1 = new String("abc");
		String str2 = new String("abc");
		
		System.out.println(str1 == str2); //false(다른 주소 값)
		
		String str3 = "abc";
		String str4 = "abc";
		
		System.out.println(str3 == str4); //true(같은 값은 같은 주소를 가리킴)
}
```
### ✔️ String 특징
- 한번 생성된 String은 불변(immutable)
- String을 연결하면 기존의 String에 연결되는 것이 아닌 새로운 문자열이 생성됨 ( 메모리 낭비가 발생 가능성)
- 상수 풀 문자열도 불변의 특징을 가짐
```java
public static void main(String[] args) {
		String java = new String("java");
		String android = new String("android");
		System.out.println(System.identityHashCode(java)); //617901222

		java = java.concat(android); 

		System.out.println(java); //javaandroid
		System.out.println(System.identityHashCode(java)); //1159190947
	}
```
- `concat`사용하면 garbage가 많이 생김 -> `stringbuilder`같은 것 사용하기


---
## ✏️ StringBuilder, StringBuffer
### ✔️ 설명
- 내부적으로 가변적인 char[]를 멤버 변수로 가짐
- 새로운 인스턴스를 생성하지 않고 char[] 를 변경함
- `StringBuffer`는 멀티 쓰레드 프로그래밍에서 동기화(synchronization)를 보장
- 단일 쓰레드 프로그램에서는 `StringBuilder` 사용을 권장


### ✔️ 예시
```java
public static void main(String[] args) {
		String java = new String("java");
		String android = new String("android");
		
		StringBuilder buffer = new StringBuilder(java);
		System.out.println(System.identityHashCode(buffer)); //617901222

		buffer.append(android);
		System.out.println(System.identityHashCode(buffer)); //617901222

		String test = buffer.toString();//toString() 메서드로 String반환
		System.out.println(test); //javaandroid
}
```
- `append()`
- `StringBuilder()`

---
## ✏️ text block 사용(java 13 이상)
문자열을 """ """ 사이에 이어서 만들 수 있음
```java
public static void main(String[] args) {
		
		String textBlocks = """
				Hello,
				hi,
				how r u""";
		System.out.println(textBlocks);
		System.out.println(getBlockOfHtml());
	}
	
	public static String getBlockOfHtml() {
	    return """
	            <html>

	                <body>
	                    <span>example text</span>
	                </body>
	            </html>""";
	
    }
```
