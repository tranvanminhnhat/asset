package com.minhnhat.demo.features.master.service;

import com.minhnhat.demo.features.master.dto.response.InvestmentAllocationMasterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllocationMasterByIdUseCaseService {

    private final InvestmentAllocationMasterService investmentAllocationMasterService;

    public InvestmentAllocationMasterResponse execute(String code) {
        return investmentAllocationMasterService.getById(code);
    }
}

