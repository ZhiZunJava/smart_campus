package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScExamSession;
import com.smart.system.domain.ScUserAchievement;
import com.smart.system.domain.ScUserPointAccount;
import com.smart.system.domain.ScUserPointLedger;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.mapper.ScExamRecordMapper;
import com.smart.system.mapper.ScExamSessionMapper;
import com.smart.system.mapper.ScUserAchievementMapper;
import com.smart.system.mapper.ScUserPointAccountMapper;
import com.smart.system.mapper.ScUserPointLedgerMapper;
import com.smart.system.service.IScUserGrowthService;

@Service
public class ScUserGrowthServiceImpl implements IScUserGrowthService {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal PASS_SCORE_LINE = BigDecimal.valueOf(60);
    private static final BigDecimal EXCELLENT_SCORE_LINE = BigDecimal.valueOf(90);
    private static final BigDecimal EFFICIENT_SCORE_LINE = BigDecimal.valueOf(85);
    private static final double EFFICIENT_DURATION_RATIO = 0.60D;

    private static final int EXAM_SUBMIT_REWARD = 10;
    private static final int EXAM_PASS_REWARD = 6;
    private static final int EXAM_EXCELLENT_REWARD = 8;
    private static final int EXAM_PERFECT_REWARD = 12;
    private static final int EXAM_DISCIPLINE_REWARD = 4;
    private static final int EXAM_EFFICIENT_REWARD = 5;
    private static final int EXAM_PROGRESS_REWARD = 4;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private ScUserPointAccountMapper scUserPointAccountMapper;

    @Autowired
    private ScUserPointLedgerMapper scUserPointLedgerMapper;

    @Autowired
    private ScUserAchievementMapper scUserAchievementMapper;

    @Autowired
    private ScExamRecordMapper scExamRecordMapper;

    @Autowired
    private ScExamPaperMapper scExamPaperMapper;

    @Autowired
    private ScExamSessionMapper scExamSessionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rewardExamSubmit(Long userId, Long recordId, String operator) {
        if (userId == null || recordId == null) {
            return;
        }
        ScExamRecord record = scExamRecordMapper.selectScExamRecordByRecordId(recordId);
        if (record == null || !Objects.equals(userId, record.getUserId())
                || !"SUBMITTED".equalsIgnoreCase(StringUtils.defaultIfEmpty(record.getExamStatus(), ""))) {
            return;
        }

        Map<Long, ScExamPaper> paperCache = new HashMap<>();
        Map<Long, ScExamSession> sessionCache = new HashMap<>();
        ScExamPaper paper = resolvePaper(record.getPaperId(), paperCache);
        ScExamSession session = resolveSession(record.getSessionId(), sessionCache);
        BigDecimal previousBestScore = resolvePreviousBestScore(record);
        ExamSnapshot snapshot = evaluateExamSnapshot(record, paper, session, previousBestScore,
                previousBestScore.compareTo(ZERO) > 0);
        List<RewardGrant> grants = buildRewardGrants(snapshot);

        ScUserPointAccount account = ensureAccount(userId, operator);
        int availablePoints = safeInt(account.getAvailablePoints());
        int totalPoints = safeInt(account.getTotalPoints());
        boolean changed = false;
        for (RewardGrant grant : grants) {
            if (grant.getPoints() <= 0) {
                continue;
            }
            ScUserPointLedger ledger = new ScUserPointLedger();
            ledger.setUserId(userId);
            ledger.setChangeType("EARN");
            ledger.setBizType(grant.getBizType());
            ledger.setBizId(recordId);
            ledger.setChangePoints(grant.getPoints());
            ledger.setBalanceAfter(availablePoints + grant.getPoints());
            ledger.setCreateBy(operator);
            ledger.setRemark(grant.getRemark());
            try {
                scUserPointLedgerMapper.insertScUserPointLedger(ledger);
                availablePoints += grant.getPoints();
                totalPoints += grant.getPoints();
                changed = true;
            } catch (DuplicateKeyException ignored) {
                // Keep submitExam idempotent for the same record.
            }
        }

        if (changed) {
            account.setTotalPoints(totalPoints);
            account.setAvailablePoints(availablePoints);
            account.setLevelCode(resolveLevelCode(totalPoints));
            account.setUpdateBy(operator);
            scUserPointAccountMapper.updateScUserPointAccount(account);
        }

        unlockAchievements(userId, collectExamGrowthStats(resolveSubmittedRecords(userId), paperCache, sessionCache), operator);
    }

    @Override
    public Map<String, Object> buildGrowthSummary(Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        ScUserPointAccount account = scUserPointAccountMapper.selectScUserPointAccountByUserId(userId);
        if (account == null) {
            account = new ScUserPointAccount();
            account.setUserId(userId);
            account.setTotalPoints(0);
            account.setAvailablePoints(0);
            account.setUsedPoints(0);
            account.setLevelCode("L1");
        }
        ScUserAchievement achievementQuery = new ScUserAchievement();
        achievementQuery.setUserId(userId);
        achievementQuery.setStatus("1");
        List<ScUserAchievement> achievements = scUserAchievementMapper.selectScUserAchievementList(achievementQuery);

        ScUserPointLedger ledgerQuery = new ScUserPointLedger();
        ledgerQuery.setUserId(userId);
        List<ScUserPointLedger> ledgers = scUserPointLedgerMapper.selectScUserPointLedgerList(ledgerQuery);

        Map<Long, ScExamPaper> paperCache = new HashMap<>();
        Map<Long, ScExamSession> sessionCache = new HashMap<>();
        ExamGrowthStats stats = collectExamGrowthStats(resolveSubmittedRecords(userId), paperCache, sessionCache);

        result.put("account", account);
        result.put("achievements", achievements);
        result.put("recentLedgers", ledgers.stream().limit(20).toList());
        result.put("levelInfo", buildLevelInfo(safeInt(account.getTotalPoints()), resolveLevelCode(safeInt(account.getTotalPoints()))));
        result.put("stats", buildStatsPayload(stats, achievements.size()));
        result.put("honorScore", calculateHonorScore(stats, achievements.size()));
        result.put("monthlyPoints", buildMonthlyPoints(ledgers));
        result.put("honorRecords", buildHonorRecords(stats, ledgers));
        result.put("rewardRules", buildRewardRules());
        return result;
    }

    private Map<String, Object> buildStatsPayload(ExamGrowthStats stats, int achievementCount) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("achievementCount", achievementCount);
        payload.put("submittedExamCount", stats.getSubmittedCount());
        payload.put("passedExamCount", stats.getPassedCount());
        payload.put("excellentExamCount", stats.getExcellentCount());
        payload.put("perfectExamCount", stats.getPerfectCount());
        payload.put("cleanExamCount", stats.getCleanCount());
        payload.put("efficientExamCount", stats.getEfficientCount());
        payload.put("improvedExamCount", stats.getImprovedCount());
        payload.put("avgExamScore", stats.getAvgScore());
        payload.put("bestExamScore", stats.getBestScore());
        payload.put("avgElapsedMinutes", stats.getAvgElapsedMinutes());
        return payload;
    }

    private Map<String, Object> buildLevelInfo(int totalPoints, String levelCode) {
        int currentFloor = resolveLevelFloor(levelCode);
        Integer nextThreshold = resolveNextLevelThreshold(levelCode);
        String nextLevelCode = resolveNextLevelCode(levelCode);
        int progressPercent;
        int pointsToNext;
        if (nextThreshold == null) {
            progressPercent = 100;
            pointsToNext = 0;
        } else {
            int range = Math.max(1, nextThreshold - currentFloor);
            progressPercent = Math.min(100, Math.max(0, (int) Math.round((totalPoints - currentFloor) * 100D / range)));
            pointsToNext = Math.max(0, nextThreshold - totalPoints);
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("currentLevelCode", levelCode);
        payload.put("currentLevelName", resolveLevelName(levelCode));
        payload.put("currentFloor", currentFloor);
        payload.put("nextLevelCode", nextLevelCode);
        payload.put("nextLevelName", resolveLevelName(nextLevelCode));
        payload.put("nextLevelThreshold", nextThreshold);
        payload.put("pointsToNextLevel", pointsToNext);
        payload.put("progressPercent", progressPercent);
        return payload;
    }

    private List<Map<String, Object>> buildMonthlyPoints(List<ScUserPointLedger> ledgers) {
        Map<String, Integer> monthMap = new LinkedHashMap<>();
        YearMonth currentMonth = YearMonth.now();
        for (int offset = 5; offset >= 0; offset--) {
            YearMonth month = currentMonth.minusMonths(offset);
            monthMap.put(MONTH_FORMATTER.format(month), 0);
        }
        for (ScUserPointLedger ledger : ledgers) {
            if (ledger == null || ledger.getCreateTime() == null) {
                continue;
            }
            YearMonth month = YearMonth.from(ledger.getCreateTime().toInstant().atZone(ZoneId.systemDefault()));
            String key = MONTH_FORMATTER.format(month);
            if (!monthMap.containsKey(key)) {
                continue;
            }
            monthMap.put(key, monthMap.get(key) + safeInt(ledger.getChangePoints()));
        }
        List<Map<String, Object>> result = new ArrayList<>();
        monthMap.forEach((month, points) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", month);
            item.put("points", points);
            result.add(item);
        });
        return result;
    }

    private List<Map<String, Object>> buildHonorRecords(ExamGrowthStats stats, List<ScUserPointLedger> ledgers) {
        Map<Long, Integer> rewardPointMap = ledgers.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getBizId() != null)
                .filter(item -> StringUtils.defaultIfEmpty(item.getBizType(), "").startsWith("EXAM_"))
                .collect(Collectors.groupingBy(ScUserPointLedger::getBizId, LinkedHashMap::new,
                        Collectors.summingInt(item -> safeInt(item.getChangePoints()))));

        return stats.getSnapshots().stream()
                .sorted(Comparator
                        .comparingInt((ExamSnapshot item) -> calculateHonorWeight(item,
                                rewardPointMap.getOrDefault(item.getRecord().getRecordId(), 0)))
                        .reversed()
                        .thenComparing(item -> resolveRecordSortTime(item.getRecord()),
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(6)
                .map(item -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("recordId", item.getRecord().getRecordId());
                    row.put("paperId", item.getRecord().getPaperId());
                    row.put("paperName", resolvePaperName(item.getRecord(), item.getPaper()));
                    row.put("score", item.getScore());
                    row.put("correctRate", item.getCorrectRate());
                    row.put("submitTime", item.getRecord().getSubmitTime());
                    row.put("elapsedMinutes", item.getElapsedMinutes());
                    row.put("pointsAwarded", rewardPointMap.getOrDefault(item.getRecord().getRecordId(), 0));
                    row.put("badges", buildHonorBadges(item));
                    return row;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildHonorBadges(ExamSnapshot snapshot) {
        List<Map<String, Object>> badges = new ArrayList<>();
        if (snapshot.isPerfect()) {
            badges.add(honorBadge("满分", "success"));
        } else if (snapshot.isExcellent()) {
            badges.add(honorBadge("高分", "primary"));
        } else if (snapshot.isPassed()) {
            badges.add(honorBadge("通过", "info"));
        }
        if (snapshot.isClean()) {
            badges.add(honorBadge("规范作答", "warning"));
        }
        if (snapshot.isEfficient()) {
            badges.add(honorBadge("高效完成", "success"));
        }
        if (snapshot.isImproved()) {
            badges.add(honorBadge("进步显著", "primary"));
        }
        return badges;
    }

    private Map<String, Object> honorBadge(String label, String tone) {
        Map<String, Object> badge = new LinkedHashMap<>();
        badge.put("label", label);
        badge.put("tone", tone);
        return badge;
    }

    private int calculateHonorWeight(ExamSnapshot snapshot, int awardedPoints) {
        int weight = Math.max(0, awardedPoints * 2);
        if (snapshot.isPerfect()) {
            weight += 100;
        } else if (snapshot.isExcellent()) {
            weight += 70;
        } else if (snapshot.isPassed()) {
            weight += 25;
        }
        if (snapshot.isClean()) {
            weight += 18;
        }
        if (snapshot.isEfficient()) {
            weight += 16;
        }
        if (snapshot.isImproved()) {
            weight += 12;
        }
        weight += snapshot.getScore().setScale(0, RoundingMode.HALF_UP).intValue();
        return weight;
    }

    private int calculateHonorScore(ExamGrowthStats stats, int achievementCount) {
        double avgScorePart = Math.min(38D, stats.getAvgScore().doubleValue() * 0.38D);
        double achievementPart = Math.min(24D, achievementCount * 8D);
        double excellentPart = Math.min(18D, stats.getExcellentCount() * 4.5D);
        double perfectPart = Math.min(10D, stats.getPerfectCount() * 5D);
        double disciplinePart = Math.min(10D, (stats.getCleanCount() + stats.getEfficientCount()) * 2.5D);
        return (int) Math.round(Math.min(100D,
                avgScorePart + achievementPart + excellentPart + perfectPart + disciplinePart));
    }

    private List<Map<String, Object>> buildRewardRules() {
        List<Map<String, Object>> rules = new ArrayList<>();
        rules.add(rewardRule("EXAM_SUBMIT", "完成交卷", EXAM_SUBMIT_REWARD, "每次成功提交考试都会获得基础积分。"));
        rules.add(rewardRule("EXAM_PASS", "通过考试", EXAM_PASS_REWARD, "成绩达到及格线后追加通过奖励。"));
        rules.add(rewardRule("EXAM_EXCELLENT", "高分表现", EXAM_EXCELLENT_REWARD, "考试分数或正确率达到 90% 以上。"));
        rules.add(rewardRule("EXAM_PERFECT", "满分答卷", EXAM_PERFECT_REWARD, "考试获得满分或正确率达到 100%。"));
        rules.add(rewardRule("EXAM_DISCIPLINE", "规范作答", EXAM_DISCIPLINE_REWARD, "考试过程中无切屏、无异常标记。"));
        rules.add(rewardRule("EXAM_EFFICIENT", "高效完成", EXAM_EFFICIENT_REWARD, "在限定时长的 60% 内完成且成绩优秀。"));
        rules.add(rewardRule("EXAM_PROGRESS", "进步跃迁", EXAM_PROGRESS_REWARD, "同一试卷较历史最佳成绩提升 10 分及以上。"));
        return rules;
    }

    private Map<String, Object> rewardRule(String code, String title, int points, String desc) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("code", code);
        item.put("title", title);
        item.put("points", points);
        item.put("desc", desc);
        return item;
    }

    private ScUserPointAccount ensureAccount(Long userId, String operator) {
        ScUserPointAccount account = scUserPointAccountMapper.selectScUserPointAccountByUserId(userId);
        if (account != null) {
            return account;
        }
        account = new ScUserPointAccount();
        account.setUserId(userId);
        account.setTotalPoints(0);
        account.setAvailablePoints(0);
        account.setUsedPoints(0);
        account.setLevelCode("L1");
        account.setCreateBy(operator);
        scUserPointAccountMapper.insertScUserPointAccount(account);
        return account;
    }

    private void unlockAchievements(Long userId, ExamGrowthStats stats, String operator) {
        if (stats.getSubmittedCount() >= 1) {
            unlockAchievement(userId, "EXAM_FIRST", "考试达人·初启", "完成首次考试提交", stats.getSubmittedCount(), operator);
        }
        if (stats.getSubmittedCount() >= 10) {
            unlockAchievement(userId, "EXAM_10", "考试达人", "累计完成 10 次考试提交", stats.getSubmittedCount(), operator);
        }
        if (stats.getExcellentCount() >= 1) {
            unlockAchievement(userId, "SCORE_90", "高分突围", "首次在考试中拿到 90 分以上或正确率达到 90%",
                    stats.getExcellentCount(), operator);
        }
        if (stats.getPerfectCount() >= 1) {
            unlockAchievement(userId, "PERFECT_100", "满分时刻", "在一次考试中交出满分答卷", stats.getPerfectCount(),
                    operator);
        }
        if (stats.getCleanCount() >= 1) {
            unlockAchievement(userId, "EXAM_CLEAN", "专注作答", "考试全程无切屏、无异常标记", stats.getCleanCount(), operator);
        }
        if (stats.getEfficientCount() >= 1) {
            unlockAchievement(userId, "EXAM_EFFICIENT", "高效通关", "在规定时长内高质量完成考试", stats.getEfficientCount(),
                    operator);
        }
        if (stats.getImprovedCount() >= 1) {
            unlockAchievement(userId, "EXAM_PROGRESS", "进步跃迁", "同一试卷较历史成绩有明显提升", stats.getImprovedCount(),
                    operator);
        }
    }

    private ExamGrowthStats collectExamGrowthStats(List<ScExamRecord> submittedRecords,
            Map<Long, ScExamPaper> paperCache, Map<Long, ScExamSession> sessionCache) {
        ExamGrowthStats stats = new ExamGrowthStats();
        if (submittedRecords.isEmpty()) {
            return stats;
        }
        Map<Long, BigDecimal> bestScoreByPaper = new HashMap<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        long totalElapsedMinutes = 0L;
        int elapsedSamples = 0;

        for (ScExamRecord record : submittedRecords) {
            ScExamPaper paper = resolvePaper(record.getPaperId(), paperCache);
            ScExamSession session = resolveSession(record.getSessionId(), sessionCache);
            BigDecimal previousBestScore = bestScoreByPaper.getOrDefault(record.getPaperId(), ZERO);
            boolean hasPrevious = bestScoreByPaper.containsKey(record.getPaperId());
            ExamSnapshot snapshot = evaluateExamSnapshot(record, paper, session, previousBestScore, hasPrevious);
            stats.getSnapshots().add(snapshot);
            stats.setSubmittedCount(stats.getSubmittedCount() + 1);
            if (snapshot.isPassed()) {
                stats.setPassedCount(stats.getPassedCount() + 1);
            }
            if (snapshot.isExcellent()) {
                stats.setExcellentCount(stats.getExcellentCount() + 1);
            }
            if (snapshot.isPerfect()) {
                stats.setPerfectCount(stats.getPerfectCount() + 1);
            }
            if (snapshot.isClean()) {
                stats.setCleanCount(stats.getCleanCount() + 1);
            }
            if (snapshot.isEfficient()) {
                stats.setEfficientCount(stats.getEfficientCount() + 1);
            }
            if (snapshot.isImproved()) {
                stats.setImprovedCount(stats.getImprovedCount() + 1);
            }
            totalScore = totalScore.add(snapshot.getScore());
            if (snapshot.getElapsedMinutes() > 0) {
                totalElapsedMinutes += snapshot.getElapsedMinutes();
                elapsedSamples++;
            }
            if (snapshot.getScore().compareTo(stats.getBestScore()) > 0) {
                stats.setBestScore(snapshot.getScore());
            }
            if (snapshot.getScore().compareTo(previousBestScore) > 0) {
                bestScoreByPaper.put(record.getPaperId(), snapshot.getScore());
            }
        }

        stats.setAvgScore(totalScore.divide(BigDecimal.valueOf(stats.getSubmittedCount()), 2, RoundingMode.HALF_UP));
        stats.setAvgElapsedMinutes(elapsedSamples <= 0
                ? BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(totalElapsedMinutes).divide(BigDecimal.valueOf(elapsedSamples), 1,
                        RoundingMode.HALF_UP));
        return stats;
    }

    private ExamSnapshot evaluateExamSnapshot(ScExamRecord record, ScExamPaper paper, ScExamSession session,
            BigDecimal previousBestScore, boolean hasPrevious) {
        BigDecimal score = safeScore(record == null ? null : record.getScore());
        BigDecimal correctRate = safeScore(record == null ? null : record.getCorrectRate());
        long elapsedSeconds = resolveElapsedSeconds(record == null ? null : record.getStartTime(),
                record == null ? null : record.getSubmitTime());
        long elapsedMinutes = resolveElapsedMinutes(record == null ? null : record.getStartTime(),
                record == null ? null : record.getSubmitTime());
        boolean passed = isPassed(score, paper);
        boolean excellent = score.compareTo(EXCELLENT_SCORE_LINE) >= 0
                || correctRate.compareTo(EXCELLENT_SCORE_LINE) >= 0;
        boolean perfect = isPerfect(score, correctRate, paper);
        boolean clean = session != null && safeInt(session.getFocusLossCount()) <= 0 && safeInt(session.getFlaggedCount()) <= 0;
        boolean efficient = isEfficient(score, elapsedSeconds, paper);
        boolean improved = hasPrevious && score.subtract(previousBestScore).compareTo(BigDecimal.TEN) >= 0;
        return new ExamSnapshot(record, paper, session, score, correctRate, elapsedSeconds, elapsedMinutes,
                passed, excellent, perfect, clean, efficient, improved);
    }

    private List<RewardGrant> buildRewardGrants(ExamSnapshot snapshot) {
        List<RewardGrant> grants = new ArrayList<>();
        grants.add(new RewardGrant("EXAM_SUBMIT", EXAM_SUBMIT_REWARD, "完成考试提交"));
        if (snapshot.isPassed()) {
            grants.add(new RewardGrant("EXAM_PASS", EXAM_PASS_REWARD, "考试通过奖励"));
        }
        if (snapshot.isExcellent()) {
            grants.add(new RewardGrant("EXAM_EXCELLENT", EXAM_EXCELLENT_REWARD, "高分表现奖励"));
        }
        if (snapshot.isPerfect()) {
            grants.add(new RewardGrant("EXAM_PERFECT", EXAM_PERFECT_REWARD, "满分答卷奖励"));
        }
        if (snapshot.isClean()) {
            grants.add(new RewardGrant("EXAM_DISCIPLINE", EXAM_DISCIPLINE_REWARD, "规范作答奖励"));
        }
        if (snapshot.isEfficient()) {
            grants.add(new RewardGrant("EXAM_EFFICIENT", EXAM_EFFICIENT_REWARD, "高效完成奖励"));
        }
        if (snapshot.isImproved()) {
            grants.add(new RewardGrant("EXAM_PROGRESS", EXAM_PROGRESS_REWARD, "成绩进步奖励"));
        }
        return grants;
    }

    private BigDecimal resolvePreviousBestScore(ScExamRecord currentRecord) {
        if (currentRecord == null || currentRecord.getUserId() == null || currentRecord.getPaperId() == null) {
            return ZERO;
        }
        ScExamRecord query = new ScExamRecord();
        query.setUserId(currentRecord.getUserId());
        query.setPaperId(currentRecord.getPaperId());
        return scExamRecordMapper.selectScExamRecordList(query).stream()
                .filter(Objects::nonNull)
                .filter(item -> !"ONGOING".equalsIgnoreCase(StringUtils.defaultIfEmpty(item.getExamStatus(), "")))
                .filter(item -> !Objects.equals(item.getRecordId(), currentRecord.getRecordId()))
                .map(ScExamRecord::getScore)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private List<ScExamRecord> resolveSubmittedRecords(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        ScExamRecord query = new ScExamRecord();
        query.setUserId(userId);
        return scExamRecordMapper.selectScExamRecordList(query).stream()
                .filter(Objects::nonNull)
                .filter(item -> "SUBMITTED".equalsIgnoreCase(StringUtils.defaultIfEmpty(item.getExamStatus(), "")))
                .sorted(Comparator.comparing(this::resolveRecordSortTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private Date resolveRecordSortTime(ScExamRecord record) {
        if (record == null) {
            return null;
        }
        if (record.getSubmitTime() != null) {
            return record.getSubmitTime();
        }
        if (record.getStartTime() != null) {
            return record.getStartTime();
        }
        return record.getCreateTime();
    }

    private ScExamPaper resolvePaper(Long paperId, Map<Long, ScExamPaper> cache) {
        if (paperId == null) {
            return null;
        }
        if (cache.containsKey(paperId)) {
            return cache.get(paperId);
        }
        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(paperId);
        cache.put(paperId, paper);
        return paper;
    }

    private ScExamSession resolveSession(Long sessionId, Map<Long, ScExamSession> cache) {
        if (sessionId == null) {
            return null;
        }
        if (cache.containsKey(sessionId)) {
            return cache.get(sessionId);
        }
        ScExamSession session = scExamSessionMapper.selectScExamSessionBySessionId(sessionId);
        cache.put(sessionId, session);
        return session;
    }

    private boolean isPassed(BigDecimal score, ScExamPaper paper) {
        BigDecimal passLine = paper == null || paper.getPassScore() == null ? PASS_SCORE_LINE : paper.getPassScore();
        return safeScore(score).compareTo(passLine) >= 0;
    }

    private boolean isPerfect(BigDecimal score, BigDecimal correctRate, ScExamPaper paper) {
        BigDecimal safeScore = safeScore(score);
        BigDecimal safeCorrectRate = safeScore(correctRate);
        if (safeCorrectRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return true;
        }
        BigDecimal totalScore = paper == null ? null : paper.getTotalScore();
        return totalScore != null && totalScore.compareTo(BigDecimal.ZERO) > 0 && safeScore.compareTo(totalScore) >= 0;
    }

    private boolean isEfficient(BigDecimal score, long elapsedSeconds, ScExamPaper paper) {
        if (paper == null || paper.getDurationMinutes() == null || paper.getDurationMinutes() <= 0 || elapsedSeconds <= 0) {
            return false;
        }
        long efficientLimitSeconds = (long) Math.ceil(paper.getDurationMinutes() * 60D * EFFICIENT_DURATION_RATIO);
        return safeScore(score).compareTo(EFFICIENT_SCORE_LINE) >= 0 && elapsedSeconds <= efficientLimitSeconds;
    }

    private String resolvePaperName(ScExamRecord record, ScExamPaper paper) {
        if (record != null && StringUtils.isNotEmpty(record.getPaperName())) {
            return record.getPaperName();
        }
        if (paper != null && StringUtils.isNotEmpty(paper.getPaperName())) {
            return paper.getPaperName();
        }
        return record == null || record.getPaperId() == null ? "未命名试卷" : "试卷 " + record.getPaperId();
    }

    private long resolveElapsedSeconds(Date startTime, Date endTime) {
        if (startTime == null || endTime == null || endTime.before(startTime)) {
            return 0L;
        }
        return Math.max(0L, (endTime.getTime() - startTime.getTime()) / 1000L);
    }

    private long resolveElapsedMinutes(Date startTime, Date endTime) {
        long seconds = resolveElapsedSeconds(startTime, endTime);
        if (seconds <= 0) {
            return 0L;
        }
        return (seconds + 59L) / 60L;
    }

    private BigDecimal safeScore(BigDecimal value) {
        return value == null ? ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private void unlockAchievement(Long userId, String code, String title, String desc, Integer progressValue,
            String operator) {
        ScUserAchievement achievement = scUserAchievementMapper.selectScUserAchievementByUserAndCode(userId, code);
        if (achievement == null) {
            achievement = new ScUserAchievement();
            achievement.setUserId(userId);
            achievement.setAchievementCode(code);
            achievement.setAchievementTitle(title);
            achievement.setAchievementDesc(desc);
            achievement.setStatus("1");
            achievement.setProgressValue(progressValue);
            achievement.setEarnedTime(new Date());
            achievement.setCreateBy(operator);
            scUserAchievementMapper.insertScUserAchievement(achievement);
            return;
        }
        achievement.setAchievementTitle(title);
        achievement.setAchievementDesc(desc);
        achievement.setStatus("1");
        achievement.setProgressValue(progressValue);
        if (achievement.getEarnedTime() == null) {
            achievement.setEarnedTime(new Date());
        }
        achievement.setUpdateBy(operator);
        scUserAchievementMapper.updateScUserAchievement(achievement);
    }

    private String resolveLevelCode(Integer totalPoints) {
        int points = totalPoints == null ? 0 : totalPoints;
        if (points >= 500) {
            return "L4";
        }
        if (points >= 200) {
            return "L3";
        }
        if (points >= 50) {
            return "L2";
        }
        return "L1";
    }

    private int resolveLevelFloor(String levelCode) {
        String normalized = StringUtils.defaultIfEmpty(levelCode, "L1").toUpperCase();
        if ("L4".equals(normalized)) {
            return 500;
        }
        if ("L3".equals(normalized)) {
            return 200;
        }
        if ("L2".equals(normalized)) {
            return 50;
        }
        return 0;
    }

    private Integer resolveNextLevelThreshold(String levelCode) {
        String normalized = StringUtils.defaultIfEmpty(levelCode, "L1").toUpperCase();
        if ("L1".equals(normalized)) {
            return 50;
        }
        if ("L2".equals(normalized)) {
            return 200;
        }
        if ("L3".equals(normalized)) {
            return 500;
        }
        return null;
    }

    private String resolveNextLevelCode(String levelCode) {
        String normalized = StringUtils.defaultIfEmpty(levelCode, "L1").toUpperCase();
        if ("L1".equals(normalized)) {
            return "L2";
        }
        if ("L2".equals(normalized)) {
            return "L3";
        }
        if ("L3".equals(normalized)) {
            return "L4";
        }
        return "";
    }

    private String resolveLevelName(String levelCode) {
        String normalized = StringUtils.defaultIfEmpty(levelCode, "").toUpperCase();
        if ("L4".equals(normalized)) {
            return "荣誉领航";
        }
        if ("L3".equals(normalized)) {
            return "实战达人";
        }
        if ("L2".equals(normalized)) {
            return "进阶行者";
        }
        if ("L1".equals(normalized)) {
            return "启航新芽";
        }
        return "";
    }

    private static final class RewardGrant {
        private final String bizType;
        private final int points;
        private final String remark;

        private RewardGrant(String bizType, int points, String remark) {
            this.bizType = bizType;
            this.points = points;
            this.remark = remark;
        }

        public String getBizType() {
            return bizType;
        }

        public int getPoints() {
            return points;
        }

        public String getRemark() {
            return remark;
        }
    }

    private static final class ExamSnapshot {
        private final ScExamRecord record;
        private final ScExamPaper paper;
        private final ScExamSession session;
        private final BigDecimal score;
        private final BigDecimal correctRate;
        private final long elapsedSeconds;
        private final long elapsedMinutes;
        private final boolean passed;
        private final boolean excellent;
        private final boolean perfect;
        private final boolean clean;
        private final boolean efficient;
        private final boolean improved;

        private ExamSnapshot(ScExamRecord record, ScExamPaper paper, ScExamSession session, BigDecimal score,
                BigDecimal correctRate, long elapsedSeconds, long elapsedMinutes, boolean passed, boolean excellent,
                boolean perfect, boolean clean, boolean efficient, boolean improved) {
            this.record = record;
            this.paper = paper;
            this.session = session;
            this.score = score;
            this.correctRate = correctRate;
            this.elapsedSeconds = elapsedSeconds;
            this.elapsedMinutes = elapsedMinutes;
            this.passed = passed;
            this.excellent = excellent;
            this.perfect = perfect;
            this.clean = clean;
            this.efficient = efficient;
            this.improved = improved;
        }

        public ScExamRecord getRecord() {
            return record;
        }

        public ScExamPaper getPaper() {
            return paper;
        }

        public BigDecimal getScore() {
            return score;
        }

        public BigDecimal getCorrectRate() {
            return correctRate;
        }

        public long getElapsedSeconds() {
            return elapsedSeconds;
        }

        public long getElapsedMinutes() {
            return elapsedMinutes;
        }

        public boolean isPassed() {
            return passed;
        }

        public boolean isExcellent() {
            return excellent;
        }

        public boolean isPerfect() {
            return perfect;
        }

        public boolean isClean() {
            return clean;
        }

        public boolean isEfficient() {
            return efficient;
        }

        public boolean isImproved() {
            return improved;
        }
    }

    private static final class ExamGrowthStats {
        private final List<ExamSnapshot> snapshots = new ArrayList<>();
        private int submittedCount;
        private int passedCount;
        private int excellentCount;
        private int perfectCount;
        private int cleanCount;
        private int efficientCount;
        private int improvedCount;
        private BigDecimal avgScore = ZERO;
        private BigDecimal bestScore = ZERO;
        private BigDecimal avgElapsedMinutes = BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);

        public List<ExamSnapshot> getSnapshots() {
            return snapshots;
        }

        public int getSubmittedCount() {
            return submittedCount;
        }

        public void setSubmittedCount(int submittedCount) {
            this.submittedCount = submittedCount;
        }

        public int getPassedCount() {
            return passedCount;
        }

        public void setPassedCount(int passedCount) {
            this.passedCount = passedCount;
        }

        public int getExcellentCount() {
            return excellentCount;
        }

        public void setExcellentCount(int excellentCount) {
            this.excellentCount = excellentCount;
        }

        public int getPerfectCount() {
            return perfectCount;
        }

        public void setPerfectCount(int perfectCount) {
            this.perfectCount = perfectCount;
        }

        public int getCleanCount() {
            return cleanCount;
        }

        public void setCleanCount(int cleanCount) {
            this.cleanCount = cleanCount;
        }

        public int getEfficientCount() {
            return efficientCount;
        }

        public void setEfficientCount(int efficientCount) {
            this.efficientCount = efficientCount;
        }

        public int getImprovedCount() {
            return improvedCount;
        }

        public void setImprovedCount(int improvedCount) {
            this.improvedCount = improvedCount;
        }

        public BigDecimal getAvgScore() {
            return avgScore;
        }

        public void setAvgScore(BigDecimal avgScore) {
            this.avgScore = avgScore;
        }

        public BigDecimal getBestScore() {
            return bestScore;
        }

        public void setBestScore(BigDecimal bestScore) {
            this.bestScore = bestScore;
        }

        public BigDecimal getAvgElapsedMinutes() {
            return avgElapsedMinutes;
        }

        public void setAvgElapsedMinutes(BigDecimal avgElapsedMinutes) {
            this.avgElapsedMinutes = avgElapsedMinutes;
        }
    }
}
