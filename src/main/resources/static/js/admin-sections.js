(function () {
    var list = document.getElementById('sectionList');
    var status = document.getElementById('saveStatus');
    if (!list) return;

    var token = document.querySelector('meta[name="_csrf"]').content;
    var header = document.querySelector('meta[name="_csrf_header"]').content;

    function flash(text, ok) {
        status.textContent = text;
        status.hidden = false;
        status.style.background = ok ? '' : '#a13a37';
        clearTimeout(flash.t);
        flash.t = setTimeout(function () { status.hidden = true; }, 1800);
    }

    function send(url, body) {
        var headers = { 'Content-Type': 'application/json' };
        headers[header] = token;
        return fetch(url, { method: 'POST', headers: headers, body: JSON.stringify(body) }).then(function (r) {
            if (!r.ok) throw new Error('save failed');
        });
    }

    new Sortable(list, {
        handle: '.handle',
        animation: 150,
        ghostClass: 'sortable-ghost',
        onEnd: function () {
            var ids = Array.prototype.map.call(list.children, function (li) { return Number(li.dataset.id); });
            send('/admin/sections/order', { ids: ids })
                .then(function () { flash('순서를 저장했습니다.', true); })
                .catch(function () { flash('저장에 실패했습니다. 새로고침 후 다시 시도하세요.', false); });
        }
    });

    list.querySelectorAll('.toggle').forEach(function (cb) {
        cb.addEventListener('change', function () {
            send('/admin/sections/' + cb.dataset.id + '/visible', { visible: cb.checked })
                .then(function () { flash(cb.checked ? '표시로 변경했습니다.' : '숨김으로 변경했습니다.', true); })
                .catch(function () { cb.checked = !cb.checked; flash('저장에 실패했습니다.', false); });
        });
    });
})();
