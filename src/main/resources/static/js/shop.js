(function () {
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
