package com.lyj.eblog.common.lang;


import lombok.Data;

import java.io.Serializable;


@Data
public class Result implements Serializable {

    private int status; // 0成功 1失败
    private String message;
    private Object data;
    private String action;

    public static Result success() {
        return Result.success("操作成功", null);
    }

    public static Result success(Object data) {
        return Result.success("操作成功", data);
    }

    public static Result success(String message, Object data) {
        Result result = new Result();
        result.status = 0;
        result.message = message;
        result.data = data;
        return result;
    }

    public static Result fail(String message) {
        Result result = new Result();
        result.status = -1;
        result.message = message;
/*        result.data = data;*/
        return result;
    }

    public Result action(String action) {
        this.action = action;
        return this;
    }

}
