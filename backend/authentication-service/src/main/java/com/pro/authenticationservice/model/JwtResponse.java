package com.pro.authenticationservice.model;



import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings(
        value = { "EI_EXPOSE_REP2", "URF_UNREAD_FIELD","EI_EXPOSE_REP" },  // list only the bug codes you really mean to ignore
        justification = "we reviewed this and it's safe"
)

public class JwtResponse {
    private String token;
    private String type ;
    private String username;
    private List<String> roles;

    public JwtResponse(String token, String type, String username, List<String> roles) {
        this.token = token;
        this.type = (type == null || type.isEmpty()) ? "Bearer" : type;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }


}

