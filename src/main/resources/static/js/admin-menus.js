// 메뉴 폼: 연결 대상(카테고리/URL) 라디오에 맞춰 입력 필드를 전환한다.
(function () {
    function sync(form) {
        var type = form.querySelector('input[name="targetType"]:checked');
        var isCategory = type && type.value === 'CATEGORY';
        form.querySelectorAll('[data-for="CATEGORY"]').forEach(function (el) { el.hidden = !isCategory; });
        form.querySelectorAll('[data-for="URL"]').forEach(function (el) { el.hidden = isCategory; });
        var cat = form.querySelector('select[name="categoryId"]');
        var url = form.querySelector('input[name="url"]');
        if (cat) cat.required = isCategory;
        if (url) url.required = !isCategory;
    }
    document.querySelectorAll('form[data-menu-form]').forEach(function (form) {
        form.querySelectorAll('input[name="targetType"]').forEach(function (r) {
            r.addEventListener('change', function () { sync(form); });
        });
        sync(form);
    });
})();
