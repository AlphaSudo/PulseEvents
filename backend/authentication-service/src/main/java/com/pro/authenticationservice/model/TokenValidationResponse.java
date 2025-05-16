package com.pro.authenticationservice.model;





import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings(
        value = { "EI_EXPOSE_REP2", "URF_UNREAD_FIELD" },  // list only the bug codes you really mean to ignore
        justification = "we reviewed this and it's safe"
)

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