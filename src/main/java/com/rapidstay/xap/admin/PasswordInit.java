package com.rapidstay.xap.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordInit implements CommandLineRunner {

    private final PasswordEncoder encoder;

    public PasswordInit(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        String encoded = encoder.encode("rapid1234");
        System.out.println("🔑 새 bcrypt 해시 = " + encoded);
    }
}
