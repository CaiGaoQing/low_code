package com.techhf.meta.register;

import com.techhf.meta.util.ClassUtil;

public abstract class AbstractSchemaRegister implements SchemaRegister {

    @Override
    public SchemaRegister getDefaultSchemaRegister(){
        if (ClassUtil.classIsExit("org.springframework.data.redis.core.RedisTemplate")){
            return new InMemorySchemaRegister();
        }
        return new InMemorySchemaRegister();
    }

}
