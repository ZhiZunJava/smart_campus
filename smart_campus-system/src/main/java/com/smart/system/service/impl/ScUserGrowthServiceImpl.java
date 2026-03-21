package com.smart.system.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScUserAchievement;
import com.smart.system.domain.ScUserPointAccount;
import com.smart.system.domain.ScUserPointLedger;
import com.smart.system.mapper.ScExamRecordMapper;
import com.smart.system.mapper.ScUserAchievementMapper;
import com.smart.system.mapper.ScUserPointAccountMapper;
import com.smart.system.mapper.ScUserPointLedgerMapper;
import com.smart.system.service.IScUserGrowthService;

@Service
public class ScUserGrowthServiceImpl implements IScUserGrowthService {
    private static final int EXAM_SUBMIT_REWARD = 10;

    @Autowired
    private ScUserPointAccountMapper scUserPointAccountMapper;

    @Autowired
    private ScUserPointLedgerMapper scUserPointLedgerMapper;

    @Autowired
    private ScUserAchievementMapper scUserAchievementMapper;

    @Autowired
    private ScExamRecordMapper scExamRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rewardExamSubmit(Long userId, Long recordId, String operator) {
        if (userId == null || recordId == null) {
            return;
        }
        ScUserPointAccount account = ensureAccount(userId, operator);
        int newBalance = (account.getAvailablePoints() == null ? 0 : account.getAvailablePoints()) + EXAM_SUBMIT_REWARD;
        ScUserPointLedger ledger = new ScUserPointLedger();
        ledger.setUserId(userId);
        ledger.setChangeType("EARN");
        ledger.setBizType("EXAM_SUBMIT");
        ledger.setBizId(recordId);
        ledger.setChangePoints(EXAM_SUBMIT_REWARD);
        ledger.setBalanceAfter(newBalance);
        ledger.setCreateBy(operator);
        ledger.setRemark("考试提交奖励积分");
        try {
            scUserPointLedgerMapper.insertScUserPointLedger(ledger);
        } catch (DuplicateKeyException e) {
            return;
        }
        account.setTotalPoints((account.getTotalPoints() == null ? 0 : account.getTotalPoints()) + EXAM_SUBMIT_REWARD);
        account.setAvailablePoints(newBalance);
        account.setLevelCode(resolveLevelCode(account.getTotalPoints()));
        account.setUpdateBy(operator);
        scUserPointAccountMapper.updateScUserPointAccount(account);
        unlockAchievement(userId, "EXAM_FIRST", "考试达人·初启", "完成首次考试提交", 1, operator);
        unlockMilestoneAchievement(userId, operator);
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
        ScUserAchievement query = new ScUserAchievement();
        query.setUserId(userId);
        query.setStatus("1");
        List<ScUserAchievement> achievements = scUserAchievementMapper.selectScUserAchievementList(query);
        ScUserPointLedger ledgerQuery = new ScUserPointLedger();
        ledgerQuery.setUserId(userId);
        List<ScUserPointLedger> ledgers = scUserPointLedgerMapper.selectScUserPointLedgerList(ledgerQuery);
        result.put("account", account);
        result.put("achievements", achievements);
        result.put("recentLedgers", ledgers.stream().limit(20).toList());
        return result;
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

    private void unlockMilestoneAchievement(Long userId, String operator) {
        ScExamRecord query = new ScExamRecord();
        query.setUserId(userId);
        List<ScExamRecord> records = scExamRecordMapper.selectScExamRecordList(query);
        long submittedCount = records.stream().filter(item -> "SUBMITTED".equals(item.getExamStatus())).count();
        if (submittedCount >= 10) {
            unlockAchievement(userId, "EXAM_10", "考试达人", "累计完成10次考试", (int) submittedCount, operator);
        }
    }

    private void unlockAchievement(Long userId, String code, String title, String desc, Integer progressValue, String operator) {
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
}
