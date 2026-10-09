package com.umc.pediupatrao.repository;

import com.umc.pediupatrao.entity.RegistroAuditoria;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AuditoriaRepository extends MongoRepository<RegistroAuditoria, String> {
    // Ordena mais recente ao mais antigo
    List<RegistroAuditoria> findAllByOrderByDataHoraDesc();
}