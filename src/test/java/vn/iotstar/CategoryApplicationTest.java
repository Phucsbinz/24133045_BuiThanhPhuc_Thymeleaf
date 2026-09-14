package vn.iotstar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryApplicationTest {
    @Autowired MockMvc mvc;
    @Autowired CategoryRepository repository;
    @BeforeEach void reset() { repository.deleteAll(); }

    @Test void crudAndVietnameseRoundTrip() throws Exception {
        mvc.perform(post("/admin/categories/saveOrUpdate").param("name", "  Sách tiếng Việt  "))
            .andExpect(status().is3xxRedirection()).andExpect(flash().attribute("message", "Đã thêm danh mục mới."));
        Category saved = repository.findAll().getFirst();
        assertThat(saved.getName()).isEqualTo("Sách tiếng Việt");
        mvc.perform(get("/admin/categories/" + saved.getCategoryId())).andExpect(status().isOk()).andExpect(content().string(containsString("Sách tiếng Việt")));
        mvc.perform(get("/admin/categories/edit/" + saved.getCategoryId())).andExpect(status().isOk());
        mvc.perform(post("/admin/categories/saveOrUpdate").param("categoryId", saved.getCategoryId().toString()).param("name", "Sách mới"))
            .andExpect(status().is3xxRedirection());
        assertThat(repository.findById(saved.getCategoryId()).orElseThrow().getName()).isEqualTo("Sách mới");
        mvc.perform(get("/admin/categories/delete/" + saved.getCategoryId())).andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/admin/categories/delete/" + saved.getCategoryId())).andExpect(status().is3xxRedirection());
        assertThat(repository.count()).isZero();
    }
    @Test void validationRetainsFormAndDoesNotWrite() throws Exception {
        for (String name : new String[]{"", "   ", "x".repeat(201)}) {
            mvc.perform(post("/admin/categories/saveOrUpdate").param("name", name))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("category", "name"))
                .andExpect(view().name("categories/form"));
        }
        assertThat(repository.count()).isZero();
    }
    @Test void searchPaginationAndBoundaryInputs() throws Exception {
        IntStream.rangeClosed(1, 12).forEach(n -> repository.save(new Category("Book " + String.format("%02d", n))));
        repository.save(new Category("Khác"));
        String html = mvc.perform(get("/admin/categories").param("name", "bOoK").param("page", "2").param("size", "5"))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Book 06")))
            .andExpect(content().string(not(containsString("Book 01"))))
            .andReturn().getResponse().getContentAsString();
        assertThat(html).contains("name=bOoK").contains("page=3");
        mvc.perform(get("/admin/categories").param("name", "Book").param("page", "999999999"))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Book 12")));
        for (String page : new String[]{"-1", "abc", "99999999999999999"}) {
            mvc.perform(get("/admin/categories").param("page", page).param("size", "0"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Book 01")));
        }
        mvc.perform(get("/admin/categories").param("name", "missing"))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Không tìm thấy danh mục")));
    }
    @Test void layoutAndEscaping() throws Exception {
        repository.save(new Category("<script>alert(1)</script>"));
        String html = mvc.perform(get("/admin/categories")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("&lt;script&gt;").doesNotContain("<script>alert(1)</script>");
        assertThat(html.split("class=\"site-header\"", -1)).hasSize(2);
        assertThat(html.split("class=\"site-footer\"", -1)).hasSize(2);
        assertThat(html).contains("24133045").contains("/images/student.jpg");
        mvc.perform(get("/admin/categories/add")).andExpect(status().isOk());
    }
    @Test void missingIdsCannotCreateOrDeleteOtherRows() throws Exception {
        mvc.perform(get("/admin/categories/999999")).andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("error"));
        mvc.perform(get("/admin/categories/edit/999999")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/admin/categories/saveOrUpdate").param("categoryId", "999999").param("name", "Unknown"))
            .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("error"));
        mvc.perform(post("/admin/categories/delete/999999")).andExpect(status().is3xxRedirection());
        assertThat(repository.count()).isZero();
    }
    @Test void emptyDatabaseAndDeletionOfLastPage() throws Exception {
        mvc.perform(get("/admin/categories").param("page", "99")).andExpect(status().isOk())
            .andExpect(content().string(containsString("Chưa có danh mục nào")));
        IntStream.rangeClosed(1, 6).forEach(n -> repository.save(new Category("Item " + n)));
        Category last = repository.findAll().getLast();
        mvc.perform(post("/admin/categories/delete/" + last.getCategoryId())).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin/categories").param("page", "2")).andExpect(status().isOk())
            .andExpect(content().string(containsString("Item 1")));
    }
}
