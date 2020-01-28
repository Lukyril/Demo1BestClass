package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Address;
import model.Customer;
import model.Staff;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2020-01-28T15:13:09")
@StaticMetamodel(Store.class)
public class Store_ { 

    public static volatile CollectionAttribute<Store, Customer> customerCollection;
    public static volatile SingularAttribute<Store, Date> lastUpdate;
    public static volatile CollectionAttribute<Store, Staff> staffCollection;
    public static volatile SingularAttribute<Store, Short> storeId;
    public static volatile SingularAttribute<Store, Address> addressId;
    public static volatile SingularAttribute<Store, Staff> managerStaffId;

}