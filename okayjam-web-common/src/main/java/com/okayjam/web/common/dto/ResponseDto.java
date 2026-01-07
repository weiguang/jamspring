package com.okayjam.web.common.dto;


import com.okayjam.web.common.util.HttpUtil;
import java.io.Serializable;
import org.slf4j.MDC;

/**
 * ResponseDto 统一返回格式
 *
 * @author Jam Chen
 * @date 2021/03/11 18:14
 **/
public class ResponseDto<T> implements Serializable {

    private String msg;
    private T data;
    private Integer code;
    private String traceId;


    /**
     * 统一返回格式
     *
     * @param obj 返回数据
     * @return obj
     */
    public static ResponseDto<?> success(Object obj) {
        return success(obj, null);
    }

    public static ResponseDto<?> success(Object obj, String msg) {
        ResponseDto<?> responseDto = new ResponseDto<>();
        responseDto.setData(obj);
        responseDto.setMsg(msg);
        responseDto.setCode(0);
        responseDto.setTraceId(MDC.get(HttpUtil.TRACE_ID));
        return responseDto;
    }

    public static ResponseDto<?> fail(String msg) {
        return fail(1000, msg);
    }

    public static ResponseDto<?> fail(int code, String msg) {
        return fail(code, msg, null);
    }

    public static ResponseDto<?> fail(int code, String msg, Object data) {
        ResponseDto<?> responseDto = new ResponseDto<String>();
        responseDto.setMsg(msg);
        responseDto.setCode(code);
        responseDto.setData(data);
        responseDto.setTraceId(MDC.get(HttpUtil.TRACE_ID));
        return responseDto;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = (T) data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
