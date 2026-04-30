package com.minhnhat.demo.features.master.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestmentAllocationMasterConstant {
    public static final String CREATE_INFO_001 = "allocation.master.create.info.001";
    public static final String UPDATE_INFO_001 = "allocation.master.update.info.001";
    public static final String DELETE_INFO_001 = "allocation.master.delete.info.001";
    public static final String GET_ALL_INFO_001 = "allocation.master.getAll.info.001";
    public static final String GET_BY_ID_INFO_001 = "allocation.master.getById.info.001";
    
    public static final String ERROR_NOT_FOUND = "allocation.master.error.notFound";
    public static final String ERROR_DUPLICATE_CODE = "allocation.master.error.duplicateCode";
    public static final String ERROR_INVALID_PERCENTAGES = "allocation.master.error.invalidPercentages";
    
    public static final String SERVICE_NAME = "AllocationMasterService";
}

