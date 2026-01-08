package com.okayjam.web.external;

import com.okayjam.web.external.dto.HolidayBatchResponse;
import com.okayjam.web.external.dto.HolidayResponse;
import com.okayjam.web.external.dto.NextHolidayResponse;
import java.util.List;

/**
 * 节假日查询服务接口
 *
 * @author JamChen
 * @date 2026/01/08
 */
public interface HolidayService {

    /**
     * 查询当前年份的节假日信息
     *
     * @return 节假日信息
     */
    HolidayResponse getCurrentYearHolidays();

    /**
     * 查询指定年份的节假日信息
     *
     * @param year 年份（如：2026）
     * @return 节假日信息
     */
    HolidayResponse getYearHolidays(Integer year);

    /**
     * 查询指定年月的节假日信息
     *
     * @param year 年份（如：2026）
     * @param month 月份（如：02）
     * @return 节假日信息
     */
    HolidayResponse getMonthHolidays(Integer year, String month);

    /**
     * 批量查询指定日期的节假日信息
     *
     * @param dates 日期列表（格式：yyyy-MM-dd，最多50个）
     * @param includeType 是否返回日期类型信息
     * @return 节假日信息
     */
    HolidayBatchResponse batchQueryHolidays(List<String> dates, boolean includeType);

    /**
     * 获取未来N天内的所有节假日
     *
     * @param days 未来天数（如：20表示未来20天）
     * @return 节假日信息
     */
    HolidayBatchResponse getUpcomingHolidays(int days);

    /**
     * 获取下一个节假日信息
     *
     * @param includeWeekend 是否包含周末
     * @return 下一个节假日信息
     */
    NextHolidayResponse getNextHoliday(boolean includeWeekend);
}
