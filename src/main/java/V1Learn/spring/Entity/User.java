package V1Learn.spring.Entity;


import V1Learn.spring.utils.Gender;
import V1Learn.spring.utils.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User extends AbstractEntity implements Serializable, UserDetails {

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "password")
    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "phone")
    String phone;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE) // chỉ lưu ngày, tháng, năm
    LocalDate dob;

    @Enumerated(EnumType.STRING) // Lưu tên enum dưới dạng chuỗi trong DB
    @Column(name = "gender")
    Gender gender;

    @ManyToMany(fetch = FetchType.EAGER)  // Load quyền ngay khi lấy user
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles = new HashSet<>();

    @Column(name = "address")
    String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user") //Khi serialize Enrollment, bỏ qua field user bên trong đó để tránh vòng lặp
    Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "instructor",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Course> courses;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("recipient")
    Set<Notification> notifications;


    @Column(name = "avatar")
    String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    Set<RegisterTeacher> registerTeachers;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.status.equals(UserStatus.ACTIVE);
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
