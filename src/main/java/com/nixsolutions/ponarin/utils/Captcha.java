package com.nixsolutions.ponarin.utils;

import org.springframework.stereotype.Component;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

@Component
public class Captcha {
    private ReCaptchaImpl reCaptcha;

    public Captcha() {
        reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6LeD4BQTAAAAAPRpVTFZbmv17K_YqjVtRig6cwme");
    }

    public boolean isValid(String remoteAddr, String challengeField,
            String responseField) {
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr,
                challengeField, responseField);
        return reCaptchaResponse.isValid();
    }
}
