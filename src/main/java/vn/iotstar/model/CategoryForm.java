package vn.iotstar.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public class CategoryForm {
    @Positive(message = "Mã danh mục không hợp lệ.")
    private Long categoryId;
    @NotBlank(message = "Vui lòng nhập tên danh mục.")
    @Size(max = 200, message = "Tên danh mục tối đa 200 ký tự.")
    private String name;
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.strip(); }
}
