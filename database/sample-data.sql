USE thymeleaf_category;
-- Import once, after schema.sql. This script does not delete existing data.
INSERT INTO categories (name) VALUES
('Sách & văn phòng phẩm'), ('Điện thoại'), ('Máy tính xách tay'), ('Thiết bị âm thanh'),
('Thời trang nam'), ('Thời trang nữ'), ('Giày dép'), ('Túi xách'),
('Đồ gia dụng'), ('Nội thất'), ('Nhà bếp'), ('Chăm sóc sức khỏe'),
('Mỹ phẩm'), ('Thể thao'), ('Du lịch'), ('Đồ chơi'),
('Thực phẩm'), ('Đồ uống'), ('Phụ kiện công nghệ'), ('Cây cảnh');
