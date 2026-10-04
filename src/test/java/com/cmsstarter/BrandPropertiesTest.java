package com.cmsstarter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.cmsstarter.config.BrandProperties;

class BrandPropertiesTest {

    @Test
    void cssVarsContainConfiguredBrandColors() {
        BrandProperties brand = new BrandProperties();
        brand.setPrimaryColor("#112233");
        brand.setBackgroundColor("#ffffff");

        assertThat(brand.getCssVars()).contains("--brand:#112233").contains("--bg:#ffffff");
    }

    @Test
    void invalidValuesFallBackToDefaultsSoNothingCanBreakOutOfTheStyleTag() {
        BrandProperties brand = new BrandProperties();
        brand.setPrimaryColor("red;}</style><script>alert(1)</script>");
        brand.setFontFamily("x;}</style>");

        assertThat(brand.getCssVars())
                .contains("--brand:#4E90C4")
                .contains("--font:sans-serif")
                .doesNotContain("<");
    }
}
