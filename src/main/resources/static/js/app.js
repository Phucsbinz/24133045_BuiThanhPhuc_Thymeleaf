document.querySelectorAll('[data-delete-form]').forEach(form => {
  form.addEventListener('submit', event => {
    if (!window.confirm(`Xóa danh mục “${form.dataset.category}”? Thao tác này không thể hoàn tác.`)) event.preventDefault();
  });
});
