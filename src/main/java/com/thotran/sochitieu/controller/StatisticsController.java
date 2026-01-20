package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.CategoryChartResponse;
import com.thotran.sochitieu.dto.response.DailyStatisticsResponse;
import com.thotran.sochitieu.dto.response.MonthComparisonResponse;
import com.thotran.sochitieu.security.SecurityUtils;
import com.thotran.sochitieu.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller cho Statistics/Reports APIs.
 * Base path: /api/statistics
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * Lấy userId từ JWT token
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    /**
     * GET /api/statistics/by-category
     * Thống kê theo category trong khoảng thời gian
     */
    @GetMapping("/by-category")
    public ResponseEntity<ApiResponse<List<CategoryChartResponse>>> getByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<CategoryChartResponse> data = statisticsService.getByCategory(
                getCurrentUserId(), startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    /**
     * GET /api/statistics/daily
     * Thống kê theo ngày trong tháng (cho calendar view)
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailyStatisticsResponse>>> getDailyStats(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int queryMonth = month != null ? month : now.getMonthValue();
        int queryYear = year != null ? year : now.getYear();
        
        List<DailyStatisticsResponse> data = statisticsService.getDailyStats(
                getCurrentUserId(), queryMonth, queryYear);
        
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    /**
     * GET /api/statistics/comparison
     * So sánh 2 tháng
     */
    @GetMapping("/comparison")
    public ResponseEntity<ApiResponse<MonthComparisonResponse>> compareMonths(
            @RequestParam Integer month1,
            @RequestParam Integer year1,
            @RequestParam Integer month2,
            @RequestParam Integer year2) {
        
        MonthComparisonResponse data = statisticsService.compareMonths(
                getCurrentUserId(), month1, year1, month2, year2);
        
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
