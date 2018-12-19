package com.pptv.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pptv.test.domain.Police;

public interface PoliceDao extends JpaRepository<Police, String>{
    Police findByUrl(String url);
}