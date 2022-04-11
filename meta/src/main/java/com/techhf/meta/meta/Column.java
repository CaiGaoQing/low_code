package com.techhf.meta.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Column {

    private Long length;

    private String name;

    private String type;

    private String remarks;
}
