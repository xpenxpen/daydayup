package org.xpen.hello.bean.orika.dto;

import java.util.List;

public class PersonDto {
    private String firstName;
    private boolean hasFirstName;
    private String lastName;
    private boolean hasLastName;
    private int age;
    private boolean hasAge;
    
    private String houseCity1;
    private boolean hasHouseCity1;
    private long housePrice1;
    private boolean hasHousePrice1;
    
    private String houseCity2;
    private boolean hasHouseCity2;
    private long housePrice2;
    private boolean hasHousePrice2;
    
    private String houseCity3;
    private boolean hasHouseCity3;
    private long housePrice3;
    private boolean hasHousePrice3;
    
    private List<String> cars;
    private boolean hasCars;
    
    public String getFirstName() {
        return firstName;
    }
    public boolean hasFirstName() {
        return hasFirstName;
    }
    public void setFirstName(String firstName) {
        //System.out.println("setFirstName called");
        this.firstName = firstName;
        this.hasFirstName = true;
    }
    
    public String getLastName() {
        return lastName;
    }
    public boolean hasLastName() {
        return hasLastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.hasLastName = true;
    }
    
    public int getAge() {
        return age;
    }
    public boolean hasAge() {
        return hasAge;
    }
    public void setAge(int age) {
        //System.out.println("setAge called");
        this.age = age;
        this.hasAge = true;
    }
    
    public String getHouseCity1() {
        return houseCity1;
    }
    public boolean hasHouseCity1() {
        return hasHouseCity1;
    }
    public void setHouseCity1(String houseCity1) {
        this.houseCity1 = houseCity1;
        this.hasHouseCity1 = true;
    }
    
    public long getHousePrice1() {
        return housePrice1;
    }
    public boolean hasHousePrice1() {
        return hasHousePrice1;
    }
    public void setHousePrice1(long housePrice1) {
        //System.out.println("setHousePrice1 called");
        this.housePrice1 = housePrice1;
        this.hasHousePrice1 = true;
    }
    
    public String getHouseCity2() {
        return houseCity2;
    }
    public boolean hasHouseCity2() {
        return hasHouseCity2;
    }
    public void setHouseCity2(String houseCity2) {
        this.houseCity2 = houseCity2;
        this.hasHouseCity2 = true;
    }
    
    public long getHousePrice2() {
        return housePrice2;
    }
    public boolean hasHousePrice2() {
        return hasHousePrice2;
    }
    public void setHousePrice2(long housePrice2) {
        //System.out.println("setHousePrice2 called");
        this.housePrice2 = housePrice2;
        this.hasHousePrice2 = true;
    }
    
    public String getHouseCity3() {
        return houseCity3;
    }
    public boolean hasHouseCity3() {
        return hasHouseCity3;
    }
    public void setHouseCity3(String houseCity3) {
        this.houseCity3 = houseCity3;
        this.hasHouseCity3 = true;
    }
    
    public long getHousePrice3() {
        return housePrice3;
    }
    public boolean hasHousePrice3() {
        return hasHousePrice3;
    }
    public void setHousePrice3(long housePrice3) {
        //System.out.println("setHousePrice3 called");
        this.housePrice3 = housePrice3;
        this.hasHousePrice3 = true;
    }
    
    
    public List<String> getCars() {
        return cars;
    }
    public boolean hasCars() {
        return hasCars;
    }
    public void setCars(List<String> cars) {
        this.cars = cars;
        this.hasCars = true;
    }
    //    public List<House> getHouseList() {
//        return houseList;
//    }
//    public void setHouseList(List<House> houseList) {
//        this.houseList = houseList;
//    }
    @Override
    public String toString() {
        return "PersonDto [firstName=" + firstName + ", lastName=" + lastName
                + ", age=" + age + ", houseCity1=" + houseCity1
                + ", housePrice1=" + housePrice1 + ", houseCity2=" + houseCity2
                + ", housePrice2=" + housePrice2 + ", houseCity3=" + houseCity3
                + ", housePrice3=" + housePrice3 //+ ", houseList=" + houseList
                + "]";
    }
    
    
}
