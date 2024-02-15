package com.BeilsangServer.domain.auth.model;

import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.global.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
// SecurityContext authentication에 저장될 유저정보
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    @Setter
    private Map<String, Object> attributes;

    public static UserPrincipal create(Member member) {
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(Role.USER.getRole()));
        return new UserPrincipal(
                member.getId(),
                member.getEmail(),
                authorities,
                null
        );
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
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

}