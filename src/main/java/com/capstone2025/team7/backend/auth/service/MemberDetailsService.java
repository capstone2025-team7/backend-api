package com.capstone2025.team7.backend.auth.service;

import com.capstone2025.team7.backend.auth.utils.CustomAuthorityUtils;
import com.capstone2025.team7.backend.exception.BusinessLogicException;
import com.capstone2025.team7.backend.exception.ExceptionCode;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<User> optionalMember = userRepository.findByEmail(userEmail);
        User findUser = optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        return new MemberDetail(findUser, authorityUtils);
    }
    @Getter
    public static final class MemberDetail implements UserDetails{

        private final User user; // 상속 대신 멤버 변수로 User를 가짐
        private final Collection<? extends GrantedAuthority> authorities;

        public MemberDetail(User user, CustomAuthorityUtils authorityUtils) {
            this.user = user;
            this.authorities = authorityUtils.createAuthorities(user.getRoles());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        // User 엔티티의 다른 정보가 필요하다면 getter를 추가할 수 있습니다.
        public Long getUserId() {
            return user.getUserId();
        }

        // 아래는 계정 상태 관련 메서드들
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
    }
}
