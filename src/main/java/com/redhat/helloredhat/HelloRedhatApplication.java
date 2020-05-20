package com.redhat.helloredhat;

import com.openshift.internal.restclient.model.Project;
import com.openshift.restclient.ClientBuilder;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class HelloRedhatApplication {


    @Value("${openshift.api}")
    String api;

    @GetMapping("/")
    public String hi() {
        return "Running...............";
    }
    
    
    @GetMapping("/good")
    public String hello() {
        return "Hello RedHat";
    }
    
    @GetMapping("/good/job")
    public String hello2() {
        return "yes, good job.";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(String username, String password) {
        IClient client = new ClientBuilder(api).
                withUserName(username).withPassword(password).build();
        Map<String, Object> body = new HashMap<>();
        try {
            client.getAuthorizationContext().isAuthorized();
            body.put("isAuthorized", true);
            body.put("token", client.getAuthorizationContext().getToken());
        }catch (UnauthorizedException exception){
            body.put("isAuthorized", false);
        }
        return ResponseEntity.ok(body);
    }

    @GetMapping("/projects")
    public ResponseEntity projects(@RequestHeader("Authorization") String authorization){
        //xxxxx
        if (!authorization.startsWith("Bearer ")){
            return ResponseEntity.status(500).build();
        }
        String token = authorization.substring(7);
        IClient client = new ClientBuilder(api).build();
        client.getAuthorizationContext().setToken(token);
        client.getAuthorizationContext().isAuthorized();
        List<Project> projects = client.list(ResourceKind.PROJECT);
        List<Pro> pros = new ArrayList<>();
        for (Project project : projects) {
            Pro p = new Pro();
            p.setName(project.getName());
            p.setDescription(project.getDescription());
            p.setDisplayName(project.getDisplayName());
            pros.add(p);
        }
        return ResponseEntity.ok(pros);
    }



    public static void main(String[] args) {
        SpringApplication.run(HelloRedhatApplication.class, args);
    }

}

class Pro {
    String name;
    String displayName;
    String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
