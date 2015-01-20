package org.xpen.hello.bean.orika.mapper.common;

import java.util.Set;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MapperFactoryFactoryBean implements FactoryBean<MapperFactory> {
    
    private final Set<MappingConfigurer> configurers;
    private MapperFactory mapperFactory;
    
    public MapperFactoryFactoryBean(Set<MappingConfigurer> configurers) {
        super();
        this.configurers = configurers;
    }
    
    public MapperFactory getObject() throws Exception {
        mapperFactory = new DefaultMapperFactory.Builder()
            //.compilerStrategy(new EclipseJdtCompilerStrategy())
            .mapNulls(false).useAutoMapping(false).build();
        
        for (MappingConfigurer configurer : configurers) {
            configurer.configure(mapperFactory);
        }
        
        //mapperFactory.build();
        
        return mapperFactory;
        
    }
    
    public Class<?> getObjectType() {
        return MapperFactory.class;
    }
    
    public boolean isSingleton() {
        return true;
    }

    
}
