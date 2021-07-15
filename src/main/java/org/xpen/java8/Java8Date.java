package org.xpen.java8;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Java8Date {

    //将日期转换为最接近的整分钟时间(1/2/5分钟......)
    public static void main(String[] args) {
        int[] periods = {1, 2, 5, 10, 15 ,20, 30, 60};
        LocalDateTime time = LocalDateTime.of(2021, 7, 1, 10, 47, 27);
        
        System.out.println("time=" + time);
        
        for (int period : periods) {
            LocalDateTime nearestFloorTime = time.truncatedTo(ChronoUnit.HOURS)
                    .plusMinutes(period * (time.getMinute() / period));
            System.out.println("period=" + period + ", time=" + nearestFloorTime);
        }
   }

}
