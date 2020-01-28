package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Address;
import model.Country;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2020-01-28T15:13:09")
@StaticMetamodel(City.class)
public class City_ { 

    public static volatile CollectionAttribute<City, Address> addressCollection;
    public static volatile SingularAttribute<City, String> city;
    public static volatile SingularAttribute<City, Date> lastUpdate;
    public static volatile SingularAttribute<City, Short> cityId;
    public static volatile SingularAttribute<City, Country> countryId;

}