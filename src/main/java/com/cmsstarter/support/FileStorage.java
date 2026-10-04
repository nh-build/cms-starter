package com.cmsstarter.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cmsstarter.config.AppProperties;

/** 이미지 업로드 저장소. 파일명은 UUID 로 바꾸고 확장자/Content-Type 을 검증한다. */
@Component
public class FileStorage {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path root;

    public FileStorage(AppProperties props) {
        this.root = Path.of(props.getUploadDir()).toAbsolutePath().normalize();
    }

    public Path getRoot() {
        return root;
    }

    /** @return 웹 경로 (예: /uploads/uuid.png). 빈 파일이면 null. */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int dot = original.lastIndexOf('.');
        String ext = dot < 0 ? "" : original.substring(dot + 1).toLowerCase(Locale.ROOT);
        String type = file.getContentType() == null ? "" : file.getContentType();
        if (!ALLOWED_EXT.contains(ext) || !type.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일(jpg, png, gif, webp)만 업로드할 수 있습니다.");
        }
        String name = UUID.randomUUID() + "." + ext;
        try {
            Files.createDirectories(root);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }
        return "/uploads/" + name;
    }

    public void delete(String webPath) {
        if (webPath == null || !webPath.startsWith("/uploads/")) {
            return;
        }
        Path target = root.resolve(webPath.substring("/uploads/".length())).normalize();
        if (!target.startsWith(root)) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // 파일 삭제 실패는 서비스 흐름을 막지 않는다.
        }
    }
}
