package com.pptv.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pptv.test.domain.Category;

public interface CategoryDao extends JpaRepository<Category, String>{
    Category findByCategoryName(String name);
}