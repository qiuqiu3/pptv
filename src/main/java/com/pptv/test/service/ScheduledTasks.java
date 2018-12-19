package com.pptv.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {

    @Autowired
    PPTVProcessor pptvProcessor;
    @Scheduled(fixedRate = 1000*7200)
    public void execute() {
    	pptvProcessor.runSpider();
    }
}