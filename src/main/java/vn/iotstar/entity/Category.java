package vn.iotstar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @Column(nullable = false, length = 200)
    private String name;

    public Category() {}
    public Category(String name) { this.name = name; }
    public Long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
