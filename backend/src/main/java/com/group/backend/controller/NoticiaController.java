package com.group.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group.backend.domain.NoticiaRepository;
import com.group.backend.entity.Noticia;
import com.group.backend.service.FilterCriteria;
import com.group.backend.service.FilterService;

@RestController
@RequestMapping("/noticias")
@CrossOrigin(origins = "*")
public class NoticiaController {

    private static final int PAGE_LENGTH = 10;
    private final NoticiaRepository noticiaRepository;
    private final FilterService filterService;

    public NoticiaController(NoticiaRepository noticiaRepository, FilterService filterService) {
        this.noticiaRepository = noticiaRepository;
        this.filterService = filterService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Noticia>> listarNoticias(
        @RequestParam(required = false) String tag,
        @RequestParam(required = false) String portal,
        @RequestParam(required = false) String reporter,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String keyword, // Suporte a texto livre
        @RequestParam(defaultValue = "0") Integer pageIndex
    ) {
        Pageable pageable = PageRequest.of(pageIndex, PAGE_LENGTH);

        Page<Noticia> noticiaPage;
        if (keyword != null && !keyword.isEmpty()) {
            // Filtro por texto livre
            noticiaPage = noticiaRepository.findByKeyword(keyword, pageable);
        } else {
            // Processando filtros adicionais
            FilterCriteria criteria = filterService.processFilters(tag, portal, reporter, startDate, endDate);

            if (criteria.getTags() == null && criteria.getPortals() == null && criteria.getReporters() == null 
                && criteria.getStartDate() == null && criteria.getEndDate() == null) {
                noticiaPage = noticiaRepository.findAll(pageable);
            } else {
                noticiaPage = noticiaRepository.findByTagsPortalsReportersAndDate(
                    criteria.getTags(),
                    criteria.getPortals(),
                    criteria.getReporters(),
                    criteria.getStartDate(),
                    criteria.getEndDate(),
                    pageable
                );
            }
        }

        List<Noticia> noticiaList = noticiaPage.toList();

        // Limitar o tamanho do texto das notícias
        noticiaList.forEach(noticia -> {
            if (noticia.getNotiText() != null && noticia.getNotiText().length() > 255) {
                noticia.setNotiText(noticia.getNotiText().substring(0, 255).concat("..."));
            }
        });

        return ResponseEntity.ok(noticiaList);
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotal(
        @RequestParam(required = false) String tag,
        @RequestParam(required = false) String portal,
        @RequestParam(required = false) String reporter,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String keyword // Suporte a texto livre
    ) {
        if (keyword != null && !keyword.isEmpty()) {
            // Retornar o total de resultados filtrados por texto livre
            long total = noticiaRepository.findByKeyword(keyword, Pageable.unpaged()).getTotalElements();
            return ResponseEntity.ok(total);
        }

        // Processando filtros adicionais
        FilterCriteria criteria = filterService.processFilters(tag, portal, reporter, startDate, endDate);

        long total;
        if (criteria.getTags() == null && criteria.getPortals() == null && criteria.getReporters() == null 
            && criteria.getStartDate() == null && criteria.getEndDate() == null) {
            total = noticiaRepository.count();
        } else {
            total = noticiaRepository.findByTagsPortalsReportersAndDate(
                criteria.getTags(),
                criteria.getPortals(),
                criteria.getReporters(),
                criteria.getStartDate(),
                criteria.getEndDate(),
                Pageable.unpaged()
            ).getTotalElements();
        }

        return ResponseEntity.ok(total);
    }
}
