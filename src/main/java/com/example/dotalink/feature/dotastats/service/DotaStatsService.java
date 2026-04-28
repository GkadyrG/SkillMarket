package com.example.dotalink.feature.dotastats.service;

import com.example.dotalink.common.exception.DotaAccountNotFoundException;
import com.example.dotalink.feature.dotaaccount.model.DotaAccount;
import com.example.dotalink.feature.dotaaccount.repository.DotaAccountRepository;
import com.example.dotalink.feature.dotastats.dto.HeroStatsDto;
import com.example.dotalink.feature.dotastats.dto.MatchDto;
import com.example.dotalink.feature.dotastats.dto.MatchResult;
import com.example.dotalink.feature.dotastats.dto.PlayerStatsDto;
import com.example.dotalink.feature.dotastats.model.DotaPlayerHeroStats;
import com.example.dotalink.feature.dotastats.model.DotaRecentMatch;
import com.example.dotalink.feature.dotastats.repository.DotaPlayerHeroStatsRepository;
import com.example.dotalink.feature.dotastats.repository.DotaRecentMatchRepository;
import com.example.dotalink.feature.hero.model.Hero;
import com.example.dotalink.feature.hero.repository.HeroRepository;
import com.example.dotalink.integration.opendota.OpenDotaClient;
import com.example.dotalink.integration.opendota.OpenDotaHeroResponse;
import com.example.dotalink.integration.opendota.OpenDotaPlayerHeroStatsResponse;
import com.example.dotalink.integration.opendota.OpenDotaRecentMatchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class DotaStatsService {

    private static final String UNKNOWN_HERO_PREFIX = "Hero";

    private final DotaAccountRepository dotaAccountRepository;
    private final DotaRecentMatchRepository dotaRecentMatchRepository;
    private final DotaPlayerHeroStatsRepository dotaPlayerHeroStatsRepository;
    private final HeroRepository heroRepository;
    private final OpenDotaClient openDotaClient;

    public DotaStatsService(DotaAccountRepository dotaAccountRepository,
                            DotaRecentMatchRepository dotaRecentMatchRepository,
                            DotaPlayerHeroStatsRepository dotaPlayerHeroStatsRepository,
                            HeroRepository heroRepository,
                            OpenDotaClient openDotaClient) {
        this.dotaAccountRepository = dotaAccountRepository;
        this.dotaRecentMatchRepository = dotaRecentMatchRepository;
        this.dotaPlayerHeroStatsRepository = dotaPlayerHeroStatsRepository;
        this.heroRepository = heroRepository;
        this.openDotaClient = openDotaClient;
    }

    @Transactional(readOnly = true)
    public PlayerStatsDto getPlayerStatsByUsername(String username) {
        DotaAccount account = dotaAccountRepository.findByUserUsername(username)
                .orElseThrow(() -> new DotaAccountNotFoundException("Dota account not found for user: " + username));

        return getPlayerStatsByAccountId(account.getAccountId());
    }

    @Transactional(readOnly = true)
    public PlayerStatsDto getPlayerStatsByAccountId(Long accountId) {
        DotaAccount account = dotaAccountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DotaAccountNotFoundException("Dota account not found for account_id: " + accountId));

        List<DotaRecentMatch> recentMatches = dotaRecentMatchRepository.findTop20ByAccountIdOrderByStartTimeDesc(accountId);
        List<DotaPlayerHeroStats> heroStats = dotaPlayerHeroStatsRepository.findByAccountIdOrderByGamesDesc(accountId);
        Map<Integer, String> heroNames = loadHeroNames();

        AverageStats averageStats = calculateAverageStats(recentMatches);

        return new PlayerStatsDto(
                account.getPersonaName(),
                account.getAvatarUrl(),
                formatRankTier(account.getRankTier()),
                account.getLeaderboardRank(),
                calculateWinrate(recentMatches),
                calculateAvgKda(recentMatches),
                averageStats.avgKills(),
                averageStats.avgDeaths(),
                averageStats.avgAssists(),
                buildTopHeroes(heroStats, heroNames),
                buildRecentMatches(recentMatches, heroNames)
        );
    }

    @Transactional
    public void syncPlayerStats(Long accountId) {
        syncHeroCatalogIfNeeded();
        List<OpenDotaRecentMatchResponse> recentMatches = openDotaClient.getRecentMatchesByAccountId(accountId);
        List<OpenDotaPlayerHeroStatsResponse> heroStats = openDotaClient.getPlayerHeroesByAccountId(accountId);

        dotaRecentMatchRepository.deleteByAccountId(accountId);
        dotaPlayerHeroStatsRepository.deleteByAccountId(accountId);

        dotaRecentMatchRepository.saveAll(recentMatches.stream()
                .map(match -> toRecentMatchEntity(accountId, match))
                .toList());

        dotaPlayerHeroStatsRepository.saveAll(heroStats.stream()
                .map(hero -> toHeroStatsEntity(accountId, hero))
                .toList());
    }

    @Transactional
    public void syncPlayerStatsIfMissing(Long accountId) {
        boolean missingRecentMatches = dotaRecentMatchRepository.countByAccountId(accountId) == 0;
        boolean missingHeroStats = dotaPlayerHeroStatsRepository.countByAccountId(accountId) == 0;
        if (missingRecentMatches || missingHeroStats) {
            syncPlayerStats(accountId);
        }
    }

    public double calculateWinrate(List<DotaRecentMatch> matches) {
        if (matches == null || matches.isEmpty()) {
            return 0.0;
        }

        long wins = matches.stream()
                .filter(match -> resolveMatchResult(match) == MatchResult.WIN)
                .count();

        return round((wins * 100.0) / matches.size());
    }

    public double calculateAvgKda(List<DotaRecentMatch> matches) {
        if (matches == null || matches.isEmpty()) {
            return 0.0;
        }

        return round(matches.stream()
                .mapToDouble(this::calculateMatchKda)
                .average()
                .orElse(0.0));
    }

    public AverageStats calculateAverageStats(List<DotaRecentMatch> matches) {
        if (matches == null || matches.isEmpty()) {
            return new AverageStats(0.0, 0.0, 0.0);
        }

        return new AverageStats(
                calculateAverage(matches, match -> safeInt(match.getKills())),
                calculateAverage(matches, match -> safeInt(match.getDeaths())),
                calculateAverage(matches, match -> safeInt(match.getAssists()))
        );
    }

    public String formatRankTier(Integer rankTier) {
        if (rankTier == null || rankTier <= 0) {
            return "-";
        }

        int majorRank = rankTier / 10;
        int star = rankTier % 10;

        String rankName = switch (majorRank) {
            case 1 -> "Herald";
            case 2 -> "Guardian";
            case 3 -> "Crusader";
            case 4 -> "Archon";
            case 5 -> "Legend";
            case 6 -> "Ancient";
            case 7 -> "Divine";
            case 8 -> "Immortal";
            default -> "Unknown rank";
        };

        if (majorRank >= 8 || star == 0) {
            return rankName;
        }

        return rankName + " " + star;
    }

    private List<MatchDto> buildRecentMatches(List<DotaRecentMatch> matches, Map<Integer, String> heroNames) {
        return matches.stream()
                .map(match -> new MatchDto(
                        resolveHeroName(match.getHeroId(), heroNames),
                        safeInt(match.getKills()),
                        safeInt(match.getDeaths()),
                        safeInt(match.getAssists()),
                        round(calculateMatchKda(match)),
                        round(safeInt(match.getDurationSeconds()) / 60.0),
                        resolveMatchResult(match),
                        match.getStartTime()
                ))
                .toList();
    }

    private List<HeroStatsDto> buildTopHeroes(List<DotaPlayerHeroStats> heroStats, Map<Integer, String> heroNames) {
        return heroStats.stream()
                .sorted((left, right) -> Long.compare(safeLong(right.getGames()), safeLong(left.getGames())))
                .limit(5)
                .map(stat -> new HeroStatsDto(
                        resolveHeroName(stat.getHeroId(), heroNames),
                        safeLong(stat.getGames()),
                        safeLong(stat.getWins()),
                        calculateHeroWinrate(stat)
                ))
                .toList();
    }

    private Map<Integer, String> loadHeroNames() {
        return heroRepository.findAll().stream()
                .collect(Collectors.toMap(Hero::getDotaHeroId, Hero::getName, (left, right) -> left));
    }

    @Transactional
    public void syncHeroCatalogIfNeeded() {
        if (heroRepository.count() > 20) {
            return;
        }

        Map<Integer, Hero> existing = new HashMap<>();
        heroRepository.findAll().forEach(hero -> existing.put(hero.getDotaHeroId(), hero));

        List<Hero> heroesToSave = openDotaClient.getHeroes().stream()
                .map(response -> mergeHero(existing.get(response.id()), response))
                .toList();

        heroRepository.saveAll(heroesToSave);
    }

    private double calculateAverage(List<DotaRecentMatch> matches, ToDoubleFunction<DotaRecentMatch> extractor) {
        return round(matches.stream()
                .mapToDouble(extractor)
                .average()
                .orElse(0.0));
    }

    private double calculateMatchKda(DotaRecentMatch match) {
        return (safeInt(match.getKills()) + safeInt(match.getAssists())) / (double) Math.max(1, safeInt(match.getDeaths()));
    }

    private double calculateHeroWinrate(DotaPlayerHeroStats stat) {
        long games = safeLong(stat.getGames());
        if (games == 0) {
            return 0.0;
        }
        return round((safeLong(stat.getWins()) * 100.0) / games);
    }

    private MatchResult resolveMatchResult(DotaRecentMatch match) {
        boolean isRadiant = safeInt(match.getPlayerSlot()) < 128;
        boolean radiantWin = Boolean.TRUE.equals(match.getRadiantWin());
        boolean win = (radiantWin && isRadiant) || (!radiantWin && !isRadiant);
        return win ? MatchResult.WIN : MatchResult.LOSE;
    }

    private String resolveHeroName(Integer heroId, Map<Integer, String> heroNames) {
        return heroNames.getOrDefault(heroId, UNKNOWN_HERO_PREFIX + " #" + heroId);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private DotaRecentMatch toRecentMatchEntity(Long accountId, OpenDotaRecentMatchResponse match) {
        DotaRecentMatch entity = new DotaRecentMatch();
        entity.setAccountId(accountId);
        entity.setMatchId(match.matchId());
        entity.setHeroId(match.heroId());
        entity.setKills(match.kills());
        entity.setDeaths(match.deaths());
        entity.setAssists(match.assists());
        entity.setDurationSeconds(match.duration());
        entity.setPlayerSlot(match.playerSlot());
        entity.setRadiantWin(match.radiantWin());
        entity.setStartTime(match.startTime() == null ? Instant.EPOCH : Instant.ofEpochSecond(match.startTime()));
        return entity;
    }

    private DotaPlayerHeroStats toHeroStatsEntity(Long accountId, OpenDotaPlayerHeroStatsResponse hero) {
        DotaPlayerHeroStats entity = new DotaPlayerHeroStats();
        entity.setAccountId(accountId);
        entity.setHeroId(hero.heroId());
        entity.setGames(hero.games());
        entity.setWins(hero.win());
        return entity;
    }

    private Hero mergeHero(Hero existingHero, OpenDotaHeroResponse response) {
        Hero hero = existingHero == null ? new Hero() : existingHero;
        hero.setDotaHeroId(response.id());
        hero.setName(response.localizedName() == null || response.localizedName().isBlank()
                ? response.name()
                : response.localizedName());
        String imageUrl = response.img() != null && !response.img().isBlank()
                ? "https://api.opendota.com" + response.img()
                : null;
        hero.setImageUrl(imageUrl);
        return hero;
    }

    public record AverageStats(
            double avgKills,
            double avgDeaths,
            double avgAssists
    ) {
    }
}
