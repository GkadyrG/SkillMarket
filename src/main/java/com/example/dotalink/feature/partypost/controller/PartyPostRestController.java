package com.example.dotalink.feature.partypost.controller;

import com.example.dotalink.common.exception.AccessDeniedBusinessException;
import com.example.dotalink.feature.application.dto.PartyApplicationDto;
import com.example.dotalink.feature.application.service.PartyApplicationService;
import com.example.dotalink.feature.partypost.dto.PartyPostCreateDto;
import com.example.dotalink.feature.partypost.dto.PartyPostDto;
import com.example.dotalink.feature.partypost.dto.PartyPostFilterDto;
import com.example.dotalink.feature.partypost.dto.PartyPostUpdateDto;
import com.example.dotalink.feature.partypost.service.PartyPostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PartyPostRestController {

    private final PartyPostService partyPostService;
    private final PartyApplicationService partyApplicationService;

    public PartyPostRestController(PartyPostService partyPostService,
                                   PartyApplicationService partyApplicationService) {
        this.partyPostService = partyPostService;
        this.partyApplicationService = partyApplicationService;
    }

    @GetMapping
    public Page<PartyPostDto> getPosts(@RequestParam(defaultValue = "") String rank,
                                       @RequestParam(defaultValue = "") String role,
                                       @RequestParam(defaultValue = "") String region,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        PartyPostFilterDto filter = new PartyPostFilterDto();
        filter.setRank(rank);
        filter.setRole(role);
        filter.setRegion(region);
        return partyPostService.getPostsPage(filter, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyPostDto createPost(@Valid @RequestBody PartyPostCreateDto request, Authentication authentication) {
        Long id = partyPostService.createPost(authentication.getName(), request);
        return partyPostService.getPost(id);
    }

    @PutMapping("/{id}")
    public PartyPostDto updatePost(@PathVariable Long id,
                                   @Valid @RequestBody PartyPostUpdateDto request,
                                   Authentication authentication) {
        partyPostService.updatePost(id, authentication.getName(), request);
        return partyPostService.getPost(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id, Authentication authentication) {
        partyPostService.deletePost(id, authentication.getName());
    }

    @PostMapping("/{id}/apply")
    public Map<String, Object> applyToPost(@PathVariable Long id,
                                           @Valid @RequestBody PartyApplicationDto request,
                                           Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authentication required");
        }
        PartyApplicationDto response;
        try {
            response = partyApplicationService.apply(id, authentication.getName(), request);
        } catch (AccessDeniedBusinessException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage(), ex);
        }
        return Map.of(
                "success", true,
                "applicationId", response.getId(),
                "postId", response.getPostId(),
                "status", response.getStatus(),
                "message", "Application sent successfully"
        );
    }
}
