package ru.socks.registry.socksapp.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.socks.registry.socksapp.models.Socks;

public class SocksSpecifications {
    public static Specification<Socks> byColor(String color) {
        return (root, query, cb) ->
                color == null ? null : cb.equal(root.get("color"), color);
    }

    public static Specification<Socks> moreThan (Integer quantity) {
        return (root, query, cb) ->
                quantity == null ? null : cb.greaterThan(root.get("quantity"), quantity);
    }

    public static Specification<Socks> lessThan (Integer quantity) {
        return (root, query, cb) ->
                quantity == null ? null : cb.lessThan(root.get("quantity"), quantity);
    }

    public static Specification<Socks> equal (Integer quantity) {
        return (root, query, cb) ->
                quantity == null ? null : cb.equal(root.get("quantity"), quantity);
    }
}
