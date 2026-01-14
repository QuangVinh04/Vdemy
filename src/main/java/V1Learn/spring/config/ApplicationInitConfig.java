package V1Learn.spring.config;

import V1Learn.spring.entity.Category;
import V1Learn.spring.entity.User;
import V1Learn.spring.entity.Role;
import V1Learn.spring.repository.CategoryRepository;
import V1Learn.spring.repository.RoleRepository;
import V1Learn.spring.repository.UserRepository;
import V1Learn.spring.constant.PredefinedRole;
import V1Learn.spring.enums.UserStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;



    @NonFinal
    static final String ADMIN_USER_NAME = "admin@gmail.com";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(prefix = "spring",
    value = "datasource.driverClassName",
    havingValue = "com.mysql.cj.jdbc.Driver", matchIfMissing = true)
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        CategoryRepository categoryRepository) {
        log.info("Initializing application.....");
        return args -> {
            Optional<Role> userRole = roleRepository.findByName(PredefinedRole.USER_ROLE);
            if (userRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());
            }

            Optional<Role> adminRole = roleRepository.findByName(PredefinedRole.ADMIN_ROLE);
            if (adminRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
            }

            Optional<Role> teacherRole = roleRepository.findByName(PredefinedRole.TEACHER_ROLE);
            if (teacherRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.TEACHER_ROLE)
                        .description("Teacher role")
                        .build());
            }


            if (userRepository.findByEmail(ADMIN_USER_NAME).isEmpty()) {
                Role role = roleRepository.findByName(PredefinedRole.ADMIN_ROLE)
                        .orElseThrow(() -> new RuntimeException("User does not exist"));

                var roles = new HashSet<Role>();
                roles.add(role);
                User user = User.builder()
                        .email(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .status(UserStatus.ACTIVE)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }

            if(categoryRepository.count() == 0){
                categoryRepository.save(Category.builder()
                        .name("Default")
                        .description("Default category created automatically")
                        .isActive(true)
                        .build());
            }


            log.info("Application initialization completed .....");
        };
    }
}
