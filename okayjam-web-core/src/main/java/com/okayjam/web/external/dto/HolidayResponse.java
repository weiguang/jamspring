package com.okayjam.web.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;

/**
 * 节假日查询响应对象
 *
 * @author JamChen
 * @date 2026/01/08
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayResponse {

    /**
     * 响应码（0表示成功）
     */
    private Integer code;

    /**
     * 节假日数据，key为日期（MM-dd格式），value为节假日详情
     */
    private Map<String, HolidayInfo> holiday;

    /**
     * 节假日详细信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HolidayInfo {

        /**
         * 是否为节假日
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
         * 日期（yyyy-MM-dd格式）
         */
        private String date;

        /**
         * 距离下一个节假日的天数
         */
        private Integer rest;

        /**
         * 是否为补班日（补班日为true）
         */
        private Boolean after;

        /**
         * 补班/调休的目标节假日
         */
        private String target;
    }
}
