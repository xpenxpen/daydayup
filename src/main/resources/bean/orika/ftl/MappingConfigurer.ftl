package ${doc.flattener["package-name"]};

import ma.glasnost.orika.MapperFactory;

import org.xpen.hello.bean.orika.mapper.common.MappingConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ${doc.flattener["class-name"]} implements MappingConfigurer {
    
    public void configure(MapperFactory factory) {
        factory.classMap(${doc.flattener["src-class"]}.class, ${doc.flattener["dest-class"]}.class)
        <#list doc.flattener.mappings.mapping as mapping>
        .fieldAToB("${mapping.@src}", "${mapping.@dest}")
        </#list>
        .register();
    }

}
