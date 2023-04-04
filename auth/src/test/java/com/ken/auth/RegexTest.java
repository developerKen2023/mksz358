package com.ken.auth;

public class RegexTest {

    public static void main(String[] args) {
        String regex = "/auths/auth/.*";

        System.out.println("/auths/auth/login".matches(regex));
        System.out.println("/auths/auth/bbb".matches(regex));
    }
}
