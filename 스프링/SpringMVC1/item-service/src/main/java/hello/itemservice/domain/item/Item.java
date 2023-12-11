package hello.itemservice.domain.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Getter @Setter  이거 쓰는게 안전함
@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;  //int는 null이 안돼서 Integer 사용

    public Item(){

    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
