package com.minhnhat.demo.features.master.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import com.minhnhat.demo.features.master.service.GetInvestmentAllocationMasterByIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.GET_BY_ID_INFO_001;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.SERVICE_NAME;

@RestController
@RequestMapping("/api/v1/allocation-master")
@RequiredArgsConstructor
public class InvestmentAllocationMasterGetByIdController {

    private final GetInvestmentAllocationMasterByIdService getInvestmentAllocationMasterByIdService;
    private final MessageService messageService;

    @GetMapping("/{id}")
    public ApiResponse<InvestmentAllocationMasterResponse> getById(@PathVariable Long id) {
        return ApiResponse.<InvestmentAllocationMasterResponse>builder()
                .data(getInvestmentAllocationMasterByIdService.execute(id))
                .message(GET_BY_ID_INFO_001, messageService.get(GET_BY_ID_INFO_001), SERVICE_NAME)
                .build();
    }
}

