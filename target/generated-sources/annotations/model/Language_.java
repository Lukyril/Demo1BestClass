package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Film;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2020-01-28T15:13:09")
@StaticMetamodel(Language.class)
public class Language_ { 

    public static volatile SingularAttribute<Language, Date> lastUpdate;
    public static volatile CollectionAttribute<Language, Film> filmCollection;
    public static volatile SingularAttribute<Language, Short> languageId;
    public static volatile SingularAttribute<Language, String> name;
    public static volatile CollectionAttribute<Language, Film> filmCollection1;

}