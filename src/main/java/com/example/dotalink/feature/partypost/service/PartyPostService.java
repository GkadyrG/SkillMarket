package com.example.dotalink.feature.partypost.service;

import com.example.dotalink.common.exception.AccessDeniedBusinessException;
import com.example.dotalink.common.exception.UserNotFoundException;
import com.example.dotalink.feature.application.repository.PartyApplicationRepository;
import com.example.dotalink.feature.partypost.dto.PartyPostCreateDto;
import com.example.dotalink.feature.partypost.dto.PartyPostDto;
import com.example.dotalink.feature.partypost.dto.PartyPostFilterDto;
import com.example.dotalink.feature.partypost.dto.PartyPostUpdateDto;
import com.example.dotalink.feature.partypost.model.PartyPost;
import com.example.dotalink.feature.partypost.model.PartyPostStatus;
import com.example.dotalink.feature.partypost.repository.PartyPostCriteriaRepository;
import com.example.dotalink.feature.partypost.repository.PartyPostRepository;
import com.example.dotalink.feature.profile.model.DotaRank;
import com.example.dotalink.feature.profile.model.DotaRegion;
import com.example.dotalink.feature.profile.model.DotaRolePreference;
import com.example.dotalink.feature.user.model.User;
import com.example.dotalink.feature.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class PartyPostService {

    private static final Logger log = LoggerFactory.getLogger(PartyPostService.class);

    private final PartyPostRepository partyPostRepository;
    private final PartyPostCriteriaRepository partyPostCriteriaRepository;
    private final PartyApplicationRepository partyApplicationRepository;
    private final UserRepository userRepository;

    public PartyPostService(PartyPostRepository partyPostRepository,
                            PartyPostCriteriaRepository partyPostCriteriaRepository,
                            PartyApplicationRepository partyApplicationRepository,
                            UserRepository userRepository) {
        this.partyPostRepository = partyPostRepository;
        this.partyPostCriteriaRepository = partyPostCriteriaRepository;
        this.partyApplicationRepository = partyApplicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<PartyPostDto> getPostsPage(PartyPostFilterDto filter, Pageable pageable) {
        Page<PartyPost> page;
        if ("jpql".equalsIgnoreCase(filter.getMode())) {
            page = partyPostRepository.searchOpenPosts(
                    lowerOrEmpty(filter.getRank()),
                    lowerOrEmpty(filter.getRole()),
                    lowerOrEmpty(filter.getRegion()),
                    PartyPostStatus.OPEN,
                    pageable
            );
        } else {
            page = partyPostCriteriaRepository.search(filter.getRank(), filter.getRole(), filter.getRegion(), pageable);
        }
        return new PageImpl<>(page.getContent().stream().map(this::toDto).toList(), pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PartyPostDto getPost(Long id) {
        return toDto(getRequiredPost(id));
    }

    @Transactional(readOnly = true)
    public PartyPostUpdateDto getUpdateDto(Long id, String username) {
        PartyPost post = getRequiredPost(id);
        validateOwner(post, username);

        PartyPostUpdateDto dto = new PartyPostUpdateDto();
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setRequiredRank(post.getRequiredRank());
        dto.setRoleNeeded(post.getRoleNeeded());
        dto.setRegion(post.getRegion());
        dto.setStatus(post.getStatus().name());
        return dto;
    }

    @Transactional
    public Long createPost(String username, PartyPostCreateDto request) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        PartyPost post = new PartyPost();
        post.setAuthor(author);
        applyFields(post, request);
        post.setStatus(PartyPostStatus.OPEN);
        PartyPost savedPost = partyPostRepository.save(post);
        log.info("Created party post: id={}, author={}", savedPost.getId(), username);
        return savedPost.getId();
    }

    @Transactional
    public void updatePost(Long id, String username, PartyPostUpdateDto request) {
        PartyPost post = getRequiredPost(id);
        validateOwner(post, username);
        applyFields(post, request);
        post.setStatus(PartyPostStatus.valueOf(request.getStatus().trim().toUpperCase(Locale.ROOT)));
        partyPostRepository.save(post);
        log.info("Updated party post: id={}, author={}", id, username);
    }

    @Transactional
    public void deletePost(Long id, String username) {
        PartyPost post = getRequiredPost(id);
        validateOwner(post, username);
        partyPostRepository.delete(post);
        log.info("Deleted party post: id={}, author={}", id, username);
    }

    private PartyPost getRequiredPost(Long id) {
        return partyPostRepository.findWithAuthorById(id)
                .orElseThrow(() -> new PartyPostNotFoundException("Party post not found: " + id));
    }

    private void validateOwner(PartyPost post, String username) {
        if (post.getAuthor() == null || !post.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedBusinessException("Only the author can change this post");
        }
    }

    private void applyFields(PartyPost post, PartyPostCreateDto request) {
        post.setTitle(request.getTitle().trim());
        post.setDescription(clean(request.getDescription()));
        post.setRequiredRank(DotaRank.normalizeOrNull(request.getRequiredRank()));
        post.setRoleNeeded(normalizeRoleOrNull(request.getRoleNeeded()));
        post.setRegion(DotaRegion.normalizeOrNull(request.getRegion()));
    }

    private PartyPostDto toDto(PartyPost post) {
        PartyPostDto dto = new PartyPostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setRequiredRank(post.getRequiredRank());
        dto.setRoleNeeded(post.getRoleNeeded());
        dto.setRegion(post.getRegion());
        dto.setStatus(post.getStatus().name());
        dto.setAuthorUsername(post.getAuthor() != null ? post.getAuthor().getUsername() : null);
        dto.setCreatedAt(post.getCreatedAt());
        dto.setApplicationsCount(partyApplicationRepository.countByPostId(post.getId()));
        return dto;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String lowerOrEmpty(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRoleOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        return DotaRolePreference.valuesList().stream()
                .filter(role -> role.equalsIgnoreCase(normalized))
                .findFirst()
                .orElse(null);
    }
}
