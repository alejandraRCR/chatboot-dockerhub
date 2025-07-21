package com.chatboot.request.repository;

import com.chatboot.request.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Integer> {
    Optional<Response> findByQuestionIgnoreCase(String question);

    List<Response> findByStatusTrue();
}
