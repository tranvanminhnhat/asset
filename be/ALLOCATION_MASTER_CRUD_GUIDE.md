# Complete CRUD API for investment_allocation_master

## Files Created So Far
1. ✅ Migration: V004__Create_Investment_Allocation_Master_Table.sql
2. ✅ Entity: InvestmentAllocationMasterEntity.java (already exists)
3. ✅ Repository: InvestmentAllocationMasterRepository.java (already exists)
4. ✅ Constant: InvestmentAllocationMasterConstant.java (created as C.java - rename it!)
5. ✅ Service: InvestmentAllocationMasterService.java (created as S.java - rename it!)
6. ⏳ Controller: InvestmentAllocationMasterController.java (IN PROGRESS)
7. ⏳ DTOs: 3 files needed (CreateRequest, UpdateRequest, Response)
8. ⏳ messages.properties: Add message keys

## Files to Create Manually (Due to Timeout Issues)

### Step 1: Rename Created Files
- Rename: `features/master/constant/C.java` → `InvestmentAllocationMasterConstant.java`
- Rename: `features/master/service/S.java` → `InvestmentAllocationMasterService.java`

### Step 2: Create Request DTOs

**File: features/master/dto/request/InvestmentAllocationMasterCreateRequest.java**
```java
package com.minhnhat.demo.features.master.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAllocationMasterCreateRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Stock percentage is required")
    @DecimalMin(value = "0", message = "Stock percentage must be >= 0")
    @DecimalMax(value = "100", message = "Stock percentage must be <= 100")
    private BigDecimal stockPercent;

    @NotNull(message = "Emergency percentage is required")
    @DecimalMin(value = "0", message = "Emergency percentage must be >= 0")
    @DecimalMax(value = "100", message = "Emergency percentage must be <= 100")
    private BigDecimal emergencyPercent;

    @NotNull(message = "Gold percentage is required")
    @DecimalMin(value = "0", message = "Gold percentage must be >= 0")
    @DecimalMax(value = "100", message = "Gold percentage must be <= 100")
    private BigDecimal goldPercent;

    @NotNull(message = "ETF percentage is required")
    @DecimalMin(value = "0", message = "ETF percentage must be >= 0")
    @DecimalMax(value = "100", message = "ETF percentage must be <= 100")
    private BigDecimal etfPercent;
}
```

**File: features/master/dto/request/InvestmentAllocationMasterUpdateRequest.java**
```java
package com.minhnhat.demo.features.master.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAllocationMasterUpdateRequest {
    @NotNull(message = "Stock percentage is required")
    @DecimalMin(value = "0", message = "Stock percentage must be >= 0")
    @DecimalMax(value = "100", message = "Stock percentage must be <= 100")
    private BigDecimal stockPercent;

    @NotNull(message = "Emergency percentage is required")
    @DecimalMin(value = "0", message = "Emergency percentage must be >= 0")
    @DecimalMax(value = "100", message = "Emergency percentage must be <= 100")
    private BigDecimal emergencyPercent;

    @NotNull(message = "Gold percentage is required")
    @DecimalMin(value = "0", message = "Gold percentage must be >= 0")
    @DecimalMax(value = "100", message = "Gold percentage must be <= 100")
    private BigDecimal goldPercent;

    @NotNull(message = "ETF percentage is required")
    @DecimalMin(value = "0", message = "ETF percentage must be >= 0")
    @DecimalMax(value = "100", message = "ETF percentage must be <= 100")
    private BigDecimal etfPercent;
}
```

### Step 3: Create Response DTO

**File: features/master/dto/response/InvestmentAllocationMasterResponse.java**
```java
package com.minhnhat.demo.features.master.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentAllocationMasterResponse {
    private String code;
    private BigDecimal stockPercent;
    private BigDecimal emergencyPercent;
    private BigDecimal goldPercent;
    private BigDecimal etfPercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Step 4: Create Controller

**File: features/master/controller/InvestmentAllocationMasterController.java**
```java
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
```

### Step 5: Update messages.properties

Add these lines to `src/main/resources/messages.properties`:

```properties
allocation.master.create.info.001=Tạo cấu hình phân bổ thành công
allocation.master.update.info.001=Cập nhật cấu hình phân bổ thành công
allocation.master.delete.info.001=Xóa cấu hình phân bổ thành công
allocation.master.getAll.info.001=Lấy danh sách cấu hình phân bổ thành công
allocation.master.getById.info.001=Lấy chi tiết cấu hình phân bổ thành công
allocation.master.error.notFound=Cấu hình phân bổ không tồn tại
allocation.master.error.duplicateCode=Mã cấu hình đã tồn tại
allocation.master.error.invalidPercentages=Tổng phần trăm phải bằng 100
```

## API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | /api/v1/allocation-master | Create new allocation master |
| GET | /api/v1/allocation-master | Get all allocation masters |
| GET | /api/v1/allocation-master/{code} | Get by code |
| PUT | /api/v1/allocation-master/{code} | Update allocation master |
| DELETE | /api/v1/allocation-master/{code} | Delete allocation master |

## Testing

```bash
# Create
curl -X POST http://localhost:8080/api/v1/allocation-master \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CONSERVATIVE",
    "stockPercent": 10,
    "emergencyPercent": 50,
    "goldPercent": 20,
    "etfPercent": 20
  }'

# Get all
curl http://localhost:8080/api/v1/allocation-master

# Get by code
curl http://localhost:8080/api/v1/allocation-master/SAFE

# Update
curl -X PUT http://localhost:8080/api/v1/allocation-master/SAFE \
  -H "Content-Type: application/json" \
  -d '{
    "stockPercent": 25,
    "emergencyPercent": 35,
    "goldPercent": 30,
    "etfPercent": 10
  }'

# Delete
curl -X DELETE http://localhost:8080/api/v1/allocation-master/SAFE
```

## Notes
- Percentages must sum to exactly 100.00
- Each percentage must be between 0 and 100
- Code is the primary key and cannot be changed
- Timestamps (createdAt, updatedAt) are automatically managed by BaseEntity

