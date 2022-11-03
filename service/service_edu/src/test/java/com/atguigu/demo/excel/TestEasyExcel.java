package com.atguigu.demo.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestEasyExcel {
    public static void main(String[] args) {
        //implement excel write operation
        //1.设置写入文件夹的地址和excel文件名称
        String filename = "C:\\write.xlsx";

        //2. invoke method of easy excel
        //first param: file path, second param: the instantiated class
        EasyExcel.write(filename,DemoData.class).sheet("StudentList").doWrite(getData());
    }

    private static List<DemoData> getData(){
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setSno(i);
            data.setSname("lucy"+i);
            list.add(data);
        }
        return list;
    }
}
