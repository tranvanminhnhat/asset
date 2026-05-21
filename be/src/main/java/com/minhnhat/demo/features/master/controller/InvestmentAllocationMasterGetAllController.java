package com.minhnhat.demo.features.master.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import com.minhnhat.demo.features.master.service.InvestmentAllocationMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.GET_ALL_INFO_001;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.SERVICE_NAME;

@RestController
@RequestMapping("/api/v1/allocation-master")
@RequiredArgsConstructor
public class InvestmentAllocationMasterGetAllController {

    private final InvestmentAllocationMasterService investmentAllocationMasterService;
    private final MessageService messageService;

    @GetMapping
    public ApiResponse<List<InvestmentAllocationMasterResponse>> getAll() {
        return ApiResponse.<List<InvestmentAllocationMasterResponse>>builder()
                .data(investmentAllocationMasterService.getAll())
                .message(GET_ALL_INFO_001, messageService.get(GET_ALL_INFO_001), SERVICE_NAME)
                .build();
    }
}

