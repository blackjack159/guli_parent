package com.atguigu.demo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData {

    @ExcelProperty("StudentNo")
    private Integer sno;

    @ExcelProperty("StudentName")
    private String sname;
}
