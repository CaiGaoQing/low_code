package com.techhf.design.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techhf.design.domain.basics.BasicsForm;
import com.techhf.design.domain.basics.BasicsNode;
import com.techhf.design.dsl.DefaultJsonResolver;
import com.techhf.design.dsl.model.FormRule;
import com.techhf.design.mapper.BasicsFormMapper;
import com.techhf.design.service.BasicsFormService;
import com.techhf.design.service.BasicsNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BasicsFormServiceImpl extends ServiceImpl<BasicsFormMapper, BasicsForm> implements BasicsFormService {

    @Autowired
    private BasicsFormService basicsFormService;


    @Autowired
    private BasicsNodeService basicsNodeService;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void saveBasicsForm(FormRule formRule) {
        DefaultJsonResolver defaultJsonResolver = new DefaultJsonResolver(formRule);
        BasicsForm formInfo = defaultJsonResolver.getFormInfo(BasicsForm.class);
        basicsFormService.save(formInfo);
        defaultJsonResolver.addNodeKey("formId",formInfo.getFormId());
        defaultJsonResolver.addNodeKey("appId",formInfo.getAppId());
        List<BasicsNode> node = defaultJsonResolver.getNode(BasicsNode.class);
        basicsNodeService.saveBatch(node);
    }
}
