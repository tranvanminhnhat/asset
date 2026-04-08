# Dashboard Screen

## Goal
Giúp người dùng theo dõi tổng quan danh mục đầu tư, hiệu suất từng tài sản và tổng tài sản hiện tại để đưa ra quyết định đầu tư.

## Sections

### 1. Hero Banner
- tiêu đề giới thiệu nền tảng đầu tư
- mô tả ngắn
- CTA button: Bắt đầu ngay
- image minh họa tăng trưởng tài sản

### 2. Portfolio List
Hiển thị danh sách các danh mục đầu tư:
- Quỹ khẩn cấp
- Vàng
- Quỹ ETF
- Cổ phiếu

Mỗi card gồm:
- tên danh mục
- giá mua
- giá hiện tại
- lợi nhuận
- % tăng trưởng
- button xem chi tiết

### 3. Total Asset Summary
Khối tổng tài sản gồm:
- tổng giá trị hiện tại
- tổng giá mua
- lợi nhuận tổng
- % tăng trưởng tổng
- button xem báo cáo chi tiết

## Actions
- click Bắt đầu ngay
- click Xem chi tiết từng portfolio
- click Xem báo cáo chi tiết
- refresh dữ liệu dashboard

## Edge Cases
- chưa có danh mục đầu tư
- lợi nhuận âm
- API loading chậm
- API timeout
- dữ liệu bị null