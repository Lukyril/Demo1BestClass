package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Address;
import model.Store;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2020-01-28T15:13:09")
@StaticMetamodel(Customer.class)
public class Customer_ { 

    public static volatile SingularAttribute<Customer, String> firstName;
    public static volatile SingularAttribute<Customer, String> lastName;
    public static volatile SingularAttribute<Customer, Date> lastUpdate;
    public static volatile SingularAttribute<Customer, Short> customerId;
    public static volatile SingularAttribute<Customer, Boolean> active;
    public static volatile SingularAttribute<Customer, Store> storeId;
    public static volatile SingularAttribute<Customer, String> email;
    public static volatile SingularAttribute<Customer, Date> createDate;
    public static volatile SingularAttribute<Customer, Address> addressId;

}