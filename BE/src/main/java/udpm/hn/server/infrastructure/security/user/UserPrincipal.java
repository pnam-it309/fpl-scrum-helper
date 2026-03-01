package udpm.hn.server.infrastructure.security.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserPrincipal implements OAuth2User, UserDetails {
// phân quyền
    @Getter
    private final String id;

    @Getter
    private final String email;

    private String password;

    private final Collection<? extends GrantedAuthority> authorities;

    @Setter
    private Map<String, Object> attributes;

    public UserPrincipal(String id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Staff staff, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        log.info("authorities staff: {}", authorities);

        UserPrincipal userPrincipal =  new UserPrincipal(
                staff.getId(),
                staff.getEmailFpt(),
                authorities
        );

        return userPrincipal;
    }

    public static UserPrincipal create(Student student, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        log.info("authorities student: {}", authorities);

        UserPrincipal userPrincipal =  new UserPrincipal(
                student.getId(),
                student.getEmail(),
                authorities
        );

        return userPrincipal;
    }

    public static UserPrincipal create(Staff staff, Map<String, Object> attributes, List<String> roles) {
        UserPrincipal userPrincipal = UserPrincipal.create(staff, roles);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public static UserPrincipal create(Student student, Map<String, Object> attributes, List<String> roles) {
        UserPrincipal userPrincipal = UserPrincipal.create(student, roles);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
