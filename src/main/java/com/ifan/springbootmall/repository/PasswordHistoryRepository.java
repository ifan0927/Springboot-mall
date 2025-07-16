package com.ifan.springbootmall.repository;

import com.ifan.springbootmall.model.PasswordHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository  extends JpaRepository<PasswordHistory, Long> {

    @NonNull
    List<PasswordHistory> findByUserIdOrderByCreatedDateDesc(@NonNull Long userId);
}
