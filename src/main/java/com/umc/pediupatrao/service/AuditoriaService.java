package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.RegistroAuditoria;
import com.umc.pediupatrao.repository.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(String tipoOperacao, String entidadeAfetada, String entidadeId,
                          List<AlteracaoCampo> alteracoes, String justificativa) {

        RegistroAuditoria log = new RegistroAuditoria();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.setUsuario(auth.getName());
            String perfil = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.replace("ROLE_", ""))
                    .collect(Collectors.joining(", "));
            log.setPerfil(perfil);
        } else {
            log.setUsuario("SISTEMA");
            log.setPerfil("SISTEMA");
        }

        log.setDataHora(LocalDateTime.now());
        log.setTipoOperacao(tipoOperacao);
        log.setEntidadeAfetada(entidadeAfetada);
        log.setEntidadeId(entidadeId);
        log.setAlteracoes(alteracoes);
        log.setJustificativa(justificativa);

        auditoriaRepository.save(log);
    }

    public List<RegistroAuditoria> listarTodos() {
        return auditoriaRepository.findAllByOrderByDataHoraDesc();
    }
}