package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.City;
import model.Customer;
import model.Staff;
import model.Store;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2020-01-28T15:13:09")
@StaticMetamodel(Address.class)
public class Address_ { 

    public static volatile CollectionAttribute<Address, Store> storeCollection;
    public static volatile SingularAttribute<Address, String> address;
    public static volatile SingularAttribute<Address, String> address2;
    public static volatile SingularAttribute<Address, String> phone;
    public static volatile CollectionAttribute<Address, Customer> customerCollection;
    public static volatile SingularAttribute<Address, String> postalCode;
    public static volatile SingularAttribute<Address, String> district;
    public static volatile SingularAttribute<Address, Date> lastUpdate;
    public static volatile SingularAttribute<Address, byte[]> location;
    public static volatile CollectionAttribute<Address, Staff> staffCollection;
    public static volatile SingularAttribute<Address, City> cityId;
    public static volatile SingularAttribute<Address, Short> addressId;

}