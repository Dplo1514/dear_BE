package com.sparta.hh99_actualproject.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FCMController {

    private final FirebaseInit init;

    @GetMapping("")
    public String v1() {
        init.init();
        return "index";
    }
}
