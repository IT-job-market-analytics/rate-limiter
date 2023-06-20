package com.example.ratelimiter.controller;

import com.example.ratelimiter.exception.BadRequestException;
import com.example.ratelimiter.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LimiterController {

    @GetMapping(value =
            {"/quota/",
                    "/quota/{operation_id}"})
    @ResponseStatus(HttpStatus.OK)
    public void getQuota(@PathVariable(required = false) String operation_id) {

        if (operation_id == null) {
            throw new BadRequestException();
        }

        if (operation_id.equals("test_not_found")) {
            throw new NotFoundException();
        }
    }
}
