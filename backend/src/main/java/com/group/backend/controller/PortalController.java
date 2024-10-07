package com.group.backend.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping; // acrescentei 

import com.group.backend.domain.DadosCadastroPortal;
import com.group.backend.domain.PortalRepository;
import com.group.backend.entity.Portal;
import com.group.backend.service.TagPortalService;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portais")
@CrossOrigin(origins = "*")
public class PortalController {

    private final PortalRepository portalRepository;
    private final TagPortalService tagPortalService;

    public PortalController(PortalRepository portalRepository, TagPortalService tagPortalService) {
        this.portalRepository = portalRepository;
        this.tagPortalService = tagPortalService;
    }

    @PostMapping("cadastrar")
    @Transactional
    public ResponseEntity<Portal> cadastrarPortal(@RequestBody DadosCadastroPortal dados) {
        Portal novoPortal = new Portal();
        novoPortal.setNome(dados.portalNome());
        novoPortal.setUrl(dados.portalUrl());
        novoPortal.setData(LocalDate.now());
        novoPortal.setFrequencia(dados.portalFrequencia());
        Portal portalSalvo = portalRepository.save(novoPortal);
        
        tagPortalService.cadastrarTagPortal(dados.tagId(), portalSalvo);
        
        return ResponseEntity.ok(portalSalvo);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Map<String, Object>>> listarPortais() {
        // Mover a lógica de listar portais e tags para o serviço
        List<Map<String, Object>> portaisComTags = tagPortalService.listarPortaisComTags();
        return ResponseEntity.ok(portaisComTags);
    }
}
