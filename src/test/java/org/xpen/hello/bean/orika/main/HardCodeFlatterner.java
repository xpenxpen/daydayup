package org.xpen.hello.bean.orika.main;


import org.xpen.hello.bean.orika.bean.Person;
import org.xpen.hello.bean.orika.dto.PersonDto;

public class HardCodeFlatterner {

    public PersonDto map(Person source) {
        PersonDto destination = new PersonDto();

        if (source.getName() != null) {
            destination.setFirstName(source.getName().getFirst());
        }
        if (source.getName() != null) {
            destination.setLastName(source.getName().getLast());
        }
        if (!(((java.lang.Integer) source.getAge()) == null)) {
            destination.setAge(((java.lang.Integer) source.getAge()).intValue());
        }
        if (!(((java.util.List) source.getHouses()) == null)
                && !((((java.util.List) source.getHouses()).size() <= 0 || ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(0)) == null))
                && !(((org.xpen.hello.bean.orika.bean.Address) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(0)).getAddress()) == null)) {

            destination
                    .setHouseCity1(((java.lang.String) ((org.xpen.hello.bean.orika.bean.Address) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                            .getHouses()).get(0)).getAddress()).getCity()));
        }
        if (!(((java.util.List) source.getHouses()) == null)
                && !((((java.util.List) source.getHouses()).size() <= 1 || ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(1)) == null))
                && !(((org.xpen.hello.bean.orika.bean.Address) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(1)).getAddress()) == null)) {

            destination
                    .setHouseCity2(((java.lang.String) ((org.xpen.hello.bean.orika.bean.Address) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                            .getHouses()).get(1)).getAddress()).getCity()));
        }
        if (!(((java.util.List) source.getHouses()) == null)
                && !((((java.util.List) source.getHouses()).size() <= 2 || ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(2)) == null))
                && !(((org.xpen.hello.bean.orika.bean.Address) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(2)).getAddress()) == null)) {

            destination
                    .setHouseCity3(((java.lang.String) ((org.xpen.hello.bean.orika.bean.Address) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                            .getHouses()).get(2)).getAddress()).getCity()));
        }
        if (!(((java.util.List) source.getHouses()) == null)
                && !((((java.util.List) source.getHouses()).size() <= 0 || ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(0)) == null))) {

            destination
                    .setHousePrice1(((long) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                            .getHouses()).get(0)).getPrice()));
        }
        if (!(((java.util.List) source.getHouses()) == null)
                && !((((java.util.List) source.getHouses()).size() <= 1 || ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(1)) == null))) {

            destination
                    .setHousePrice2(((long) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                            .getHouses()).get(1)).getPrice()));
        }
        if (!(((java.util.List) source.getHouses()) == null)
                && !((((java.util.List) source.getHouses()).size() <= 2 || ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                        .getHouses()).get(2)) == null))) {

            destination
                    .setHousePrice3(((long) ((org.xpen.hello.bean.orika.bean.House) ((java.util.List) source
                            .getHouses()).get(2)).getPrice()));
        }
        if (!(((java.util.List) source.getHouses()) == null)) {

            java.util.List new_houseList = ((java.util.List) new java.util.ArrayList(
                    ((java.util.List) source.getHouses()).size()));

            // new_houseList.addAll(mapperFacade.mapAsList(((java.util.List)source.getHouses()),
            // ((ma.glasnost.orika.metadata.Type)usedTypes[0]),
            // ((ma.glasnost.orika.metadata.Type)usedTypes[0]),
            // mappingContext));
            //destination.setHouseList(new_houseList);
        } else {
            //destination.setHouseList(null);
        }
        
        return destination;
    }

}
