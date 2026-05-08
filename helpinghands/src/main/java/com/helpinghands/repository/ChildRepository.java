package com.helpinghands.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.helpinghands.model.Child;

public interface ChildRepository extends JpaRepository<Child, Long> {}
