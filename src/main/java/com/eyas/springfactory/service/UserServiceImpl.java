package com.eyas.springfactory.service;

import com.eyas.springfactory.boot.spring.Autowired;
import com.eyas.springfactory.boot.spring.Component;
import com.eyas.springfactory.boot.spring.Scope;
import com.eyas.springfactory.boot.spring.ScopeEnum;

@Component("userService")
@Scope(ScopeEnum.singleton)
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
