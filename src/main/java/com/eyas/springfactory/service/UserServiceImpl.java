package com.eyas.springfactory.service;

import com.eyas.springfactory.boot.spring.*;

@Component("userService")
@Scope(ScopeEnum.singleton)
@Transactional
public class UserServiceImpl {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private VipServiceImpl vipService;


    public void test(String orderNo){
        System.out.println("test---->");
        this.orderService.createOrder(orderNo);
        this.vipService.vip("vip-->" + orderNo);
    }
}
