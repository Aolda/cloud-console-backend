package com.acc.server.openstack.controller;

import com.acc.server.openstack.service.OpenStackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OpenStackController {
    private final OpenStackService openStackService;

    @GetMapping("/token")
    public String issueToken(@RequestParam String user,
                             @RequestParam String pwd,
                             @RequestParam String project) {
        return openStackService.getToken(user, pwd, project);
    }
}
