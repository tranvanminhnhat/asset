package com.minhnhat.demo.features.master.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.service.InvestmentAllocationMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.DELETE_INFO_001;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.SERVICE_NAME;

@RestController
@RequestMapping("/api/v1/allocation-master")
@RequiredArgsConstructor
public class InvestmentAllocationMasterDeleteController {

    private final InvestmentAllocationMasterService investmentAllocationMasterService;
    private final MessageService messageService;

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code) {
        investmentAllocationMasterService.delete(code);
        return ApiResponse.<Void>builder()
                .message(DELETE_INFO_001, messageService.get(DELETE_INFO_001), SERVICE_NAME)
                .build();
    }
}

