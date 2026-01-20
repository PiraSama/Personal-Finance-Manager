package com.thotran.sochitieu.controller;

import com.thotran.sochitieu.dto.response.ApiResponse;
import com.thotran.sochitieu.dto.response.CategoryChartResponse;
import com.thotran.sochitieu.dto.response.DashboardSummaryResponse;
import com.thotran.sochitieu.dto.response.MonthlyTrendResponse;
import com.thotran.sochitieu.security.SecurityUtils;
import com.thotran.sochitieu.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller cho Dashboard APIs.
 * Base path: /api/dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * Lấy userId từ JWT token
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    /**
     * GET /api/dashboard/summary
     * Lấy tổng quan tài chính
     * 
     * @param month Tháng (mặc định: tháng hiện tại)
     * @param year Năm (mặc định: năm hiện tại)
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int queryMonth = month != null ? month : now.getMonthValue();
        int queryYear = year != null ? year : now.getYear();
        
        DashboardSummaryResponse summary = dashboardService.getSummary(
                getCurrentUserId(), queryMonth, queryYear);
        
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
    
    /**
     * GET /api/dashboard/chart/expense-by-category
     * Lấy data biểu đồ chi tiêu theo category
     */
    @GetMapping("/chart/expense-by-category")
    public ResponseEntity<ApiResponse<List<CategoryChartResponse>>> getExpenseByCategory(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        LocalDate now = LocalDate.now();
        int queryMonth = month != null ? month : now.getMonthValue();
        int queryYear = year != null ? year : now.getYear();
        
        List<CategoryChartResponse> chartData = dashboardService.getExpenseByCategory(
                getCurrentUserId(), queryMonth, queryYear);
        
        return ResponseEntity.ok(ApiResponse.success(chartData));
    }
    
    /**
     * GET /api/dashboard/chart/monthly-trend
     * Lấy xu hướng thu/chi theo tháng
     * 
     * @param months Số tháng cần lấy (mặc định: 6)
     */
    @GetMapping("/chart/monthly-trend")
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> getMonthlyTrend(
            @RequestParam(required = false, defaultValue = "6") Integer months) {
        
        List<MonthlyTrendResponse> trendData = dashboardService.getMonthlyTrend(
                getCurrentUserId(), months);
        
        return ResponseEntity.ok(ApiResponse.success(trendData));
    }
}
