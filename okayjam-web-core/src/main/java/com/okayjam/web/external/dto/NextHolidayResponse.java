package com.okayjam.web.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 下一个节假日查询响应对象
 *
 * @author JamChen
 * @date 2026/01/08
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NextHolidayResponse {

    /**
     * 响应码（0表示成功）
     */
    private Integer code;

    /**
     * 下一个节假日信息
     */
    private HolidayDetail holiday;

    /**
     * 节假日前的调休信息（如果没有调休则为null）
     */
    private WorkdayDetail workday;

    /**
     * 节假日详细信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HolidayDetail {

        /**
         * 是否为节假日（该字段一定为true）
         */
        private Boolean holiday;

        /**
         * 节假日名称
         */
        private String name;

        /**
         * 薪资倍数（1=正常，2=双倍，3=三倍）
         */
        private Integer wage;

        /**
         * 节假日日期（yyyy-MM-dd格式）
         */
        private String date;

        /**
         * 距离该节假日还有多少天
         */
        private Integer rest;
    }

    /**
     * 调休日详细信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkdayDetail {

        /**
         * 是否为节假日（该字段一定为false）
         */
        private Boolean holiday;

        /**
         * 调休名称
         */
        private String name;

        /**
         * 薪资倍数
         */
        private Integer wage;

        /**
         * 是否为节后调休（true=节后调休，false=节前调休）
         */
        private Boolean after;

        /**
         * 调休的目标节假日
         */
        private String target;

        /**
         * 调休日期（yyyy-MM-dd格式）
         */
        private String date;

        /**
         * 距离该调休日还有多少天
         */
        private Integer rest;
    }
}
