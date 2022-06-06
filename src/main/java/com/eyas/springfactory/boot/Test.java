package com.eyas.springfactory.boot;

import com.eyas.springfactory.boot.spring.SpringApplication;
import com.eyas.springfactory.service.UserServiceImpl;

public class Test {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SpringFactoryApplication.class);
//        UserServiceImpl userService = (UserServiceImpl) springApplication.getBean("userServiceImpl");
        System.out.println(springApplication.getBean(UserServiceImpl.class));
        System.out.println(springApplication.getBean(UserServiceImpl.class));
        System.out.println(springApplication.getBean(UserServiceImpl.class));
//        System.out.println(springApplication.getBean("userService"));
        UserServiceImpl userService = (UserServiceImpl) springApplication.getBean(UserServiceImpl.class);
        userService.test("编号89757");

    }
}
