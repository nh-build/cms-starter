package com.cmsstarter.domain.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "site_setting")
@Getter
@Setter
@NoArgsConstructor
public class SiteSetting {

    @Id
    @Column(name = "setting_key")
    private String key;

    @Column(name = "setting_value", nullable = false)
    private String value;

    public SiteSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
