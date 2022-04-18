package com.techhf.design.api;

import com.techhf.design.domain.basics.BasicsForm;
import com.techhf.design.domain.basics.BasicsNode;
import com.techhf.design.dsl.DefaultJsonResolver;
import com.techhf.design.dsl.model.FormRule;
import com.techhf.design.service.BasicsFormService;
import com.techhf.design.service.BasicsNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("form")
public class FormController {

    @Autowired
    private BasicsFormService basicsFormService;

    @Autowired
    private BasicsNodeService basicsNodeService;


    /**
     * 设计表单表单
     * @param basicsForm 表单 以及表单
     */
    @PostMapping("basics/design")
    public void save(@RequestBody FormRule basicsForm) {
        basicsFormService.saveBasicsForm(basicsForm);
    }




    @PostMapping("basics/{id}")
    public static void save(@PathVariable(name = "id")String id) {
//       new
    }

}
