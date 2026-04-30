package com.minhnhat.demo.features.master.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterCreateRequest;
import com.minhnhat.demo.features.master.dto.request.InvestmentAllocationMasterUpdateRequest;
import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import com.minhnhat.demo.features.master.service.InvestmentAllocationMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.minhnhat.demo.features.master.constant.InvestmentAllocationMasterConstant.*;

@RestController
@RequestMapping("/api/v1/allocation-master")
@RequiredArgsConstructor
public class InvestmentAllocationMasterController {

    private final InvestmentAllocationMasterService service;
    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InvestmentAllocationMasterResponse> create(@Valid @RequestBody InvestmentAllocationMasterCreateRequest request) {
        return ApiResponse.<InvestmentAllocationMasterResponse>builder()
                .data(service.create(request))
                .message(CREATE_INFO_001, messageService.get(CREATE_INFO_001), SERVICE_NAME)
                .build();
    }

    @GetMapping("/{code}")
    public ApiResponse<InvestmentAllocationMasterResponse> getById(@PathVariable String code) {
        return ApiResponse.<InvestmentAllocationMasterResponse>builder()
                .data(service.getById(code))
                .message(GET_BY_ID_INFO_001, messageService.get(GET_BY_ID_INFO_001), SERVICE_NAME)
                .build();
    }

    @GetMapping
    public ApiResponse<List<InvestmentAllocationMasterResponse>> getAll() {
        return ApiResponse.<List<InvestmentAllocationMasterResponse>>builder()
                .data(service.getAll())
                .message(GET_ALL_INFO_001, messageService.get(GET_ALL_INFO_001), SERVICE_NAME)
                .build();
    }

    @PutMapping("/{code}")
    public ApiResponse<InvestmentAllocationMasterResponse> update(
            @PathVariable String code,
            @Valid @RequestBody InvestmentAllocationMasterUpdateRequest request) {
        return ApiResponse.<InvestmentAllocationMasterResponse>builder()
                .data(service.update(code, request))
                .message(UPDATE_INFO_001, messageService.get(UPDATE_INFO_001), SERVICE_NAME)
                .build();
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ApiResponse.<Void>builder()
                .message(DELETE_INFO_001, messageService.get(DELETE_INFO_001), SERVICE_NAME)
                .build();
    }
}

