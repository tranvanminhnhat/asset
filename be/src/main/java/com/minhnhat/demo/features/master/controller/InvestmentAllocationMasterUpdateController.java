package com.minhnhat.demo.features.master.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterUpdateRequest;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import com.minhnhat.demo.features.master.service.InvestmentAllocationMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.SERVICE_NAME;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.UPDATE_INFO_001;

@RestController
@RequestMapping("/api/v1/allocation-master")
@RequiredArgsConstructor
public class InvestmentAllocationMasterUpdateController {

    private final InvestmentAllocationMasterService investmentAllocationMasterService;
    private final MessageService messageService;

    @PutMapping("/{code}")
    public ApiResponse<InvestmentAllocationMasterResponse> update(
            @PathVariable String code,
            @Valid @RequestBody InvestmentAllocationMasterUpdateRequest request) {
        return ApiResponse.<InvestmentAllocationMasterResponse>builder()
                .data(investmentAllocationMasterService.update(code, request))
                .message(UPDATE_INFO_001, messageService.get(UPDATE_INFO_001), SERVICE_NAME)
                .build();
    }
}

