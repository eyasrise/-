package com.eyas.springfactory.service;

import com.eyas.springfactory.boot.spring.Component;
import com.eyas.springfactory.boot.spring.Scope;
import com.eyas.springfactory.boot.spring.ScopeEnum;

@Component("orderService")
@Scope(ScopeEnum.singleton)
public class OrderServiceImpl {

    public void createOrder(String orderNo){
        System.out.println("orderNo:" + orderNo);
    }
}
