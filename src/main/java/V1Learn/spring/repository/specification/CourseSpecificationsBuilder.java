package V1Learn.spring.repository.specification;


import V1Learn.spring.entity.Course;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final  class CourseSpecificationsBuilder {

    public final List<SpecSearchCriteria> params;

    public CourseSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    // khi không có toán tử or
    public CourseSpecificationsBuilder with(final String key, final String operation, final Object value, final String prefix, final String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public CourseSpecificationsBuilder with(final String orPredicate, final String key, final String operation, final Object value, final String prefix, final String suffix) {
        //Chuyển đổi ký tự đầu tiên của phép toán (operation) thành SearchOperation (enum).
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (searchOperation != null) {
            if (searchOperation == SearchOperation.EQUALITY) { // the operation may be complex operation
                final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    searchOperation = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    searchOperation = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    searchOperation = SearchOperation.STARTS_WITH;
                }
            }
            params.add(new SpecSearchCriteria(orPredicate, key, searchOperation, value));
        }
        return this;
    }

    // tạo 1 Specification kết hợp các điều kiện
    public Specification<Course> build() {
        if (params.isEmpty())
            return null;
        // khởi tạo 1 Specification từ đk tìm kiếm đầu tiên
        Specification<Course> result = new CourseSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new CourseSpecification(params.get(i)))
                    : Specification.where(result).and(new CourseSpecification(params.get(i)));
        }
        return result;
    }

    public CourseSpecificationsBuilder with(CourseSpecification spec) {
        params.add(spec.getCriteria());
        return this;
    }

    public CourseSpecificationsBuilder with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }
}