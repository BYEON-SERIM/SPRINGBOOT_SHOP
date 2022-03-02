package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order 연관 관계
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;



    /**
     *
     * 주문 조회 V1
     * 엔티티를 DTO로 변환(fetch join 사용 안함)
     * N+1 문제   Laze Loading으로 인한 데이터베이스 쿼리가 너무 많이 호출된다는 문제가 있다.
     *
     */
    @GetMapping("/api/v1/simple-orders")
    public Result ordersv1(){
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return new Result(result);

    }

    /**
     *
     * 주문 조회 V2
     * fetch join 사용
     *
     */
    @GetMapping("/api/v2/simple-orders")
    public Result ordersv2(){
        List<Order>  orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return new Result(result);

    }

    /**
     *
     * 주문 조회 V3
     * JPA에서 바로 DTO로 조회
     *
     */
    @GetMapping("/api/v3/simple-orders")
    public Result ordersv3() {
        List<OrderSimpleQueryDto> result = orderSimpleQueryRepository.findOrderDtos();
        return new Result(result);
    }



    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;   //고객 주소가 아니라 배송지 주소

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name =  order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address =order.getDelivery().getAddress();
        };
    }

}
