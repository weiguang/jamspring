package com.okayjam.web.common.dto;


import com.okayjam.web.common.util.HttpUtil;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * ResponseDto 统一返回格式
 *
 * @author Jam Chen
 * 2021/03/11 18:14
 **/
@Data
public class ResponseDto<T> implements Serializable {

    private String msg;
    private T data;
    private Integer code;
    private String traceId;


    /**
     * 统一返回格式
     *
     * @param data 返回数据
     * @return ResponseDto
     */
    public static <T> ResponseDto<T> success(T data) {
        return success(data, null);
    }

    public static <T> ResponseDto<T> success(T data, String msg) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.setData(data);
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

    public static <T> ResponseDto<T> fail(int code, String msg, T data) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.setMsg(msg);
        responseDto.setCode(code);
        responseDto.setData(data);
        responseDto.setTraceId(MDC.get(HttpUtil.TRACE_ID));
        return responseDto;
    }


}
