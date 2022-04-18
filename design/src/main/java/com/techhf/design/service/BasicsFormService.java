package com.techhf.design.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.techhf.design.domain.basics.BasicsForm;
import com.techhf.design.domain.basics.BasicsNode;
import com.techhf.design.dsl.model.FormRule;

public interface BasicsFormService extends IService<BasicsForm> {

    void saveBasicsForm(FormRule formRule);
}
