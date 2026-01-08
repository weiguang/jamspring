package com.okayjam.web.external.impl;

import com.okayjam.web.common.configuration.http.HttpClientFactory;
import com.okayjam.web.external.HolidayService;
import com.okayjam.web.external.dto.HolidayBatchResponse;
import com.okayjam.web.external.dto.HolidayResponse;
import com.okayjam.web.external.dto.NextHolidayResponse;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 节假日查询服务实现类
 * 使用 timor.tech 提供的免费节假日 API
 *
 * @author JamChen
 * @date 2026/01/08
 */
@Slf4j
@Lazy
@Service
public class HolidayServiceImpl implements HolidayService {

    private final RestClient restClient;

    @Autowired
    public HolidayServiceImpl(
            HttpClientFactory httpClientFactory,
            @Value("${holiday.api.base-url:http://timor.tech/api/holiday}") String holidayApiBaseUrl) {
        // 使用 HttpClientFactory 创建 RestClient
        log.info("初始化 HolidayService，API 基础地址：{}", holidayApiBaseUrl);
        this.restClient = httpClientFactory
                .createRestClientBuilder(holidayApiBaseUrl, "HolidayAPI")
                .build();
    }

    @Override
    public HolidayResponse getCurrentYearHolidays() {
        int currentYear = Year.now().getValue();
        return getYearHolidays(currentYear);
    }

    @Override
    public HolidayResponse getYearHolidays(Integer year) {
        log.info("查询指定年份节假日信息，年份：{}", year);

        try {
            // 注意：获取整年数据需要在年份后面加斜杠
            String url = String.format("/year/%d/", year);

            // 发送 GET 请求
            ResponseEntity<HolidayResponse> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(HolidayResponse.class);

            HolidayResponse holidayResponse = response.getBody();

            if (holidayResponse != null && holidayResponse.getCode() == 0) {
                log.info("查询节假日成功，年份：{}，节假日数量：{}",
                        year, holidayResponse.getHoliday() != null ? holidayResponse.getHoliday().size() : 0);
            } else {
                log.warn("查询节假日返回异常，年份：{}，响应码：{}", year,
                        holidayResponse != null ? holidayResponse.getCode() : null);
            }

            return holidayResponse;

        } catch (Exception e) {
            log.error("查询节假日失败，年份：{}", year, e);
            throw new RuntimeException("查询节假日失败：" + e.getMessage(), e);
        }
    }

    @Override
    public HolidayResponse getMonthHolidays(Integer year, String month) {
        log.info("查询指定年月节假日信息，年份：{}，月份：{}", year, month);

        try {
            // 格式化月份为两位数
            String formattedMonth = month.length() == 1 ? "0" + month : month;
            String url = String.format("/year/%d-%s", year, formattedMonth);

            // 发送 GET 请求
            ResponseEntity<HolidayResponse> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(HolidayResponse.class);

            HolidayResponse holidayResponse = response.getBody();

            if (holidayResponse != null && holidayResponse.getCode() == 0) {
                log.info("查询节假日成功，年月：{}-{}，节假日数量：{}",
                        year, formattedMonth,
                        holidayResponse.getHoliday() != null ? holidayResponse.getHoliday().size() : 0);
            } else {
                log.warn("查询节假日返回异常，年月：{}-{}，响应码：{}", year, formattedMonth,
                        holidayResponse != null ? holidayResponse.getCode() : null);
            }

            return holidayResponse;

        } catch (Exception e) {
            log.error("查询节假日失败，年月：{}-{}", year, month, e);
            throw new RuntimeException("查询节假日失败：" + e.getMessage(), e);
        }
    }

    @Override
    public HolidayBatchResponse batchQueryHolidays(List<String> dates, boolean includeType) {
        log.info("批量查询节假日信息，日期数量：{}", dates.size());

        if (dates == null || dates.isEmpty()) {
            throw new IllegalArgumentException("日期列表不能为空");
        }

        if (dates.size() > 50) {
            throw new IllegalArgumentException("日期数量不能超过50个");
        }

        try {
            // 构建 URL，使用 &d= 连接多个日期
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/batch");

            // 添加日期参数
            for (String date : dates) {
                builder.queryParam("d", date);
            }

            // 添加类型参数
            if (includeType) {
                builder.queryParam("type", "Y");
            }

            String url = builder.toUriString();

            // 发送 GET 请求
            ResponseEntity<HolidayBatchResponse> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(HolidayBatchResponse.class);

            HolidayBatchResponse batchResponse = response.getBody();

            if (batchResponse != null && batchResponse.getCode() == 0) {
                long holidayCount = batchResponse.getHoliday().values().stream()
                        .filter(h -> h != null && h.getHoliday() != null && h.getHoliday())
                        .count();
                log.info("批量查询节假日成功，查询日期数：{}，节假日数量：{}", dates.size(), holidayCount);
            } else {
                log.warn("批量查询节假日返回异常，响应码：{}",
                        batchResponse != null ? batchResponse.getCode() : null);
            }

            return batchResponse;

        } catch (Exception e) {
            log.error("批量查询节假日失败", e);
            throw new RuntimeException("批量查询节假日失败：" + e.getMessage(), e);
        }
    }

    @Override
    public HolidayBatchResponse getUpcomingHolidays(int days) {
        log.info("查询未来{}天内的节假日", days);

        if (days <= 0 || days > 365) {
            throw new IllegalArgumentException("天数必须在1-365之间");
        }

        try {
            // 生成未来N天的日期列表
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<String> dates = new ArrayList<>();
            for (int i = 0; i <= days; i++) {
                dates.add(today.plusDays(i).format(formatter));
            }

            // 批量查询这些日期
            HolidayBatchResponse response = batchQueryHolidays(dates, true);

            // 过滤出节假日
            if (response != null && response.getHoliday() != null) {
                response.setHoliday(
                        response.getHoliday().entrySet().stream()
                                .filter(entry -> entry.getValue() != null &&
                                        entry.getValue().getHoliday() != null &&
                                        entry.getValue().getHoliday())
                                .collect(Collectors.toMap(
                                        entry -> entry.getKey(),
                                        entry -> entry.getValue()
                                ))
                );

                log.info("查询未来{}天内的节假日成功，节假日数量：{}", days, response.getHoliday().size());
            }

            return response;

        } catch (Exception e) {
            log.error("查询未来{}天内的节假日失败", days, e);
            throw new RuntimeException("查询未来节假日失败：" + e.getMessage(), e);
        }
    }

    @Override
    public NextHolidayResponse getNextHoliday(boolean includeWeekend) {
        log.info("查询下一个节假日，是否包含周末：{}", includeWeekend);

        try {
            // 构建 URL
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/next");
            builder.queryParam("type", "Y");

            if (includeWeekend) {
                builder.queryParam("week", "Y");
            }

            String url = builder.toUriString();

            // 发送 GET 请求
            ResponseEntity<NextHolidayResponse> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(NextHolidayResponse.class);

            NextHolidayResponse nextHolidayResponse = response.getBody();

            if (nextHolidayResponse != null && nextHolidayResponse.getCode() == 0) {
                if (nextHolidayResponse.getHoliday() != null) {
                    log.info("查询下一个节假日成功，节假日：{}，日期：{}",
                            nextHolidayResponse.getHoliday().getName(),
                            nextHolidayResponse.getHoliday().getDate());
                }
            } else {
                log.warn("查询下一个节假日返回异常，响应码：{}",
                        nextHolidayResponse != null ? nextHolidayResponse.getCode() : null);
            }

            return nextHolidayResponse;

        } catch (Exception e) {
            log.error("查询下一个节假日失败", e);
            throw new RuntimeException("查询下一个节假日失败：" + e.getMessage(), e);
        }
    }
}
