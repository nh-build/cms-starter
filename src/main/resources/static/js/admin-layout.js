// 메인 레이아웃: 썸네일/아이콘을 누르면 즉시 저장하고, 섹션 제목은 값이 바뀔 때만 저장 버튼을 보여준다.
(function () {
    document.querySelectorAll('form[data-autosave]').forEach(function (form) {
        var url = form.getAttribute('action');
        form.addEventListener('change', function (e) {
            if (!e.target.matches('input[type="radio"]')) return;
            var body = new URLSearchParams(new FormData(form));
            fetch(url, { method: 'POST', headers: { 'X-Requested-With': 'fetch' }, body: body })
                .then(function (r) { if (!r.ok) throw new Error(); })
                .then(function () { window.adminFlash && window.adminFlash('저장했습니다.', true); })
                .catch(function () { window.adminFlash && window.adminFlash('저장에 실패했습니다. 새로고침 후 다시 시도하세요.', false); });
        });
    });

    document.querySelectorAll('.title-form').forEach(function (form) {
        var input = form.querySelector('.title-input');
        var btn = form.querySelector('.title-save');
        var original = input.value;
        input.addEventListener('input', function () { btn.hidden = input.value === original; });
    });
})();
