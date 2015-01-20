package org.xpen.hello.bean.orika.mapper.configurer;

import ma.glasnost.orika.MapperFactory;

import org.springframework.stereotype.Component;
import org.xpen.hello.bean.orika.bean.Person;
import org.xpen.hello.bean.orika.dto.PersonDto;
import org.xpen.hello.bean.orika.mapper.common.MappingConfigurer;

@Component
public class PersonMappingConfigurer implements MappingConfigurer {
    
    public void configure(MapperFactory factory) {
        configureAuthor(factory);
        
    }
    

    
    private void configureAuthor(MapperFactory factory) {
        factory.classMap(Person.class, PersonDto.class)
        .fieldAToB("name.first", "firstName")
        .fieldAToB("name.last", "lastName")
        .fieldAToB("age", "age")
        .fieldAToB("houses[0].address.city", "houseCity1")
        .fieldAToB("houses[1].address.city", "houseCity2")
        .fieldAToB("houses[2].address.city", "houseCity3")
        .fieldAToB("houses[0].price", "housePrice1")
        .fieldAToB("houses[1].price", "housePrice2")
        .fieldAToB("houses[2].price", "housePrice3")
        //.fieldAToB("houses", "houseList")
        .register();
    }
}
