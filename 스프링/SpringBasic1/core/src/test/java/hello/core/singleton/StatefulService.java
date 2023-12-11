package hello.core.singleton;

public class StatefulService {

//    private int price; // 상태를 유지하는 필드


//    싱글톤 컨테이너 문제점 해결 void -> int
    public int order(String name, int price){
        System.out.println("name = " + name + " price = " + price);
//        this.price = price;     //여기가 문제
        return price;
    }

//    public int getPrice() {
//        return price;
//    }
}
