package com.eyas.springfactory.service;

import com.eyas.springfactory.boot.spring.Component;
import com.eyas.springfactory.boot.spring.Scope;
import com.eyas.springfactory.boot.spring.ScopeEnum;

@Component("vipService")
@Scope(ScopeEnum.singleton)
public class VipServiceImpl {

    public void vip(String vipCode){
        System.out.println(vipCode);
    }
}
