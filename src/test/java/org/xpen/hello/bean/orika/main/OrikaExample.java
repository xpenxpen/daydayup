package org.xpen.hello.bean.orika.main;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xpen.hello.bean.orika.bean.Address;
import org.xpen.hello.bean.orika.bean.House;
import org.xpen.hello.bean.orika.bean.Name;
import org.xpen.hello.bean.orika.bean.Person;
import org.xpen.hello.bean.orika.dto.PersonDto;

/**
 * orika演示
 * 演示了3种bean之间拷贝的方法，以及性能测试
 * 1.硬编码
 * 2.orika MapperFacade
 * 3.orika BoundMapperFacade
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:library-beans.xml"})
public class OrikaExample extends AbstractJUnit4SpringContextTests {

    
    @Autowired
    //MapperFacade mapperFacade;
    MapperFactory mapperFactory;
    
    @Test
    public void testMapping() {
        
        Person bp = buildPerson();
        
        MapperFacade mapperFacade = mapperFactory.getMapperFacade();
        
        BoundMapperFacade<Person,PersonDto> personMapperFacade
          = mapperFactory.getMapperFacade(Person.class, PersonDto.class);
        
        PersonDto p1 = mapperFacade.map(bp, PersonDto.class);
        System.out.println(p1);
        
        HardCodeFlatterner hcf = new HardCodeFlatterner();
        
        PersonDto p2 = hcf.map(bp);
        System.out.println(p2);
        
        PersonDto p3 = personMapperFacade.map(bp);
        System.out.println(p3);
        
        
        //perfomance test
        int testRound = 1000000;
        
        for (int j = 0; j < 1; j++) {
        
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < testRound; i++) {
                PersonDto p = mapperFacade.map(bp, PersonDto.class);
            }
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            System.out.println("orika-->" + timeElapsed + "ms");
            
            startTime = System.currentTimeMillis();
            for (int i = 0; i < testRound; i++) {
                PersonDto p = hcf.map(bp);;
            }
            endTime = System.currentTimeMillis();
            timeElapsed = endTime - startTime;
            System.out.println("hardcode-->" + timeElapsed + "ms");
            
            startTime = System.currentTimeMillis();
            for (int i = 0; i < testRound; i++) {
                PersonDto p = personMapperFacade.map(bp);
            }
            endTime = System.currentTimeMillis();
            timeElapsed = endTime - startTime;
            System.out.println("orika BoundMapperFacade-->" + timeElapsed + "ms");
        }

    }

    private Person buildPerson() {
        Person person = new Person();
        Name name = new Name();
        name.setFirst("George");
        name.setLast("Bush");
        person.setName(name);
        
        House house1=new House();
        House house2=new House();
        House house3=new House();
        Address address1=new Address();
        Address address2=new Address();
        Address address3=new Address();
        
        address1.setCity("shanghai");
        address1.setRoad("Pudian Rd.");
        address2.setCity("beijing");
        address2.setRoad("Changan Rd.");
        address3.setCity("shanghai");
        address3.setRoad("Dongfang Rd.");
        
        house1.setAddress(address1);
        house1.setPrice(1300000L);
        house2.setAddress(address2);
        house2.setPrice(2000000L);
        house3.setAddress(address3);
        house3.setPrice(3000000L);
        
        List<House> houses = new ArrayList<House>();
        houses.add(house1);
        houses.add(house2);
        houses.add(house3);
        
        person.setHouses(houses);
        person.setAge(20);
        
        
        return person;
    }
    

}
