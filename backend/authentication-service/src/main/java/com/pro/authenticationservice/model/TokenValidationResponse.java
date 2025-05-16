package com.pro.authenticationservice.model;





import java.util.List;


public class TokenValidationResponse {
    private boolean valid;
    private List<String> roles;


    public TokenValidationResponse(boolean valid, List<String> roles) {
        this.valid = valid;
        this.roles = roles;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }


}