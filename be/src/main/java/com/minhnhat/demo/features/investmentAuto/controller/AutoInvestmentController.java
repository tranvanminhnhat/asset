package com.minhnhat.demo.features.investmentAuto.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.features.investment.dto.response.FinalizeSessionResponse;
import com.minhnhat.demo.features.investmentAuto.dto.request.AutoInvestRequest;
import com.minhnhat.demo.features.investmentAuto.service.AutoInvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/investment/auto")
@RequiredArgsConstructor
public class AutoInvestmentController {

	private final AutoInvestmentService autoInvestmentService;

	@PostMapping
	public ApiResponse<FinalizeSessionResponse> execute(@RequestBody AutoInvestRequest request) {
		FinalizeSessionResponse response = autoInvestmentService.execute(request.getAmount(), request.getNotificationId());
		return ApiResponse.<FinalizeSessionResponse>builder()
				.data(response)
				.message("SUCCESS", "Auto investment completed", "AutoInvestmentService")
				.build();
	}
}

