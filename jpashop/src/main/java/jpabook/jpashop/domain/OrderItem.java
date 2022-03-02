package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
//createOrderItem 외의 방법으로 주문을 추가하는 일을 막기 위한 어노테이션. 직접 생성을 막아줌.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="order_id")
    private Order order;

    private int orderPrice;  //주문 가격
    private int  count;  //주문수량



    //== 생성 메서드 ==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);

        return orderItem;
    }
    //orderPrice를 또 만들어준 이유는 할인, 쿠폰 사용의 경우 결제 금액이 달라지기 때문이다.

    //== 비즈니스 로직 ==//
    //재고 수량을 되돌려줌
    public void cancel() {
        getItem().addStock(count);
    }

    //== 조회 로직 ==//
    /**
     * 주문상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
