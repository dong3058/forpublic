package com.roulette.roulette.aboutlogin.userinfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails {


    private Kakaouserdata kakaouserdata;
    private List<SimpleGrantedAuthority> simpleGrantedAuthorityList=new ArrayList<>();
    public CustomUserDetail(Kakaouserdata kakaouserdata) {
        this.kakaouserdata = kakaouserdata;
        }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role="ROLE_"+"user";
        if(simpleGrantedAuthorityList.size()==0){
            simpleGrantedAuthorityList.add(new SimpleGrantedAuthority(role));

        }
        return simpleGrantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return kakaouserdata.getEmail();
    }

    @Override
    public String getUsername() {
        return kakaouserdata.getUsername();
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
}
