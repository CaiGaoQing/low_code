package com.techhf.runtime;

import org.springframework.context.annotation.ComponentScan;


//@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
//@ConditionalOnSingleCandidate(DataSource.class)
//@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisPlusLanguageDriverAutoConfiguration.class})
@ComponentScan("com.techhf.runtime")
public class RuntimeAutoConfiguration {

}
