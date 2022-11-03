package com.atguigu.commonutils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.HashMap;
import java.util.Map;

//it is for JSON return type (unified)
//auto generate the getter and setter for attributes below
@Data
public class R {

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("响应码")
    private Integer code;

    @ApiModelProperty("返回信息")
    private String message;

    @ApiModelProperty("返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    //make the constructor become private,other user can't invoke
    private R(){}


    //success method
    public static R ok(){
        R r = new R();
        r.success = true;
        r.code = ResultCode.SUCCESS;
        r.message = "SUCCESS";
        return r;
    }

    public static R error(){
        R r = new R();
        r.success = false;
        r.code = ResultCode.ERROR;
        r.message = "FAILED";
        return r;
    }

    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public R data(Map<String,Object> map){
        this.setData(map);
        return this;
    }

}
