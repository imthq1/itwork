package com.example.demo.controller;

import com.example.demo.domain.Subscriber;
import com.example.demo.domain.User;
import com.example.demo.service.SubscriberService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {


    private final SubscriberService subscriberService;
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }
    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        boolean isExist=this.subscriberService.isExistEmail(subscriber.getEmail());
//        String email= SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get():"";
        if(isExist==true)
        {
            throw new IdInvalidException("Nguoi dung k ton tai!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.createSubscriber(subscriber));
    }
    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        Subscriber subscriber1=this.subscriberService.FindById(subscriber.getId());
        if(subscriber1==null)
        {
            throw new IdInvalidException("Nguoi dung k ton tai!");
        }
        return ResponseEntity.ok().body(this.subscriberService.updateSubscriber(subscriber,subscriber1));
    }
    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscriberSkills() throws IdInvalidException {
        String email= SecurityUtil.getCurrentUserLogin().isPresent()==true?SecurityUtil.getCurrentUserLogin().get():"";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }
}
