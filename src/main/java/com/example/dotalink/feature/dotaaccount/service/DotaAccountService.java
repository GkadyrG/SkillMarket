package com.example.dotalink.feature.dotaaccount.service;

import com.example.dotalink.common.exception.DotaAccountNotFoundException;
import com.example.dotalink.common.exception.UserNotFoundException;
import com.example.dotalink.feature.dotaaccount.dto.DotaAccountForm;
import com.example.dotalink.feature.dotaaccount.dto.DotaAccountViewDto;
import com.example.dotalink.feature.dotaaccount.model.DotaAccount;
import com.example.dotalink.feature.dotaaccount.repository.DotaAccountRepository;
import com.example.dotalink.feature.user.model.User;
import com.example.dotalink.feature.user.repository.UserRepository;
import com.example.dotalink.integration.dota.DotaExternalProfile;
import com.example.dotalink.integration.dota.DotaApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DotaAccountService {

    private static final Logger log = LoggerFactory.getLogger(DotaAccountService.class);

    private final UserRepository userRepository;
    private final DotaAccountRepository dotaAccountRepository;
    private final DotaApiClient dotaApiClient;

    public DotaAccountService(UserRepository userRepository,
                              DotaAccountRepository dotaAccountRepository,
                              DotaApiClient dotaApiClient) {
        this.userRepository = userRepository;
        this.dotaAccountRepository = dotaAccountRepository;
        this.dotaApiClient = dotaApiClient;
    }

    @Transactional(readOnly = true)
    public Optional<DotaAccount> getForUser(String username) {
        return dotaAccountRepository.findByUserUsername(username);
    }

    @Transactional(readOnly = true)
    public DotaAccount getRequiredForUser(String username) {
        return getForUser(username)
                .orElseThrow(() -> new DotaAccountNotFoundException("Dota account not found for user: " + username));
    }

    @Transactional
    public DotaAccount upsertForUser(String username, DotaAccountForm form) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        DotaAccount account = dotaAccountRepository.findByUserId(user.getId()).orElseGet(DotaAccount::new);
        account.setUser(user);
        account.setSteamId(form.getSteamId().trim());
        account.setAccountId(clean(form.getAccountId()));
        account.setProfileUrl(clean(form.getProfileUrl()));
        account.setAvatarUrl(clean(form.getAvatarUrl()));

        dotaApiClient.fetchProfileBySteamId(account.getSteamId())
                .ifPresent(externalProfile -> applyExternalData(account, externalProfile));

        DotaAccount saved = dotaAccountRepository.save(account);
        log.info("DotaAccount saved: userId={}, dotaAccountId={}, steamId={}", user.getId(), saved.getId(), saved.getSteamId());
        return saved;
    }

    @Transactional
    public void deleteForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        DotaAccount account = dotaAccountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new DotaAccountNotFoundException("Dota account not found for user: " + username));

        dotaAccountRepository.delete(account);
        log.info("DotaAccount deleted: userId={}, dotaAccountId={}", user.getId(), account.getId());
    }

    @Transactional(readOnly = true)
    public DotaAccountForm toForm(DotaAccount account) {
        DotaAccountForm form = new DotaAccountForm();
        form.setSteamId(account.getSteamId());
        form.setAccountId(account.getAccountId());
        form.setProfileUrl(account.getProfileUrl());
        form.setAvatarUrl(account.getAvatarUrl());
        return form;
    }

    @Transactional(readOnly = true)
    public DotaAccountViewDto toViewDto(DotaAccount account) {
        return new DotaAccountViewDto(
                account.getId(),
                account.getSteamId(),
                account.getAccountId(),
                account.getAvatarUrl(),
                account.getProfileUrl(),
                account.getMmr(),
                account.getLastSyncAt()
        );
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void applyExternalData(DotaAccount account, DotaExternalProfile externalProfile) {
        if (account.getAccountId() == null && externalProfile.accountId() != null) {
            account.setAccountId(externalProfile.accountId());
        }
        if (account.getProfileUrl() == null && externalProfile.profileUrl() != null) {
            account.setProfileUrl(externalProfile.profileUrl());
        }
        if (account.getAvatarUrl() == null && externalProfile.avatarUrl() != null) {
            account.setAvatarUrl(externalProfile.avatarUrl());
        }
    }
}
