package com.cmsstarter;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.cmsstarter.config.PortOneProperties;
import com.cmsstarter.domain.order.PortOneClient;

class PortOneClientTest {

    @Test
    void verificationIsSkippedWhenDisabled() {
        PortOneProperties props = new PortOneProperties();
        props.setVerifyEnabled(false);

        assertThatCode(() -> new PortOneClient(props).verify("imp_1", "ORDER-1", 1000)).doesNotThrowAnyException();
    }

    @Test
    void enablingVerificationWithoutKeysFailsLoudly() {
        PortOneProperties props = new PortOneProperties();
        props.setVerifyEnabled(true);

        assertThatThrownBy(() -> new PortOneClient(props).verify("imp_1", "ORDER-1", 1000))
                .isInstanceOf(IllegalStateException.class);
    }
}
