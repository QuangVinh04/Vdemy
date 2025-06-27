package V1Learn.spring.controller;

import V1Learn.spring.DTO.Response.APIResponse;
import V1Learn.spring.DTO.Response.DashboardSummaryResponse;
import V1Learn.spring.DTO.Response.MonthlyRevenueResponse;
import V1Learn.spring.DTO.Response.OrderEarningResponse;
import V1Learn.spring.Service.RevenueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/revenue")
public class RevenueController {

    RevenueService revenueService;

    @GetMapping("/summary")
    APIResponse<DashboardSummaryResponse> getSummary(@RequestParam int month,
                                                     @RequestParam int year){
        log.info("Controller: getSummary");
        return APIResponse.<DashboardSummaryResponse>builder()
                .result(revenueService.getDashboardSummary(month, year))
                .build();
    }

    @GetMapping("/monthly")
    APIResponse<List<MonthlyRevenueResponse>> getMonthly(@RequestParam int year) {
        log.info("Controller: getMonthly");
        return APIResponse.<List<MonthlyRevenueResponse>>builder()
                .result(revenueService.getMonthlyRevenue(year))
                .build();
    }


    @GetMapping("/orders")
    APIResponse<List<OrderEarningResponse>> getOrders(@RequestParam(required = false) String from,
                                                      @RequestParam(required = false) String to) {
        log.info("Controller: get Orders");
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusDays(7);
        LocalDate toDate = to != null ? LocalDate.parse(to) : LocalDate.now();
        return APIResponse.<List<OrderEarningResponse>>builder()
                .result(revenueService.getOrderEarnings(fromDate, toDate))
                .build();
    }
}
