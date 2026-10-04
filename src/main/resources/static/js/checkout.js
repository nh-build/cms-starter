(function () {
    var form = document.getElementById('checkoutForm');
    var btn = document.getElementById('payButton');
    var errBox = document.getElementById('payError');
    if (!form || !btn) return;

    var csrfToken = document.querySelector('meta[name="_csrf"]').content;
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    function showError(msg) {
        errBox.textContent = msg;
        errBox.hidden = false;
        btn.disabled = false;
    }

    function post(url, body) {
        var headers = { 'Content-Type': 'application/json' };
        headers[csrfHeader] = csrfToken;
        return fetch(url, { method: 'POST', headers: headers, body: JSON.stringify(body) })
            .then(function (r) { return r.json().then(function (j) { return { ok: r.ok, body: j }; }); });
    }

    btn.addEventListener('click', function () {
        errBox.hidden = true;
        if (!form.reportValidity()) return;
        var data = Object.fromEntries(new FormData(form).entries());
        btn.disabled = true;

        post('/checkout/prepare', data).then(function (res) {
            if (!res.ok) { showError(res.body.message || '주문을 생성할 수 없습니다.'); return; }
            var order = res.body;
            if (typeof IMP === 'undefined') { showError('결제 모듈을 불러오지 못했습니다.'); return; }
            IMP.init(form.dataset.impCode);
            var kakao = data.payMethod === 'KAKAO';
            IMP.request_pay({
                pg: kakao ? 'kakaopay' : form.dataset.pg,
                pay_method: data.payMethod === 'TRANS' ? 'trans' : 'card',
                merchant_uid: order.orderNo,
                name: order.name,
                amount: order.amount,
                buyer_email: form.dataset.email,
                buyer_name: data.receiverName,
                buyer_tel: data.receiverPhone,
                buyer_addr: data.address,
                buyer_postcode: data.zipcode
            }, function (rsp) {
                if (!rsp.success) { showError('결제가 취소되었거나 실패했습니다. ' + (rsp.error_msg || '')); return; }
                post('/checkout/complete', { impUid: rsp.imp_uid, merchantUid: rsp.merchant_uid }).then(function (done) {
                    if (done.ok) { location.href = done.body.redirect; }
                    else { showError(done.body.message || '결제 검증에 실패했습니다.'); }
                });
            });
        }).catch(function () { showError('네트워크 오류가 발생했습니다.'); });
    });
})();
