package com.ankita.mediumclone.repository;

import com.ankita.mediumclone.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}