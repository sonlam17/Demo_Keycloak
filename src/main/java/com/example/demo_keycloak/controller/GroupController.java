package com.example.demo_keycloak.controller;

import com.example.demo_keycloak.keycloakService.KeycloakService;
import com.example.demo_keycloak.vo.EmployeeVO;
import com.example.demo_keycloak.vo.GroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/group")
@RestController
public class GroupController {
    @Autowired
    private KeycloakService keycloakService;

    @PostMapping(path = "/create")
    public ResponseEntity<?> createGroup
            (@RequestBody GroupVO groupVO) {
        return ResponseEntity.ok(keycloakService.
                createGroup(groupVO));
    }
    @PostMapping(path = "/createChild")
    public ResponseEntity<?> createChildGroup
            (@RequestBody GroupVO groupVO) {
        return ResponseEntity.ok(keycloakService.
                creatChildGroup(groupVO));
    }
    @GetMapping(path = "/allGroup")
    public ResponseEntity<?> allGroup
            () {
        return ResponseEntity.ok(keycloakService.
                allGroup());
    }
    @GetMapping(path = "/detailGroup")
    public ResponseEntity<?> detailGroup
            () {
        return ResponseEntity.ok(keycloakService.
                detailGroup());
    }
}
