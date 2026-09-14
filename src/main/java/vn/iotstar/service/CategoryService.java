package vn.iotstar.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.Category;
import vn.iotstar.model.CategoryForm;
import vn.iotstar.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository repository;
    public CategoryService(CategoryRepository repository) { this.repository = repository; }

    public Page<Category> search(String name, int page, int size) {
        if ((long) (page - 1) * size > Integer.MAX_VALUE) {
            long total = repository.countByNameContainingIgnoreCase(name);
            page = (int) Math.max(1, Math.min(Integer.MAX_VALUE / size, (total + size - 1) / size));
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").ascending().and(Sort.by("categoryId")));
        Page<Category> result = repository.findByNameContainingIgnoreCase(name, pageable);
        if (page > Math.max(1, result.getTotalPages())) {
            return repository.findByNameContainingIgnoreCase(name, pageable.withPage(Math.max(0, result.getTotalPages() - 1)));
        }
        return result;
    }
    public Category get(Long id) { return repository.findById(id).orElseThrow(CategoryNotFoundException::new); }

    @Transactional
    public Category save(CategoryForm form) {
        Category category = form.getCategoryId() == null ? new Category() : get(form.getCategoryId());
        category.setName(form.getName().strip());
        return repository.save(category);
    }
    @Transactional
    public void delete(Long id) { repository.delete(get(id)); }
}
