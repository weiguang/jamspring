package com.okayjam.web.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;

/**
 * 批量查询节假日响应对象
 *
 * @author JamChen
 * @date 2026/01/08
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayBatchResponse {

    /**
     * 响应码（0表示成功）
     */
    private Integer code;

    /**
     * 节假日数据，key为日期（yyyy-MM-dd格式），value为节假日详情
     * 如果不是节假日，则value为null
     */
    private Map<String, HolidayDetail> holiday;

    /**
     * 日期类型信息（需要传参 type=Y 才返回）
     */
    private Map<String, DateType> type;

    /**
     * 节假日详细信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HolidayDetail {

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
    }

    /**
     * 日期类型信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateType {

        /**
         * 节假日类型：0=工作日，1=周末，2=节日，3=调休
         */
        private Integer type;

        /**
         * 节假日类型中文名（如：周一、周二、国庆节、国庆节调休等）
         */
        private String name;

        /**
         * 一周中的第几天（1-7，分别表示周一至周日）
         */
        private Integer week;
    }
}
