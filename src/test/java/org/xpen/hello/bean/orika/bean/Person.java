package org.xpen.hello.bean.orika.bean;

import java.util.List;

public class Person {
    private Name name;
    private Integer age;
    private List<House> houses;
    
    
    public Name getName() {
        return name;
    }
    public void setName(Name name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public List<House> getHouses() {
        return houses;
    }
    public void setHouses(List<House> houses) {
        this.houses = houses;
    }
    
    
}
