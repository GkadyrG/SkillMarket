package com.example.dotalink.feature.dotaaccount.service;

import com.example.dotalink.common.exception.DotaAccountNotFoundException;
import com.example.dotalink.common.exception.ExternalApiException;
import com.example.dotalink.common.exception.UserNotFoundException;
import com.example.dotalink.feature.dotaaccount.dto.DotaAccountViewDto;
import com.example.dotalink.feature.dotaaccount.model.DotaAccount;
import com.example.dotalink.feature.dotaaccount.repository.DotaAccountRepository;
import com.example.dotalink.feature.dotastats.service.DotaStatsService;
import com.example.dotalink.feature.user.model.User;
import com.example.dotalink.feature.user.repository.UserRepository;
import com.example.dotalink.integration.opendota.OpenDotaClient;
import com.example.dotalink.integration.opendota.OpenDotaPlayerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DotaAccountService {

    private static final Logger log = LoggerFactory.getLogger(DotaAccountService.class);

    private final UserRepository userRepository;
    private final DotaAccountRepository dotaAccountRepository;
    private final OpenDotaClient openDotaClient;
    private final DotaStatsService dotaStatsService;

    public DotaAccountService(UserRepository userRepository,
                              DotaAccountRepository dotaAccountRepository,
                              OpenDotaClient openDotaClient,
                              DotaStatsService dotaStatsService) {
        this.userRepository = userRepository;
        this.dotaAccountRepository = dotaAccountRepository;
        this.openDotaClient = openDotaClient;
        this.dotaStatsService = dotaStatsService;
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
    public DotaAccount linkDotaAccount(String username, Long accountId) {
        validateAccountId(accountId);
        log.info("Starting Dota account linking: username={}, accountId={}", username, accountId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        OpenDotaPlayerResponse playerResponse = openDotaClient.getPlayerByAccountId(accountId);
        if (playerResponse.profile() == null) {
            throw new DotaAccountNotFoundException("Player profile for account_id " + accountId + " was not found");
        }

        DotaAccount account = dotaAccountRepository.findByUserId(user.getId()).orElseGet(DotaAccount::new);
        account.setUser(user);
        account.setAccountId(accountId);
        account.setPersonaName(playerResponse.profile().personaname());
        account.setAvatarUrl(playerResponse.profile().avatarfull());
        account.setProfileUrl(playerResponse.profile().profileurl());
        account.setRankTier(playerResponse.rank_tier());
        account.setLeaderboardRank(playerResponse.leaderboard_rank());
        account.setLastSyncAt(LocalDateTime.now());
        user.setDotaAccount(account);

        DotaAccount saved = dotaAccountRepository.save(account);
        try {
            dotaStatsService.syncPlayerStats(accountId);
        } catch (Exception ex) {
            log.warn("Dota account linked without stats sync: username={}, accountId={}, reason={}",
                    username, accountId, ex.getMessage());
        }
        log.info("Dota account linked successfully: username={}, accountId={}, dotaAccountId={}",
                username, accountId, saved.getId());
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
    public DotaAccountViewDto toViewDto(DotaAccount account) {
        return new DotaAccountViewDto(
                account.getId(),
                account.getAccountId(),
                account.getPersonaName(),
                account.getAvatarUrl(),
                account.getProfileUrl(),
                account.getRankTier(),
                account.getLeaderboardRank(),
                account.getLastSyncAt()
        );
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            log.warn("Invalid Dota account ID received: {}", accountId);
            throw new IllegalArgumentException("Dota account ID must be a positive number");
        }
    }
}
