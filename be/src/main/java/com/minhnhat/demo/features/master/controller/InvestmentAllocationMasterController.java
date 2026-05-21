package com.minhnhat.demo.features.master.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterCreateRequest;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import com.minhnhat.demo.features.master.service.InvestmentAllocationMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.*;

@RestController
@RequestMapping("/api/v1/allocation-master")
@RequiredArgsConstructor
public class InvestmentAllocationMasterController {

    private final InvestmentAllocationMasterService investmentAllocationMasterService;
    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InvestmentAllocationMasterResponse> create(@Valid @RequestBody InvestmentAllocationMasterCreateRequest request) {
        return ApiResponse.<InvestmentAllocationMasterResponse>builder()
                .data(investmentAllocationMasterService.create(request))
                .message(CREATE_INFO_001, messageService.get(CREATE_INFO_001), SERVICE_NAME)
                .build();
    }
}

