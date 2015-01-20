package org.xpen.hello.bean.orika.generated.flattener;

import ma.glasnost.orika.MapperFactory;

import org.xpen.hello.bean.orika.mapper.common.MappingConfigurer;
import org.springframework.stereotype.Component;

@Component
public class PersonMappingConfigurer3 implements MappingConfigurer {
    
    public void configure(MapperFactory factory) {
        factory.classMap(org.xpen.hello.bean.orika.bean.Person.class, org.xpen.hello.bean.orika.dto.PersonDto.class)
        .fieldAToB("name.first", "firstName")
        .fieldAToB("name.last", "lastName")
        .fieldAToB("age", "age")
        .fieldAToB("houses[0].address.city", "houseCity1")
        .fieldAToB("houses[1].address.city", "houseCity2")
        .fieldAToB("houses[2].address.city", "houseCity3")
        .fieldAToB("houses[0].price", "housePrice1")
        .fieldAToB("houses[1].price", "housePrice2")
        .fieldAToB("houses[2].price", "housePrice3")
        .register();
    }

}
