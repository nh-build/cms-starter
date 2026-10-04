/*
 * 관리자 공통: 드래그 순서 변경 + 표시/숨김 토글 (섹션, 메뉴 등에서 재사용).
 *
 * <ul data-sortable data-order-url="/admin/xxx/order" data-visible-url="/admin/xxx/-ID-/visible">
 *   <li data-id="1"> <span class="handle">…</span> … <input type="checkbox" class="toggle" data-id="1"> </li>
 * </ul>
 * 순서 저장: POST { ids: [...] }, 토글 저장: POST { visible: true|false }  (CSRF 토큰은 meta 에서 읽는다)
 */
(function () {
    var token = document.querySelector('meta[name="_csrf"]');
    var header = document.querySelector('meta[name="_csrf_header"]');
    var status = document.getElementById('saveStatus');

    function flash(text, ok) {
        if (!status) return;
        status.textContent = text;
        status.hidden = false;
        status.style.background = ok ? '' : '#a13a37';
        clearTimeout(flash.t);
        flash.t = setTimeout(function () { status.hidden = true; }, 1800);
    }

    function send(url, body) {
        var headers = { 'Content-Type': 'application/json' };
        if (token && header) headers[header.content] = token.content;
        return fetch(url, { method: 'POST', headers: headers, body: JSON.stringify(body) }).then(function (r) {
            if (!r.ok) throw new Error('save failed');
        });
    }

    window.adminFlash = flash;
    window.adminPost = send;

    document.querySelectorAll('[data-sortable]').forEach(function (list) {
        var orderUrl = list.dataset.orderUrl;
        var visibleUrl = list.dataset.visibleUrl;

        if (window.Sortable && orderUrl) {
            new Sortable(list, {
                handle: '.handle',
                animation: 150,
                ghostClass: 'sortable-ghost',
                onEnd: function () {
                    var ids = Array.prototype.map.call(list.children, function (li) { return Number(li.dataset.id); });
                    send(orderUrl, { ids: ids })
                        .then(function () { flash('순서를 저장했습니다.', true); })
                        .catch(function () { flash('저장에 실패했습니다. 새로고침 후 다시 시도하세요.', false); });
                }
            });
        }

        if (visibleUrl) {
            list.querySelectorAll('.toggle').forEach(function (cb) {
                cb.addEventListener('change', function () {
                    send(visibleUrl.replace('-ID-', cb.dataset.id), { visible: cb.checked })
                        .then(function () { flash(cb.checked ? '표시로 변경했습니다.' : '숨김으로 변경했습니다.', true); })
                        .catch(function () { cb.checked = !cb.checked; flash('저장에 실패했습니다.', false); });
                });
            });
        }
    });
})();
