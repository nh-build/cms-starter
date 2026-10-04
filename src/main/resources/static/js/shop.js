(function () {
    // 메뉴 드로어: MENU 버튼으로 열고, 배경/닫기 버튼/ESC 로 닫는다
    var drawer = document.getElementById('navDrawer');
    if (drawer) {
        var openers = document.querySelectorAll('[data-menu-open]');
        function setDrawer(open) {
            drawer.classList.toggle('open', open);
            drawer.setAttribute('aria-hidden', open ? 'false' : 'true');
            document.body.classList.toggle('drawer-open', open);
            openers.forEach(function (b) { b.setAttribute('aria-expanded', open ? 'true' : 'false'); });
        }
        openers.forEach(function (b) { b.addEventListener('click', function () { setDrawer(true); }); });
        drawer.querySelectorAll('[data-menu-close]').forEach(function (b) {
            b.addEventListener('click', function () { setDrawer(false); });
        });
        document.addEventListener('keydown', function (e) { if (e.key === 'Escape') setDrawer(false); });
    }

    // 오버레이 헤더: 스크롤하면 흰 배경으로 전환, Search 는 입력창을 펼친다
    var header = document.querySelector('[data-site-header].overlay');
    if (header) {
        var onScroll = function () { header.classList.toggle('scrolled', window.scrollY > 40); };
        window.addEventListener('scroll', onScroll, { passive: true });
        onScroll();
    }
    document.querySelectorAll('[data-search-toggle]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var form = btn.parentElement.querySelector('.search');
            if (!form) return;
            form.classList.toggle('open');
            if (form.classList.contains('open')) form.querySelector('input').focus();
        });
    });
    // 상품 상세: 썸네일 클릭 시 메인 이미지 교체
    var main = document.getElementById('mainImage');
    document.querySelectorAll('[data-thumb]').forEach(function (t) {
        t.addEventListener('click', function () { if (main) main.src = t.src; });
    });

    // 히어로 슬라이더: 자동 넘김 + 점 네비게이션
    document.querySelectorAll('[data-slider]').forEach(function (root) {
        var track = root.querySelector('.slides');
        var items = track.children;
        var dots = root.querySelectorAll('.dots button');
        var index = 0;
        function go(i) {
            index = (i + items.length) % items.length;
            track.scrollTo({ left: items[index].offsetLeft - track.offsetLeft, behavior: 'smooth' });
        }
        function sync() {
            var i = Math.round(track.scrollLeft / track.clientWidth);
            index = i;
            dots.forEach(function (d, n) { d.classList.toggle('on', n === i); });
        }
        dots.forEach(function (d) { d.addEventListener('click', function () { go(+d.dataset.index); }); });
        track.addEventListener('scroll', sync, { passive: true });
        sync();
        if (items.length > 1) {
            var timer = setInterval(function () { go(index + 1); }, 5000);
            root.addEventListener('pointerdown', function () { clearInterval(timer); });
        }
    });
})();
