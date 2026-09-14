package vn.iotstar.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.Category;
import vn.iotstar.model.CategoryForm;
import vn.iotstar.service.*;

@Controller
public class CategoryController {
    private final CategoryService service;
    private final String studentPhoto;
    public CategoryController(CategoryService service, @Value("${app.student-photo}") String studentPhoto) {
        this.service = service; this.studentPhoto = studentPhoto;
    }
    @ModelAttribute("studentPhoto") public String studentPhoto() { return studentPhoto; }
    @GetMapping("/") public String home() { return "redirect:/admin/categories"; }

    @GetMapping("/admin/categories")
    public String list(@RequestParam(defaultValue = "") String name,
                       @RequestParam(defaultValue = "1") String page,
                       @RequestParam(defaultValue = "5") String size, Model model) {
        int pageNumber = positiveInt(page, 1);
        int pageSize = positiveInt(size, 5);
        if (!List.of(5, 10, 20).contains(pageSize)) pageSize = 5;
        Page<Category> result = service.search(name.strip(), pageNumber, pageSize);
        int current = result.getNumber() + 1;
        model.addAttribute("categoryPage", result);
        model.addAttribute("name", name.strip());
        model.addAttribute("pageNumbers", IntStream.rangeClosed(Math.max(1, current - 2), Math.min(result.getTotalPages(), current + 2)).toArray());
        return "categories/list";
    }
    private int positiveInt(String value, int fallback) {
        try { int n = Integer.parseInt(value); return n > 0 ? n : fallback; }
        catch (NumberFormatException e) { return fallback; }
    }
    @GetMapping("/admin/categories/add")
    public String add(Model model) { model.addAttribute("category", new CategoryForm()); return "categories/form"; }
    @GetMapping("/admin/categories/{id}")
    public String detail(@PathVariable Long id, Model model) { model.addAttribute("category", service.get(id)); return "categories/detail"; }
    @GetMapping("/admin/categories/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Category entity = service.get(id);
        CategoryForm form = new CategoryForm(); form.setCategoryId(entity.getCategoryId()); form.setName(entity.getName());
        model.addAttribute("category", form); return "categories/form";
    }
    @PostMapping("/admin/categories/saveOrUpdate")
    public String save(@Valid @ModelAttribute("category") CategoryForm form, BindingResult errors, RedirectAttributes redirect) {
        if (errors.hasErrors()) return "categories/form";
        service.save(form);
        redirect.addFlashAttribute("message", form.getCategoryId() == null ? "Đã thêm danh mục mới." : "Đã cập nhật danh mục.");
        return "redirect:/admin/categories";
    }
    @PostMapping("/admin/categories/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        service.delete(id); redirect.addFlashAttribute("message", "Đã xóa danh mục."); return "redirect:/admin/categories";
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public String missing(RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", "Danh mục không tồn tại hoặc đã bị xóa."); return "redirect:/admin/categories";
    }
}
