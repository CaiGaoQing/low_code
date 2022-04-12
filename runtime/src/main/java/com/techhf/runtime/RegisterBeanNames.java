package com.techhf.runtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterBeanNames {

    private List<String> mapping;

    private List<String> controller;

    private List<String> beans;
}
