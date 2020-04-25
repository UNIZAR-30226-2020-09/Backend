package com.Backend.repository;

import com.Backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMensajeRepo extends JpaRepository<Message, Long> {
}
