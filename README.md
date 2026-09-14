# Category Studio · Thymeleaf

**Sinh viên:** Bùi Thanh Phúc · **MSSV:** 24133045  
**Môn học:** Lập trình Web · HCMUTE

**GitHub:** https://github.com/Phucsbinz/24133045_BuiThanhPhuc_Thymeleaf

Ứng dụng Spring Boot quản lý danh mục với CRUD, tìm kiếm theo tên và phân trang tại database. Giao diện Thymeleaf dùng Layout Dialect để tái sử dụng header, content và footer.

## Công nghệ

- Java 21, Maven Wrapper 3.9.16, Spring Boot 3.3.13.
- Spring MVC, Spring Data JPA, Jakarta Validation, MySQL 8.
- Thymeleaf và `nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect` (phiên bản do Spring Boot quản lý).
- Bootstrap 5.3.3 được lưu trong project, không cần CDN khi chạy.
- JUnit 5, MockMvc, H2 chỉ dùng cho kiểm thử tự động.

## Chạy ứng dụng

1. Cài JDK 21 và MySQL 8, đặt `JAVA_HOME` trỏ đến thư mục JDK 21. Trong STS/Eclipse, chọn JDK 21 cho project và Maven runtime.
2. Clone repository, mở terminal tại thư mục project.
3. Tạo database bằng MySQL Workbench hoặc MySQL CLI:

```sql
SOURCE D:/duong-dan-project/database/schema.sql;
SOURCE D:/duong-dan-project/database/sample-data.sql;
```

Thay đường dẫn bằng vị trí clone thực tế. Import `sample-data.sql` **một lần** để có 20 danh mục; chạy lặp sẽ thêm dữ liệu trùng. Script không xóa dữ liệu đang có. Schema dùng database riêng `thymeleaf_category`.

4. Cấu hình và chạy trong PowerShell:

```powershell
$env:JAVA_HOME = 'C:\duong-dan\jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'mat-khau-MySQL-cua-ban'
.\mvnw.cmd clean verify
java -jar target/24133045_BuiThanhPhuc_Thymeleaf-1.0.0.jar
```

Trên macOS/Linux dùng `./mvnw clean verify` sau khi đặt các biến môi trường tương ứng. Lần build đầu cần Internet để tải Maven và dependencies. Kiểm thử tự động không cần MySQL.

5. Mở **http://localhost:8088/admin/categories**. Có thể đổi cổng qua biến `PORT` hoặc chuỗi kết nối qua `DB_URL`.

Không commit mật khẩu thật. `ddl-auto=validate` chỉ kiểm tra schema; ứng dụng không tự xóa/tạo lại bảng khi khởi động. Database lưu dữ liệu qua các lần chạy.

## Ảnh sinh viên

Header đã dùng ảnh cá nhân của sinh viên tại `src/main/resources/static/images/student.jpg`. Ảnh được đóng gói trong JAR và hiển thị mặc định khi clone/build project. Có thể đổi đường dẫn bằng biến môi trường:

```powershell
$env:STUDENT_PHOTO = '/images/student.jpg'
```

Build lại sau khi thay file ảnh. Footer có họ tên và MSSV.

## Chức năng và URL

| Phương thức | URL | Chức năng |
|---|---|---|
| GET | `/` | Chuyển đến danh sách |
| GET | `/admin/categories?name=sách&page=1&size=5` | Tìm kiếm và phân trang |
| GET | `/admin/categories/{id}` | Xem chi tiết |
| GET | `/admin/categories/add` | Form thêm |
| GET | `/admin/categories/edit/{id}` | Form sửa |
| POST | `/admin/categories/saveOrUpdate` | Lưu dữ liệu hợp lệ |
| POST | `/admin/categories/delete/{id}` | Xóa danh mục |

- Category có mã `Long` tự tăng và tên tối đa 200 ký tự. Trim khoảng trắng hai đầu; không chấp nhận tên rỗng. Cho phép tên trùng.
- Tìm một phần tên, không phân biệt hoa/thường. Từ khóa rỗng trả tất cả; `%` và `_` được Spring Data xử lý như ký tự tìm kiếm thông thường.
- Trang bắt đầu từ 1, mặc định 5 mục; chọn 5/10/20. Sắp xếp theo tên rồi mã. Giữ từ khóa khi đổi trang hoặc đổi số mục.
- Tham số phân trang sai dùng giá trị mặc định; trang vượt giới hạn hiển thị trang cuối. Danh sách rỗng có thông báo riêng.
- Form sai giữ dữ liệu và hiện lỗi tại trường. Lưu/xóa dùng POST và redirect với flash message, tránh gửi lại form khi refresh.
- Xóa có hộp thoại xác nhận trong trình duyệt. Mã đã bị xóa/không tồn tại có thông báo rõ ràng.
- Phạm vi bài tập không có đăng nhập, User, upload ảnh Category hoặc i18n; ứng dụng dành cho chạy và chấm bài cục bộ.

## Cấu trúc và layout

```text
src/main/java/vn/iotstar/
  controller/   # Nhận request, form validation, view và flash message
  service/      # CRUD, transaction, xử lý giới hạn phân trang
  repository/   # Spring Data JPA truy vấn tên và Pageable
  entity/       # Category ánh xạ bảng categories
  model/        # CategoryForm
src/main/resources/
  templates/layouts/main.html  # Khung header-content-footer
  templates/fragments/        # Header có ảnh, footer có thông tin sinh viên
  templates/categories/       # list.html, form.html, detail.html
  static/                     # CSS, JavaScript, ảnh, Bootstrap
database/                     # Schema và 20 danh mục mẫu
```

Trang con dùng `layout:decorate="~{layouts/main}"` và `layout:fragment="content"`; layout dùng `th:replace` để chèn header/footer. Danh sách dùng `th:each`, `th:if`, `th:text`, `th:href`; form dùng `th:object`, `th:field`, `th:errors`. Không phụ thuộc JSP, JSTL hoặc SiteMesh.

Phần tổ chức Repository/Service/Controller tham khảo bài BT05; bài này có project và database riêng. Bài giảng tham khảo: `11_Thymeleaf.pdf` (ThS. Nguyễn Hữu Trung).

## Kiểm thử và nộp bài

`./mvnw clean verify` chạy 6 kịch bản tích hợp: CRUD/tiếng Việt, validation, tìm kiếm/phân trang/đầu vào bất thường, layout/escape HTML, mã không tồn tại, database rỗng/xóa mục cuối trang. MockMvc render template thật với Thymeleaf Layout Dialect.

Đã kiểm tra: `mvn clean verify` thành công (6 tests, 0 failures/errors), JAR chạy với MySQL 8.0.44 và HTTP CRUD/tiếng Việt/layout/tài nguyên tĩnh thành công. Đã bổ sung ảnh cá nhân thật; chưa kiểm tra bố cục bằng trình duyệt.

Trước khi nộp: mở danh sách và form trên trình duyệt máy tính/điện thoại, kiểm tra header-footer và xác nhận xóa. Chụp màn hình danh sách, form và tìm kiếm để minh họa README nếu cần. Kiểm tra repository public truy cập được, rồi nộp link GitHub vào đúng bài tập UTeXLMS.

Bootstrap phân phối theo MIT License; thông tin bản quyền giữ trong đầu file CSS.
