package com.czw.enums;

/**
 * @author: ChengZiwang
 * @date: 2020/7/20
 **/
public enum CodeMsg {

    //通用
    /** 成功 */
    SUCCESS(1, "success"),
    /** 失败 */
    SERVER_ERROR(1000, "服务器异常"),

    //通用的错误码 5001xx
    REQUEST_ILLEGAL(500102, "请求非法"),
    ACCESS_LIMIT_REACHED(500104, "访问太频繁！"),

    //登录模块 5003xx
    SESSION_ERROR(500300, "Session不存在或者已经失效"),
    MOBILE_EMPTY(500301,"手机号为空" ),
    PASSWORD_EMPTY(500302,"密码为空" ),
    MOBILE_ERROR(500303,"手机号格式错误" ),
    PASSWORD_ERROR(500304,"密码错误"),
    MOBILE_NOT_EXIST(500305,"手机号不存在"),
    BIND_ERROR(500306,"绑定异常"),


    //订单模块 5004xx
    ORDER_NOT_EXIST(500400, "订单不存在"),

    //秒杀模块 5005xx
    SECKILL_OVER(500501,"库存不足"),
    SECKILL_EXIST(500502,"已经参与秒杀"),
    SECKILL_FAILURE(500503,"秒杀失败");

    private Integer code;
    private String msg;

    CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
