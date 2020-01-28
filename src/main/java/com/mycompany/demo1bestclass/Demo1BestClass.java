/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.demo1bestclass;

import controller.CustomerJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Lukyril
 */
public class Demo1BestClass {
    private static final String PERSISTANCE_UNIT_NAME = "com.mycompany_Demo1BestClass_jar_1.0-SNAPSHOTPU";
    private static EntityManagerFactory factory;

    public static void main(String[] args) {
        factory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);
        CustomerJpaController customerJpaController = new CustomerJpaController(factory);
        customerJpaController.getAllCustomer().forEach(customer ->
        {
            System.out.println(customer.toString());
        });
    }

}
