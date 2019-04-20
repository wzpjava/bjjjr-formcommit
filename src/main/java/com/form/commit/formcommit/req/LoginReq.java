package com.form.commit.formcommit.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReq {

    public LoginReq(String phoneNo, String loginPwd) {
        this.phoneNo = phoneNo;
        this.loginPwd = loginPwd;
    }

    private String phoneNo;
    private String  loginPwd;
}
