// 디자인 설정: 입력 중 미리보기 (색상 선택기 <-> 코드 입력 동기화, CSS 변수 즉시 적용, 폰트 카드 선택)
(function () {
    var root = document.documentElement;
    var form = document.querySelector('[data-design-form]');
    if (!form) return;

    // 서버(ColorUtil)와 같은 규칙: 흰 글자 대비가 3:1 이상이면 흰색, 아니면 어두운 색
    function luminance(hex) {
        var c = [1, 3, 5].map(function (i) {
            var v = parseInt(hex.substr(i, 2), 16) / 255;
            return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
        });
        return 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2];
    }
    function onColor(hex) { return 1.05 / (luminance(hex) + 0.05) >= 3 ? '#FFFFFF' : '#23252B'; }
    function isHex(v) { return /^#[0-9a-fA-F]{6}$/.test(v); }

    var vars = { primaryColor: '--brand', backgroundColor: '--bg' };
    Object.keys(vars).forEach(function (name) {
        var picker = form.querySelector('[data-color-picker="' + name + '"]');
        var text = form.querySelector('[data-color-text="' + name + '"]');
        function apply(value) {
            root.style.setProperty(vars[name], value);
            if (name === 'primaryColor') root.style.setProperty('--on-brand', onColor(value));
        }
        picker.addEventListener('input', function () { text.value = picker.value.toUpperCase(); apply(picker.value); });
        text.addEventListener('input', function () {
            if (isHex(text.value)) { picker.value = text.value; apply(text.value); }
        });
    });

    form.querySelectorAll('input[name="fontPreset"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
            if (radio.checked) root.style.setProperty('--font-family', radio.dataset.stack);
        });
    });
})();
