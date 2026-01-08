package com.okayjam.web.controller;

import com.okayjam.web.external.HolidayService;
import com.okayjam.web.external.dto.HolidayBatchResponse;
import com.okayjam.web.external.dto.HolidayResponse;
import com.okayjam.web.external.dto.NextHolidayResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 节假日查询控制器
 *
 * @author JamChen
 * @date 2026/01/08
 */
@Slf4j
@RestController
@RequestMapping("/api/holiday")
@Tag(name = "节假日查询", description = "节假日信息查询接口")
public class HolidayController {

    @Lazy  // 延迟加载：只有在第一次调用 API 时才初始化 HolidayService
    @Autowired
    private HolidayService holidayService;

    /**
     * 查询当前年份的节假日
     *
     * @return 节假日信息
     */
    @GetMapping("/year")
    @Operation(summary = "查询当前年份节假日", description = "查询当前年份的所有节假日信息")
    public HolidayResponse getCurrentYearHolidays() {
        log.info("接收到查询当前年份节假日请求");
        return holidayService.getCurrentYearHolidays();
    }

    /**
     * 查询指定年份的节假日
     *
     * @param year 年份
     * @return 节假日信息
     */
    @GetMapping("/year/{year}")
    @Operation(summary = "查询指定年份节假日", description = "查询指定年份的所有节假日信息")
    public HolidayResponse getYearHolidays(
            @Parameter(description = "年份", example = "2026")
            @PathVariable Integer year) {
        log.info("接收到查询指定年份节假日请求，年份：{}", year);
        return holidayService.getYearHolidays(year);
    }

    /**
     * 查询指定年月的节假日
     *
     * @param year 年份
     * @param month 月份
     * @return 节假日信息
     */
    @GetMapping("/year/{year}/{month}")
    @Operation(summary = "查询指定年月节假日", description = "查询指定年月的节假日信息")
    public HolidayResponse getMonthHolidays(
            @Parameter(description = "年份", example = "2026")
            @PathVariable Integer year,
            @Parameter(description = "月份", example = "02")
            @PathVariable String month) {
        log.info("接收到查询指定年月节假日请求，年份：{}，月份：{}", year, month);
        return holidayService.getMonthHolidays(year, month);
    }

    /**
     * 批量查询指定日期的节假日
     *
     * @param dates 日期列表（格式：yyyy-MM-dd）
     * @param includeType 是否返回日期类型信息
     * @return 节假日信息
     */
    @PostMapping("/batch")
    @Operation(summary = "批量查询节假日", description = "批量查询指定日期的节假日信息，最多支持50个日期")
    public HolidayBatchResponse batchQueryHolidays(
            @Parameter(description = "日期列表（格式：yyyy-MM-dd）", example = "[\"2026-01-01\", \"2026-02-16\"]")
            @RequestBody List<String> dates,
            @Parameter(description = "是否返回日期类型信息", example = "true")
            @RequestParam(defaultValue = "false") boolean includeType) {
        log.info("接收到批量查询节假日请求，日期数量：{}", dates.size());
        return holidayService.batchQueryHolidays(dates, includeType);
    }

    /**
     * 获取未来N天内的所有节假日
     *
     * @param days 未来天数
     * @return 节假日信息
     */
    @GetMapping("/upcoming")
    @Operation(summary = "获取未来N天内的节假日", description = "获取从今天开始未来N天内的所有节假日信息")
    public HolidayBatchResponse getUpcomingHolidays(
            @Parameter(description = "未来天数", example = "20")
            @RequestParam(defaultValue = "20") int days) {
        log.info("接收到查询未来{}天内节假日请求", days);
        return holidayService.getUpcomingHolidays(days);
    }

    /**
     * 获取下一个节假日
     *
     * @param includeWeekend 是否包含周末
     * @return 下一个节假日信息
     */
    @GetMapping("/next")
    @Operation(summary = "获取下一个节假日", description = "获取距离当前时间最近的下一个节假日信息")
    public NextHolidayResponse getNextHoliday(
            @Parameter(description = "是否包含周末", example = "false")
            @RequestParam(defaultValue = "false") boolean includeWeekend) {
        log.info("接收到查询下一个节假日请求，是否包含周末：{}", includeWeekend);
        return holidayService.getNextHoliday(includeWeekend);
    }
}
