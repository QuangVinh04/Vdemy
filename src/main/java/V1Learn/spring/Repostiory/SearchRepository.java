//package V1Learn.spring.Repostiory;
//
//
//import V1Learn.spring.DTO.Response.PageResponse;
//import V1Learn.spring.DTO.Response.UserResponse;
//import V1Learn.spring.Entity.Course;
//import V1Learn.spring.Entity.User;
//import V1Learn.spring.Mapper.UserMapper;
//import V1Learn.spring.Repostiory.criteria.SearchCriteria;
//import V1Learn.spring.Repostiory.criteria.UserSearchQueryCriteriaConsumer;
//import V1Learn.spring.Repostiory.specification.SpecSearchCriteria;
//import V1Learn.spring.Service.CourseService;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//import jakarta.persistence.criteria.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Repository
//@Slf4j
//public class SearchRepository {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Autowired
//    private UserMapper userMapper;
//
//
//    private static final String LIKE_FORMAT = "%%%s%%";
//    //% là một ký tự đặc biệt trong String.format(), nên ta phải dùng %% để biểu diễn một % thực sự.
//    //"%%%s%%" -> "%s%"
//
//    public PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
//        StringBuilder sqlQuery = new StringBuilder("select u from User u where 1 = 1");
//
//        // xử lý phần search
//        if (StringUtils.hasLength(search)) { // kiểm tra search không null, không rỗng
//            sqlQuery.append(" and lower(u.fullname) like lower(:fullname)");
//            sqlQuery.append(" or lower(u.username) like lower(:username)");
//            sqlQuery.append(" or lower(u.email) like lower(:email)");
//        }
//
//        if (StringUtils.hasLength(sortBy)) {
//            // fullname:asc|desc
//            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
//            Matcher matcher = pattern.matcher(sortBy);
//            // kiểm tra xem chuỗi sortBy có khớp với regex không
//            if (matcher.find()) {
//                sqlQuery.append(String.format(" ORDER BY u.%s %s", matcher.group(1), matcher.group(3)));
//            }
//        }
//
//
//        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
//        selectQuery.setFirstResult(pageNo); // set giá trị bắt đầu
//        selectQuery.setMaxResults(pageSize); // set giá trị kết thúc
//
//        // gán tham số tìm kiếm
//        if (StringUtils.hasLength(search)) {
//            selectQuery.setParameter("fullname", String.format(LIKE_FORMAT, search)); //?
//            selectQuery.setParameter("username", String.format(LIKE_FORMAT, search));
//            selectQuery.setParameter("email", String.format(LIKE_FORMAT, search));
//        }
//
//
//        List<User> users = selectQuery.getResultList(); // lấy ra danh sách users
//        List<UserResponse> userResponses = users.stream().map(userMapper::toUserResponse).toList();
//
//
//        // xử lý phần lấy ra số kết quả thỏa mãn
//        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from User u where 1 = 1");
//        if (StringUtils.hasLength(search)) {
//            sqlCountQuery.append(" and lower(u.fullname) like lower(?1)");
//            sqlCountQuery.append(" or lower(u.username) like lower(?2)");
//            sqlCountQuery.append(" or lower(u.email) like lower(?3)");
//        }
//
//        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
//
//        // gán tham số tìm kiếm
//        if (StringUtils.hasLength(search)) {
//            selectCountQuery.setParameter(1, String.format(LIKE_FORMAT, search)); //?
//            selectCountQuery.setParameter(2, String.format(LIKE_FORMAT, search));
//            selectCountQuery.setParameter(3, String.format(LIKE_FORMAT, search));
//        }
//        Long totalElements = (Long) selectCountQuery.getSingleResult();
//        if (totalElements == null) totalElements = 0L;
//
//        Pageable pageable = PageRequest.of(pageNo, pageSize);
//
//        //tạo một đối tượng Page chứa danh sách dữ liệu phân trang.
//        Page<?> page = new PageImpl<>(userResponses, pageable, totalElements);
//
//        return PageResponse.builder()
//                .pageNo(pageNo)
//                .pageSize(pageSize)
//                .totalPage(page.getTotalPages())
//                .items(userResponses)
//                .build();
//    }
//
////    /**
////     * Advance search user by criterias
////     *
////     * @param offset
////     * @param pageSize
////     * @param sortBy
////     * @param search
////     * @return
////     */
//
//
//    public PageResponse<?> searchUserByCriteria(int offset, int pageSize, String sortBy, String address, String... search) {
//        log.info("Search user with search={} and sortBy={}", search, sortBy);
//
//        List<SearchCriteria> criteriaList = new ArrayList<>();
//
//        if (search.length > 0) {
//            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(.*)");
//            for (String s : search) {
//                Matcher matcher = pattern.matcher(s);
//                if (matcher.find()) {
//                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
//                }
//            }
//        }
//
//
//        List<User> users = getUsers(offset, pageSize, criteriaList, address, sortBy);
//        List<UserResponse> userResponses = users.stream().map(userMapper::toUserResponse).toList();
//
//        Long totalElements = getTotalElements(criteriaList);
//
//        Page<User> page = new PageImpl<>(users, PageRequest.of(offset, pageSize), totalElements);
//
//        return PageResponse.builder()
//                .pageNo(offset)
//                .pageSize(pageSize)
//                .totalPage(page.getTotalPages())
//                .items(userResponses)
//                .build();
//    }
//
////    private List<User> getUsers(int offset, int pageSize, List<SearchCriteria> criteriaList, String address, String sortBy) {
////
////        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder(); // tạo truy vấn, điều kiện lọc, sắp xếp
////        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class); // xd truy vấn áp dụng điều kiện, sx, nhóm dữ kiệu
////        Root<User> userRoot = query.from(User.class);                       // đại diện thực thể gốc, truy xuất dl (FROM), thực hiện JOIN
////
////        Predicate userPredicate = criteriaBuilder.conjunction(); // tạo điều kiện mặc định là true
////        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(userPredicate, criteriaBuilder, userRoot);
////
////        if (StringUtils.hasLength(address)) {
////            Join<RabbitConnectionDetails.Address, User> userAddressJoin = userRoot.join("addresses");
////            Predicate addressPredicate = criteriaBuilder.equal(userAddressJoin.get("city"), address);
////            query.where(userPredicate, addressPredicate);
////        } else {
////            criteriaList.forEach(searchConsumer);
////            userPredicate = searchConsumer.getPredicate();
////            query.where(userPredicate);
////        }
////
////
////        // xử lý sắp xếp
////        if (StringUtils.hasLength(sortBy)) {
////            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
////            Matcher matcher = pattern.matcher(sortBy);
////            if (matcher.find()) {
////                String fieldName = matcher.group(1);
////                String direction = matcher.group(3);
////                if (direction.equalsIgnoreCase("asc")) {
////                    query.orderBy(criteriaBuilder.asc(userRoot.get(fieldName)));
////                } else {
////                    query.orderBy(criteriaBuilder.desc(userRoot.get(fieldName)));
////                }
////            }
////        }
////
////        return entityManager.createQuery(query)
////                .setFirstResult(offset)
////                .setMaxResults(pageSize)
////                .getResultList();
////    }
//
//
//    private Long getTotalElements(List<SearchCriteria> params) {
//        log.info("-------------- getTotalElements --------------");
//
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
//        Root<User> root = query.from(User.class);
//
//        Predicate predicate = criteriaBuilder.conjunction();
//        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, criteriaBuilder, root);
//        params.forEach(searchConsumer);
//        predicate = searchConsumer.getPredicate();
//        query.select(criteriaBuilder.count(root)); // truy xuất số bản ghi
//        query.where(predicate);  // áp dụng điều kiện lọc
//
//        return entityManager.createQuery(query).getSingleResult();
//    }
//
//
//}
