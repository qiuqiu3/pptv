package com.pptv.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pptv.test.domain.Item;

public interface ItemDao extends JpaRepository<Item, String>{
    Item findByDocid(String docid);
    Item findByUrl(String url);
}