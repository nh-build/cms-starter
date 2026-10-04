package com.cmsstarter.domain.catalog;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cmsstarter.support.FileStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductAdminService {

    private static final java.util.regex.Pattern HEX = java.util.regex.Pattern.compile("^#[0-9a-fA-F]{3,8}$");

    private final ProductRepository products;
    private final CategoryRepository categories;
    private final FileStorage storage;

    @Transactional
    public Product save(Long id, ProductForm form) {
        Product p = id == null ? new Product() : products.findDetailById(id).orElseThrow();
        p.setName(form.getName().trim());
        p.setPrice(form.getPrice());
        p.setDescription(form.getDescription());
        p.setStock(form.getStock());
        p.setStatus(form.getStatus());
        p.setFeatured(form.isFeatured());
        p.setNewArrival(form.isNewArrival());
        p.setBest(form.isBest());
        p.setCategory(form.getCategoryId() == null ? null : categories.findById(form.getCategoryId()).orElse(null));

        String thumb = storage.store(form.getThumbnailFile());
        if (thumb != null) {
            storage.delete(p.getThumbnail());
            p.setThumbnail(thumb);
        }
        if (form.getImageFiles() != null) {
            int order = p.getImages().size();
            for (MultipartFile f : form.getImageFiles()) {
                String path = storage.store(f);
                if (path != null) {
                    ProductImage img = new ProductImage();
                    img.setProduct(p);
                    img.setPath(path);
                    img.setSortOrder(order++);
                    p.getImages().add(img);
                }
            }
        }
        replaceOptions(p, form);
        return products.save(p);
    }

    @Transactional
    public void delete(Long id) {
        products.findDetailById(id).ifPresent(p -> {
            storage.delete(p.getThumbnail());
            p.getImages().forEach(i -> storage.delete(i.getPath()));
            products.delete(p);
        });
    }

    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        Product p = products.findDetailById(productId).orElseThrow();
        p.getImages().removeIf(i -> {
            boolean hit = i.getId().equals(imageId);
            if (hit) {
                storage.delete(i.getPath());
            }
            return hit;
        });
    }

    @Transactional(readOnly = true)
    public ProductForm toForm(Product p) {
        ProductForm f = new ProductForm();
        f.setName(p.getName());
        f.setPrice(p.getPrice());
        f.setCategoryId(p.getCategory() == null ? null : p.getCategory().getId());
        f.setDescription(p.getDescription());
        f.setStock(p.getStock());
        f.setStatus(p.getStatus());
        f.setFeatured(p.isFeatured());
        f.setNewArrival(p.isNewArrival());
        f.setBest(p.isBest());
        f.setColors(p.optionsOf(OptionType.COLOR).stream()
                .map(o -> o.getValue() + (o.getColorHex() == null ? "" : "=" + o.getColorHex()))
                .collect(Collectors.joining("\n")));
        f.setSizes(p.optionsOf(OptionType.SIZE).stream().map(ProductOption::getValue).collect(Collectors.joining(",")));
        return f;
    }

    private void replaceOptions(Product p, ProductForm form) {
        p.getOptions().clear();
        int order = 0;
        if (form.getColors() != null) {
            for (String line : form.getColors().split("\\R")) {
                String t = line.trim();
                if (t.isEmpty()) {
                    continue;
                }
                String[] parts = t.split("=", 2);
                ProductOption o = new ProductOption();
                o.setProduct(p);
                o.setType(OptionType.COLOR);
                o.setValue(parts[0].trim());
                if (parts.length > 1 && HEX.matcher(parts[1].trim()).matches()) {
                    o.setColorHex(parts[1].trim());
                }
                o.setSortOrder(order++);
                p.getOptions().add(o);
            }
        }
        for (String s : ProductService.splitValues(form.getSizes())) {
            ProductOption o = new ProductOption();
            o.setProduct(p);
            o.setType(OptionType.SIZE);
            o.setValue(s);
            o.setSortOrder(order++);
            p.getOptions().add(o);
        }
    }
}
