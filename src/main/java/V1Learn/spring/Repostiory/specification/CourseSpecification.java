package V1Learn.spring.Repostiory.specification;

import V1Learn.spring.Entity.Course;
import V1Learn.spring.enums.CourseLevel;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CourseSpecification implements Specification<Course> {

    private SpecSearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String key = criteria.getKey();
        Object value = criteria.getValue();

        // Xử lý đặc biệt cho một số trường
        return switch (key) {
            case "instructor" -> {
                Join<Object, Object> instructorJoin = root.join("instructor");
                String keyword = "%" + criteria.getValue().toString().toLowerCase() + "%";

                Predicate firstNameMatch = cb.like(cb.lower(instructorJoin.get("firstName")), keyword);
                Predicate lastNameMatch = cb.like(cb.lower(instructorJoin.get("lastName")), keyword);

                yield cb.or(firstNameMatch, lastNameMatch);
            }

            case "range" -> {
                String rangeStr = criteria.getValue().toString();
                BigDecimal min, max;

                if (rangeStr.endsWith("+")) {
                    min = new BigDecimal(rangeStr.replace("+", ""));
                    yield cb.greaterThanOrEqualTo(root.get("price"), min);
                } else {
                    String[] bounds = rangeStr.split("-");
                    if (bounds.length == 2) {
                        min = new BigDecimal(bounds[0]);
                        max = new BigDecimal(bounds[1]);
                        yield cb.between(root.get("price"), min, max);
                    } else yield null;
                }
            }

            case "level" -> {
                try {
                    CourseLevel level = CourseLevel.valueOf(value.toString().toUpperCase());
                    yield cb.equal(root.get("level"), level);
                } catch (IllegalArgumentException e) {
                    yield cb.disjunction();
                }
            }

            case "price" -> {
                if ("FREE".equalsIgnoreCase(value.toString())) {
                    yield cb.equal(root.get("price"), BigDecimal.ZERO);
                }
                try {
                    BigDecimal price = new BigDecimal(value.toString());
                    yield getPredicate(cb, root.get("price"), price);
                } catch (NumberFormatException e) {
                    yield cb.disjunction();
                }
            }

            default -> getPredicate(cb, root.get(key), value);
        };
    }

    private Predicate getPredicate(CriteriaBuilder cb, Path path, Object value) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> cb.equal(path, value);
            case NEGATION -> cb.notEqual(path, value);
            case GREATER_THAN -> cb.greaterThan(path.as(String.class), value.toString());
            case LESS_THAN -> cb.lessThan(path.as(String.class), value.toString());
            case LIKE -> cb.like(path.as(String.class), "%" + value + "%");
            case STARTS_WITH -> cb.like(path.as(String.class), value + "%");
            case ENDS_WITH -> cb.like(path.as(String.class), "%" + value);
            case CONTAINS -> cb.like(path.as(String.class), "%" + value + "%");
        };
    }
}