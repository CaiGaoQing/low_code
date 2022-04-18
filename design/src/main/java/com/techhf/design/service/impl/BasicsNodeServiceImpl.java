package com.techhf.design.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.techhf.design.domain.basics.BasicsNode;
import com.techhf.design.mapper.BasicsNodeMapper;
import com.techhf.design.service.BasicsNodeService;
import org.springframework.stereotype.Service;

@Service
public class BasicsNodeServiceImpl extends ServiceImpl<BasicsNodeMapper, BasicsNode> implements BasicsNodeService {
}
