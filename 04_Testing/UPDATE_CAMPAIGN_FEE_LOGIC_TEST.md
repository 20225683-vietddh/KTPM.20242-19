# 💰 LOGIC CẬP NHẬT ĐỢT THU PHÍ - CAMPAIGN FEE UPDATE

## 📋 Tổng quan
Hệ thống cập nhật đợt thu phí với logic thông minh, xử lý các trường hợp phức tạp và đảm bảo tính nhất quán của dữ liệu.

---

## 🎯 CÁC TRƯỜNG HỢP CẬP NHẬT

### 📅 **Trường hợp 1: Cập nhật thời gian (Start Date / End Date)**

#### Kịch bản 1.1: Gia hạn thời gian (Extend)
```
Trước: 01/01/2024 - 31/01/2024
Sau:  01/01/2024 - 28/02/2024 (gia hạn thêm 1 tháng)

✅ LOGIC:
- Cho phép cập nhật
- Không ảnh hưởng đến dữ liệu thanh toán hiện tại
- Thông báo: "Đã gia hạn đợt thu đến 28/02/2024"
```

#### Kịch bản 1.2: Rút ngắn thời gian (Shorten)
```
Trước: 01/01/2024 - 31/03/2024
Sau:  01/01/2024 - 31/01/2024 (rút ngắn 2 tháng)

⚠️ CẢNH BÁO:
- Kiểm tra có thanh toán nào sau ngày kết thúc mới không
- Nếu có → Hiển thị cảnh báo + yêu cầu xác nhận
- Nếu không → Cho phép cập nhật
```

#### Kịch bản 1.3: Thay đổi ngày bắt đầu (Risky!)
```
Trước: 01/01/2024 - 31/03/2024
Sau:  15/01/2024 - 31/03/2024 (dời ngày bắt đầu)

🚨 NGUY HIỂM:
- Có thể có thanh toán trước ngày bắt đầu mới
- Bắt buộc hiển thị cảnh báo nghiêm trọng
- Yêu cầu xác nhận 2 lần
```

### 💵 **Trường hợp 2: Cập nhật số tiền (Amount)**

#### Kịch bản 2.1: Tăng số tiền
```
Trước: 100,000 VND
Sau:  150,000 VND

✅ LOGIC:
- Cho phép cập nhật
- Cập nhật tất cả bản ghi chưa thanh toán
- Giữ nguyên các bản ghi đã thanh toán
- Thông báo: "Đã cập nhật phí cho X hộ khẩu chưa thanh toán"
```

#### Kịch bản 2.2: Giảm số tiền
```
Trước: 150,000 VND
Sau:  100,000 VND

⚠️ CẢNH BÁO:
- Kiểm tra có ai đã thanh toán số tiền cao hơn không
- Nếu có → Cảnh báo + tùy chọn hoàn tiền
- Cập nhật các bản ghi chưa thanh toán
```

### 📝 **Trường hợp 3: Cập nhật mô tả (Description)**

#### Kịch bản 3.1: Thay đổi mô tả
```
Trước: "Thu phí quản lý tháng 1"
Sau:  "Thu phí quản lý + bảo trì thang máy tháng 1"

✅ LOGIC:
- Luôn cho phép cập nhật
- Không ảnh hưởng đến dữ liệu thanh toán
- Cập nhật ngay lập tức
```

### 🏠 **Trường hợp 4: Thay đổi danh sách hộ khẩu áp dụng**

#### Kịch bản 4.1: Thêm hộ khẩu mới
```
Trước: Áp dụng cho 50 hộ khẩu
Sau:  Áp dụng cho 55 hộ khẩu (thêm 5 hộ mới)

✅ LOGIC:
- Tạo bản ghi mới cho 5 hộ khẩu được thêm
- Trạng thái: "Chưa thanh toán"
- Thông báo: "Đã thêm 5 hộ khẩu vào đợt thu"
```

#### Kịch bản 4.2: Loại bỏ hộ khẩu
```
Trước: Áp dụng cho 50 hộ khẩu
Sau:  Áp dụng cho 45 hộ khẩu (bỏ 5 hộ)

⚠️ CẢNH BÁO:
- Kiểm tra 5 hộ bị loại bỏ đã thanh toán chưa
- Nếu chưa thanh toán → Xóa bản ghi
- Nếu đã thanh toán → Cảnh báo + tùy chọn hoàn tiền
```

### 💳 **Trường hợp 5: Thay đổi danh sách khoản thu (FEE TYPES) - PHỨC TẠP NHẤT!**

#### Kịch bản 5.1: Thêm khoản thu mới
```
Trước: [Phí quản lý, Phí vệ sinh, Phí bảo vệ]
Sau:  [Phí quản lý, Phí vệ sinh, Phí bảo vệ, Phí thang máy]

✅ LOGIC:
- Thêm "Phí thang máy" cho TẤT CẢ hộ khẩu trong đợt thu
- Trạng thái: "Chưa thanh toán" cho khoản thu mới
- Giữ nguyên trạng thái các khoản thu cũ
- Thông báo: "Đã thêm khoản thu 'Phí thang máy' cho tất cả hộ khẩu"
```

#### Kịch bản 5.2: Xóa khoản thu (NGUY HIỂM!)
```
Trước: [A, B, C, D]
Sau:  [A, B]
→ Khoản thu C và D bị xóa

🚨 LOGIC KIỂM TRA:
1. Kiểm tra có hộ nào đã thanh toán C hoặc D chưa?
2. Nếu CÓ → ❌ TỪ CHỐI cập nhật
3. Nếu KHÔNG → ✅ Cho phép xóa
```

#### Kịch bản 5.3: Thay thế khoản thu (CỰC KỲ PHỨC TẠP!)
```
Trước: [A, B, C, D]
Sau:  [A, B, E, F]
→ Xóa: [C, D], Thêm: [E, F]

🧠 LOGIC THÔNG MINH:
1. Phân tích thay đổi:
   - Giữ nguyên: [A, B]
   - Xóa: [C, D] 
   - Thêm: [E, F]

2. Kiểm tra xung đột:
   - Có hộ nào đã thanh toán C? → Nếu có: BLOCK
   - Có hộ nào đã thanh toán D? → Nếu có: BLOCK

3. Nếu không có xung đột:
   - Xóa tất cả bản ghi của C và D
   - Thêm bản ghi mới cho E và F (tất cả hộ khẩu)
   - Giữ nguyên A và B
```

#### Kịch bản 5.4: Thay đổi thông tin khoản thu
```
Trước: Phí quản lý - 100,000 VND
Sau:  Phí quản lý - 120,000 VND

⚠️ LOGIC:
- Cập nhật số tiền cho các bản ghi CHƯA thanh toán
- Giữ nguyên số tiền cho các bản ghi ĐÃ thanh toán
- Thông báo: "Đã cập nhật phí cho X hộ khẩu chưa thanh toán"
```

---

## 🔄 QUY TRÌNH CẬP NHẬT CHI TIẾT

### Bước 1: Validation & Analysis
```java
// Phân tích thay đổi
CampaignFeeUpdateAnalysis analysis = analyzeCampaignFeeChanges(oldCampaign, newCampaign);

// Kiểm tra các điều kiện
if (analysis.hasDateChanges()) {
    validateDateChanges(analysis);
}
if (analysis.hasAmountChanges()) {
    validateAmountChanges(analysis);
}
if (analysis.hasHouseholdChanges()) {
    validateHouseholdChanges(analysis);
}
```

### Bước 2: Warning & Confirmation
```java
// Tạo danh sách cảnh báo
List<String> warnings = generateWarnings(analysis);

// Hiển thị cảnh báo nếu cần
if (!warnings.isEmpty()) {
    boolean confirmed = showWarningDialog(warnings);
    if (!confirmed) return false;
}
```

### Bước 3: Execute Update
```java
// Cập nhật theo từng loại thay đổi
if (analysis.hasDateChanges()) {
    updateCampaignDates(analysis);
}
if (analysis.hasAmountChanges()) {
    updateCampaignAmounts(analysis);
}
if (analysis.hasHouseholdChanges()) {
    updateCampaignHouseholds(analysis);
}
```

### Bước 4: Post-Update Actions
```java
// Cập nhật thống kê
updateCampaignStatistics(campaignId);

// Gửi thông báo
notifyAffectedHouseholds(analysis.getAffectedHouseholds());

// Log thay đổi
logCampaignChanges(analysis);
```

---

## 🧪 TEST SCENARIOS

### Test Case 1: Gia hạn thời gian đơn giản
```
INPUT:
- Old End Date: 31/01/2024
- New End Date: 29/02/2024
- Không có thanh toán nào bị ảnh hưởng

EXPECTED:
✅ Cập nhật thành công
✅ Không có cảnh báo
✅ Thông báo: "Đã gia hạn đợt thu đến 29/02/2024"
```

### Test Case 2: Rút ngắn thời gian có xung đột
```
INPUT:
- Old End Date: 31/03/2024
- New End Date: 31/01/2024
- Có 5 thanh toán trong tháng 2-3

EXPECTED:
⚠️ Hiển thị cảnh báo: "5 hộ khẩu đã thanh toán sau ngày kết thúc mới"
⚠️ Yêu cầu xác nhận
✅ Nếu xác nhận → Cập nhật + giữ nguyên thanh toán
```

### Test Case 3: Tăng số tiền
```
INPUT:
- Old Amount: 100,000 VND
- New Amount: 150,000 VND
- 30 hộ chưa thanh toán, 20 hộ đã thanh toán

EXPECTED:
✅ Cập nhật 30 hộ chưa thanh toán → 150,000 VND
✅ Giữ nguyên 20 hộ đã thanh toán → 100,000 VND
✅ Thông báo: "Đã cập nhật phí cho 30 hộ khẩu chưa thanh toán"
```

### Test Case 4: Giảm số tiền có xung đột
```
INPUT:
- Old Amount: 150,000 VND
- New Amount: 100,000 VND
- 10 hộ đã thanh toán 150,000 VND

EXPECTED:
⚠️ Cảnh báo: "10 hộ khẩu đã thanh toán số tiền cao hơn"
⚠️ Tùy chọn: "Hoàn tiền 50,000 VND cho mỗi hộ?"
✅ Cập nhật các hộ chưa thanh toán
```

### Test Case 5: Thêm hộ khẩu mới
```
INPUT:
- Thêm 5 hộ khẩu mới vào đợt thu đang diễn ra
- Campaign Amount: 100,000 VND

EXPECTED:
✅ Tạo 5 bản ghi mới với trạng thái "Chưa thanh toán"
✅ Amount: 100,000 VND cho mỗi hộ
✅ Thông báo: "Đã thêm 5 hộ khẩu vào đợt thu"
```

### Test Case 6: Loại bỏ hộ khẩu đã thanh toán
```
INPUT:
- Loại bỏ 3 hộ khẩu khỏi đợt thu
- 2 hộ đã thanh toán, 1 hộ chưa thanh toán

EXPECTED:
⚠️ Cảnh báo: "2 hộ khẩu đã thanh toán sẽ bị loại bỏ"
⚠️ Tùy chọn: "Hoàn tiền cho 2 hộ khẩu này?"
✅ Xóa 1 bản ghi chưa thanh toán
✅ Giữ hoặc xóa 2 bản ghi đã thanh toán (tùy lựa chọn)
```

### Test Case 7: Thêm khoản thu mới
```
INPUT:
- Thêm "Phí thang máy" cho tất cả hộ khẩu trong đợt thu
- Campaign Amount: 100,000 VND

EXPECTED:
✅ Tạo 5 bản ghi mới với trạng thái "Chưa thanh toán"
✅ Amount: 100,000 VND cho mỗi hộ
✅ Thông báo: "Đã thêm khoản thu 'Phí thang máy' cho tất cả hộ khẩu"
```

### Test Case 8: Xóa khoản thu có xung đột
```
INPUT:
- Xóa "Phí thang máy" khỏi đợt thu
- 5 hộ khẩu đã thanh toán "Phí thang máy"

EXPECTED:
❌ TỪ CHỐI cập nhật
❌ Thông báo lỗi: "Không thể xóa 'Phí thang máy' vì đã có 5 hộ khẩu thanh toán!"
❌ Gợi ý: "Vui lòng hoàn tiền trước khi xóa khoản thu này"
```

### Test Case 9: Xóa khoản thu an toàn
```
INPUT:
- Xóa "Phí thang máy" khỏi đợt thu
- 0 hộ khẩu đã thanh toán "Phí thang máy"

EXPECTED:
✅ Xóa thành công tất cả bản ghi "Phí thang máy"
✅ Thông báo: "Đã xóa khoản thu 'Phí thang máy' khỏi đợt thu"
```

### Test Case 10: Thay thế khoản thu phức tạp
```
INPUT:
- Trước: [Phí quản lý, Phí vệ sinh, Phí bảo vệ, Phí thang máy]
- Sau:  [Phí quản lý, Phí vệ sinh, Phí điện, Phí nước]
- Xóa: [Phí bảo vệ, Phí thang máy]
- Thêm: [Phí điện, Phí nước]
- Có 3 hộ đã thanh toán "Phí bảo vệ"

EXPECTED:
❌ TỪ CHỐI cập nhật
❌ Thông báo: "Không thể xóa 'Phí bảo vệ' vì đã có 3 hộ khẩu thanh toán!"
❌ Danh sách khoản thu không thay đổi
```

### Test Case 11: Thay thế khoản thu thành công
```
INPUT:
- Trước: [Phí quản lý, Phí vệ sinh, Phí bảo vệ, Phí thang máy]
- Sau:  [Phí quản lý, Phí vệ sinh, Phí điện, Phí nước]
- Xóa: [Phí bảo vệ, Phí thang máy] (0 hộ đã thanh toán)
- Thêm: [Phí điện, Phí nước]

EXPECTED:
✅ Xóa tất cả bản ghi "Phí bảo vệ" và "Phí thang máy"
✅ Tạo bản ghi mới "Phí điện" và "Phí nước" cho tất cả hộ khẩu
✅ Giữ nguyên "Phí quản lý" và "Phí vệ sinh"
✅ Thông báo: "Đã cập nhật danh sách khoản thu thành công"
```

---

## 🔧 IMPLEMENTATION DETAILS

### 🆕 Classes cần tạo
```java
// Phân tích thay đổi
public class CampaignFeeUpdateAnalysis {
    private boolean hasDateChanges;
    private boolean hasAmountChanges;
    private boolean hasHouseholdChanges;
    private boolean hasFeeTypeChanges;
    private List<PaymentRecord> affectedPayments;
    private List<Household> addedHouseholds;
    private List<Household> removedHouseholds;
    private List<Fee> addedFeeTypes;
    private List<Fee> removedFeeTypes;
    private List<Fee> unchangedFeeTypes;
    private Map<Fee, List<PaymentRecord>> conflictingPayments;
}

// Service xử lý cập nhật
public class CampaignFeeUpdateService {
    public CampaignFeeUpdateAnalysis analyzeCampaignFeeChanges(CampaignFee old, CampaignFee new);
    public List<String> generateWarnings(CampaignFeeUpdateAnalysis analysis);
    public boolean updateCampaignFeeSafely(CampaignFeeUpdateAnalysis analysis);
    
    public boolean validateFeeTypeChanges(CampaignFeeUpdateAnalysis analysis);
    public Map<Fee, List<PaymentRecord>> findConflictingPayments(List<Fee> removedFees);
    public boolean canRemoveFeeTypes(List<Fee> feeTypes);
    public void addFeeTypesToAllHouseholds(List<Fee> newFeeTypes, int campaignId);
    public void removeFeeTypesFromCampaign(List<Fee> feeTypes, int campaignId);
}

// CLASS MỚI: Phân tích thay đổi Fee Types
public class FeeTypeChangeAnalyzer {
    public static class FeeTypeChanges {
        private List<Fee> added;
        private List<Fee> removed;
        private List<Fee> unchanged;
        private List<Fee> modified;
    }
    
    public FeeTypeChanges analyzeFeeTypeChanges(List<Fee> oldFees, List<Fee> newFees);
    public boolean hasConflicts(List<Fee> removedFees);
    public Map<Fee, Integer> countPaymentsByFeeType(List<Fee> feeTypes);
}
```

### 🎨 UI Components
```java
// Dialog cảnh báo đặc biệt
public class CampaignFeeUpdateWarningDialog extends Alert {
    // Hiển thị danh sách cảnh báo
    // Tùy chọn hoàn tiền
    // Xác nhận nhiều cấp độ
    // MỚI: Hiển thị xung đột khoản thu
    public void showFeeTypeConflicts(Map<Fee, List<PaymentRecord>> conflicts);
    public boolean confirmFeeTypeRemoval(List<Fee> removedFees);
}

// Form cập nhật với validation real-time
public class UpdateCampaignFeeFormHandler {
    // Real-time validation
    // Preview thay đổi
    // Confirmation workflow
    // MỚI: Fee Type Management
    private FeeTypeSelector feeTypeSelector;
    private FeeTypeConflictPreview conflictPreview;
    
    public void validateFeeTypeChanges();
    public void previewFeeTypeImpact();
    public void handleFeeTypeConflicts();
}

// COMPONENT MỚI: Quản lý khoản thu
public class FeeTypeSelector extends VBox {
    private ObservableList<Fee> availableFees;
    private ObservableList<Fee> selectedFees;
    
    public void loadAvailableFees();
    public void updateSelectedFees(List<Fee> fees);
    public List<Fee> getSelectedFees();
    public FeeTypeChanges getChanges();
}

// COMPONENT MỚI: Preview xung đột
public class FeeTypeConflictPreview extends VBox {
    public void showConflicts(Map<Fee, List<PaymentRecord>> conflicts);
    public void showImpactSummary(FeeTypeChanges changes);
    public void highlightRiskyChanges();
}
```

---

## 🚀 READY FOR IMPLEMENTATION!

Logic cập nhật đợt thu phí đã được thiết kế với:
- ✅ **Phân tích thông minh** - Detect tất cả loại thay đổi
- ✅ **Cảnh báo đa cấp** - Warning phù hợp với mức độ rủi ro
- ✅ **Xử lý xung đột** - Handle các trường hợp phức tạp
- ✅ **Data Integrity** - Đảm bảo tính nhất quán dữ liệu
- ✅ **User Experience** - Thông báo rõ ràng, tùy chọn linh hoạt

**Sẵn sàng để implement logic cập nhật đợt thu phí thông minh! 🎯** 