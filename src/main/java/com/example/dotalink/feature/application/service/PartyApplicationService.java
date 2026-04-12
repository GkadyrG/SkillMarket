package com.example.dotalink.feature.application.service;

import com.example.dotalink.common.exception.AccessDeniedBusinessException;
import com.example.dotalink.common.exception.UserNotFoundException;
import com.example.dotalink.feature.application.dto.PartyApplicationDto;
import com.example.dotalink.feature.application.model.PartyApplication;
import com.example.dotalink.feature.application.model.PartyApplicationStatus;
import com.example.dotalink.feature.application.repository.PartyApplicationRepository;
import com.example.dotalink.feature.partypost.model.PartyPost;
import com.example.dotalink.feature.partypost.model.PartyPostStatus;
import com.example.dotalink.feature.partypost.repository.PartyPostRepository;
import com.example.dotalink.feature.partypost.service.PartyPostNotFoundException;
import com.example.dotalink.feature.user.model.User;
import com.example.dotalink.feature.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartyApplicationService {

    private static final Logger log = LoggerFactory.getLogger(PartyApplicationService.class);

    private final PartyApplicationRepository partyApplicationRepository;
    private final PartyPostRepository partyPostRepository;
    private final UserRepository userRepository;

    public PartyApplicationService(PartyApplicationRepository partyApplicationRepository,
                                   PartyPostRepository partyPostRepository,
                                   UserRepository userRepository) {
        this.partyApplicationRepository = partyApplicationRepository;
        this.partyPostRepository = partyPostRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PartyApplicationDto apply(Long postId, String username, PartyApplicationDto request) {
        PartyPost post = partyPostRepository.findWithAuthorById(postId)
                .orElseThrow(() -> new PartyPostNotFoundException("Party post not found: " + postId));
        User applicant = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (post.getAuthor() != null && post.getAuthor().getId().equals(applicant.getId())) {
            throw new AccessDeniedBusinessException("Author cannot apply to own post");
        }
        if (post.getStatus() != PartyPostStatus.OPEN) {
            throw new AccessDeniedBusinessException("Applications are closed for this post");
        }
        if (partyApplicationRepository.findByPostIdAndApplicantId(postId, applicant.getId()).isPresent()) {
            throw new DuplicatePartyApplicationException("You have already applied to this post");
        }

        PartyApplication application = new PartyApplication();
        application.setPost(post);
        application.setApplicant(applicant);
        application.setMessage(clean(request.getMessage()));
        application.setStatus(PartyApplicationStatus.NEW);

        PartyApplication savedApplication = partyApplicationRepository.save(application);
        log.info("Created application: id={}, postId={}, applicant={}", savedApplication.getId(), postId, username);
        return toDto(savedApplication);
    }

    @Transactional(readOnly = true)
    public List<PartyApplicationDto> getApplicationsForPost(Long postId) {
        return partyApplicationRepository.findAllByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(this::toDto)
                .toList();
    }

    private PartyApplicationDto toDto(PartyApplication application) {
        PartyApplicationDto dto = new PartyApplicationDto();
        dto.setId(application.getId());
        dto.setPostId(application.getPost() != null ? application.getPost().getId() : null);
        dto.setApplicantUsername(application.getApplicant() != null ? application.getApplicant().getUsername() : null);
        dto.setMessage(application.getMessage());
        dto.setStatus(application.getStatus().name());
        dto.setCreatedAt(application.getCreatedAt());
        return dto;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
