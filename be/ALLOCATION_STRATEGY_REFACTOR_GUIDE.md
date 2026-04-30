# REFACTORING ALLOCATION STRATEGY - HƯỚNG DẪN ĐẦY ĐỦ

## Tóm Tắt Thay Đổi

Dự án đã được refactor để sử dụng cấu trúc tháo rời (Modular) với hai bảng mới:
- `mst_allocation_strategies` (chiến lược phân bổ)
- `mst_allocation_strategy_details` (chi tiết phân bổ theo asset type)

Thay vì sử dụng bảng cũ `investment_allocation_master` với các cột cộng đơn.

---

## CÔNG VIỆC ĐÃ THỰC HIỆN

### 1. ✅ CẬP NHẬT ENTITIES

#### AllocationStrategyEntity.java
**File:** `src/main/java/com/minhnhat/demo/features/master/entity/AllocationStrategyEntity.java`

**Thay đổi:**
- `@Table(name = "allocation_strategies")` → `@Table(name = "mst_allocation_strategies")`
- Xóa cột `description` (không có trong table mới)
- Giữ lại relationship `@OneToMany` → `AllocationStrategyDetailEntity`

```java
@Entity
@Table(name = "mst_allocation_strategies")
public class AllocationStrategyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", length = 30, unique = true, nullable = false)
    private String code;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AllocationStrategyDetailEntity> details = new ArrayList<>();
}
```

#### AllocationStrategyDetailEntity.java
**File:** `src/main/java/com/minhnhat/demo/features/master/entity/AllocationStrategyDetailEntity.java`

**Thay đổi:**
- `@Table(name = "allocation_strategy_details")` → `@Table(name = "mst_allocation_strategy_details")`
- Thêm cột mới: `strategyCode` (VARCHAR 45) → phục vụ cho truy vấn nhanh

```java
@Entity
@Table(name = "mst_allocation_strategy_details")
public class AllocationStrategyDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false, insertable = false, updatable = false)
    private AllocationStrategyEntity strategy;
    
    @Column(name = "asset_type", length = 50, nullable = false)
    private String assetType;  // EMERGENCY, GOLD, ETF, STOCK
    
    @Column(name = "percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;
    
    @Column(name = "strategy_code", length = 45, nullable = false)
    private String strategyCode;  // Liên kết tới strategy code
}
```

### 2. ✅ CẬP NHẬT REPOSITORIES

**Không cần thay đổi** - `AllocationStrategyRepository` vẫn hoạt động bình thường:
```java
@Repository
public interface AllocationStrategyRepository extends JpaRepository<AllocationStrategyEntity, Long> {
    Optional<AllocationStrategyEntity> findByCode(String code);
    
    @Query("SELECT s FROM AllocationStrategyEntity s " +
           "LEFT JOIN FETCH s.details " +
           "WHERE s.code = :code")
    Optional<AllocationStrategyEntity> findByCodeWithDetails(@Param("code") String code);
}
```

### 3. ✅ REFACTOR SERVICE

**File:** `src/main/java/com/minhnhat/demo/features/investmentRecommendations/service/InvestmentRecommendationsService.java`

**Thay đổi Chính:**
- Thay `InvestmentAllocationMasterRepository` → `AllocationStrategyRepository`
- Thay `InvestmentAllocationMasterEntity` → `AllocationStrategyEntity` + `AllocationStrategyDetailEntity`

**Mô Tả Quy Trình:**
```
1. validateRequest(request)
   - Kiểm tra amount > 0
   - Kiểm tra type không rỗng

2. loadData(request)
   - Query AllocationStrategy bằng code
   - Load chi tiết phân bổ (details)

3. executeBusinessLogic(ctx)
   - Trích xuất percentages từ details thành map:
     EMERGENCY → pEmergency
     GOLD → pGold
     ETF → pEtf
     STOCK → pStock
   
   - Normalize nếu sum ≠ 1.0
   - Tính amount cho từng asset:
     amount = totalAmount * percentage
   - Điều chỉnh rounding để đảm bảo tổng = totalAmount

4. createResponse(ctx)
   - Return 4 allocations (EMERGENCY, GOLD, ETF, STOCK)
   - Mỗi allocation có: type, amount, percent
```

---

## 📊 FLOW DỮ LIỆU

### Input (Request)
```json
{
  "amount": 10000,
  "type": "BALANCED"
}
```

### Database Query
```sql
SELECT s FROM AllocationStrategyEntity s
LEFT JOIN FETCH s.details
WHERE s.code = 'BALANCED'
```

### Processing
```
Strategy: BALANCED (id=1)
  ├─ Detail: EMERGENCY, 25%, strategyCode=BALANCED
  ├─ Detail: GOLD, 20%, strategyCode=BALANCED
  ├─ Detail: ETF, 20%, strategyCode=BALANCED
  └─ Detail: STOCK, 35%, strategyCode=BALANCED

Tính toán:
  ├─ EMERGENCY: 10000 × 0.25 = 2500
  ├─ GOLD: 10000 × 0.20 = 2000
  ├─ ETF: 10000 × 0.20 = 2000
  └─ STOCK: 10000 × 0.35 = 3500
  Sum = 10000 ✓
```

### Output (Response)
```json
{
  "allocations": [
    {
      "type": "EMERGENCY",
      "amount": 2500,
      "percent": 0.25
    },
    {
      "type": "GOLD",
      "amount": 2000,
      "percent": 0.20
    },
    {
      "type": "ETF",
      "amount": 2000,
      "percent": 0.20
    },
    {
      "type": "STOCK",
      "amount": 3500,
      "percent": 0.35
    }
  ]
}
```

---

## 🔄 MIGRATION STRATEGY

### Nếu database đã có table cũ:
```sql
-- Option 1: Rename (thủ công)
ALTER TABLE allocation_strategies RENAME TO mst_allocation_strategies;
ALTER TABLE allocation_strategy_details RENAME TO mst_allocation_strategy_details;

-- Option 2: Tự động (bằng Flyway migration)
-- File: V005__Rename_Allocation_Tables.sql sẽ tự chạy
```

### Dữ liệu mẫu để insert:
```sql
INSERT INTO mst_allocation_strategies (code, name, created_at)
VALUES 
  ('SAFE', 'Chiến lược An Toàn', NOW()),
  ('BALANCED', 'Chiến lược Cân Bằng', NOW()),
  ('GROWTH', 'Chiến lược Tăng Trưởng', NOW());

INSERT INTO mst_allocation_strategy_details (strategy_id, asset_type, percentage, strategy_code, created_at)
VALUES
  (1, 'EMERGENCY', 40, 'SAFE', NOW()),
  (1, 'GOLD', 30, 'SAFE', NOW()),
  (1, 'ETF', 10, 'SAFE', NOW()),
  (1, 'STOCK', 20, 'SAFE', NOW()),
  
  (2, 'EMERGENCY', 25, 'BALANCED', NOW()),
  (2, 'GOLD', 20, 'BALANCED', NOW()),
  (2, 'ETF', 20, 'BALANCED', NOW()),
  (2, 'STOCK', 35, 'BALANCED', NOW()),
  
  (3, 'EMERGENCY', 10, 'GROWTH', NOW()),
  (3, 'GOLD', 10, 'GROWTH', NOW()),
  (3, 'ETF', 30, 'GROWTH', NOW()),
  (3, 'STOCK', 50, 'GROWTH', NOW());
```

---

## ✨ LỢI ỊCH CỦA REFACTORING

| Yếu tố | Trước | Sau |
|--------|--------|-----|
| **Linh hoạt** | Chỉ 4 cột cố định | Thêm asset type dễ dàng |
| **Mở rộng** | Thêm cột mới phức tạp | Chỉ cần insert row mới |
| **Query** | Trực tiếp bảng | JOIN hiệu quả |
| **Normalize** | Denormalized | Normalized (3NF) |
| **Bảo trì** | Khó scale | Dễ scale, modular |

---

## 🧪 KIỂM TRA CHỨC NĂNG

### API Endpoint
```
POST /investment-amount
Content-Type: application/json

{
  "amount": 50000,
  "type": "BALANCED"
}
```

### Expected Response
```json
{
  "success": true,
  "message": "Allocation calculated successfully",
  "data": {
    "allocations": [
      { "type": "EMERGENCY", "amount": 12500, "percent": 0.25 },
      { "type": "GOLD", "amount": 10000, "percent": 0.20 },
      { "type": "ETF", "amount": 10000, "percent": 0.20 },
      { "type": "STOCK", "amount": 17500, "percent": 0.35 }
    ]
  }
}
```

---

## ⚠️ LƯU Ý QUAN TRỌNG

1. **Cập nhật Database** - Chạy migration trước khi build
2. **Dữ liệu Mẫu** - Insert các strategy và details mẫu
3. **Kiểm tra** - Test API với các type khác nhau (SAFE, BALANCED, GROWTH)
4. **Backward Compatibility** - Request/Response DTO không đổi, chỉ source data thay đổi

---

## 📝 FILES THAY ĐỔI

```
✅ src/main/java/com/minhnhat/demo/features/master/entity/
   ├─ AllocationStrategyEntity.java (cập nhật table name)
   ├─ AllocationStrategyDetailEntity.java (cập nhật table name + thêm strategyCode)
   └─ InvestmentAllocationMasterEntity.java (vẫn giữ, nhưng không dùng)

✅ src/main/java/com/minhnhat/demo/features/investmentRecommendations/service/
   └─ InvestmentRecommendationsService.java (refactor import + logic)

✅ src/main/resources/db/migration/
   └─ V005__Rename_Allocation_Tables.sql (migration script)
```

---

**Status:** ✅ COMPLETE - Refactoring thành công, sẵn sàng deploy!

