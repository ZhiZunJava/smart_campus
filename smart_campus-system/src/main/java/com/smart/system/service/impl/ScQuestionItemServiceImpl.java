package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScQuestionCatalogItemRel;
import com.smart.system.domain.ScQuestionItem;
import com.smart.system.domain.ScQuestionItemVersion;
import com.smart.system.domain.dto.QuestionItemUpsertDto;
import com.smart.system.mapper.ScQuestionCatalogItemRelMapper;
import com.smart.system.mapper.ScQuestionItemMapper;
import com.smart.system.mapper.ScQuestionItemVersionMapper;
import com.smart.system.service.IScQuestionItemService;

@Service
public class ScQuestionItemServiceImpl implements IScQuestionItemService {
    @Autowired
    private ScQuestionItemMapper scQuestionItemMapper;

    @Autowired
    private ScQuestionItemVersionMapper scQuestionItemVersionMapper;

    @Autowired
    private ScQuestionCatalogItemRelMapper scQuestionCatalogItemRelMapper;

    @Override
    public ScQuestionItem selectScQuestionItemByItemId(Long itemId) {
        return scQuestionItemMapper.selectScQuestionItemByItemId(itemId);
    }

    @Override
    public ScQuestionItem selectScQuestionItemBySource(String sourceType, Long sourceRefId) {
        return scQuestionItemMapper.selectScQuestionItemBySource(sourceType, sourceRefId);
    }

    @Override
    public ScQuestionItem selectLatestScQuestionItemBySourceRefId(Long sourceRefId) {
        return scQuestionItemMapper.selectLatestScQuestionItemBySourceRefId(sourceRefId);
    }

    @Override
    public List<ScQuestionItem> selectScQuestionItemList(ScQuestionItem scQuestionItem) {
        return scQuestionItemMapper.selectScQuestionItemList(scQuestionItem);
    }

    @Override
    public List<ScQuestionItemVersion> selectScQuestionItemVersionByItemId(Long itemId) {
        return scQuestionItemVersionMapper.selectScQuestionItemVersionByItemId(itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertScQuestionItem(QuestionItemUpsertDto dto, String operator) {
        validateDto(dto, false);
        ScQuestionItem item = buildItem(dto);
        item.setCreateBy(operator);
        item.setCurrentVersionNo(1);
        item.setLatestVersionId(0L);
        scQuestionItemMapper.insertScQuestionItem(item);

        ScQuestionItemVersion version = buildVersion(dto);
        version.setItemId(item.getItemId());
        version.setVersionNo(1);
        version.setCreateBy(operator);
        scQuestionItemVersionMapper.insertScQuestionItemVersion(version);

        item.setLatestVersionId(version.getVersionId());
        item.setUpdateBy(operator);
        scQuestionItemMapper.updateScQuestionItem(item);
        saveCatalogRelation(item.getItemId(), dto.getCatalogId(), operator);
        return item.getItemId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateScQuestionItem(QuestionItemUpsertDto dto, String operator) {
        validateDto(dto, true);
        ScQuestionItem current = scQuestionItemMapper.selectScQuestionItemByItemId(dto.getItemId());
        if (current == null) {
            throw new ServiceException("题目不存在");
        }

        int nextVersionNo = current.getCurrentVersionNo() == null ? 1 : current.getCurrentVersionNo() + 1;
        scQuestionItemVersionMapper.updateCurrentFlagByItemId(dto.getItemId());

        ScQuestionItemVersion version = buildVersion(dto);
        version.setItemId(dto.getItemId());
        version.setVersionNo(nextVersionNo);
        version.setCreateBy(operator);
        scQuestionItemVersionMapper.insertScQuestionItemVersion(version);

        ScQuestionItem update = buildItem(dto);
        update.setItemId(dto.getItemId());
        update.setLatestVersionId(version.getVersionId());
        update.setCurrentVersionNo(nextVersionNo);
        update.setUpdateBy(operator);
        scQuestionItemMapper.updateScQuestionItem(update);
        saveCatalogRelation(dto.getItemId(), dto.getCatalogId(), operator);
        return dto.getItemId();
    }

    private void validateDto(QuestionItemUpsertDto dto, boolean requireItemId) {
        if (dto == null) {
            throw new ServiceException("题目数据不能为空");
        }
        if (requireItemId && dto.getItemId() == null) {
            throw new ServiceException("题目ID不能为空");
        }
        if (StringUtils.isEmpty(dto.getQuestionType())) {
            throw new ServiceException("题型不能为空");
        }
        if (StringUtils.isEmpty(dto.getStem())) {
            throw new ServiceException("题干不能为空");
        }
        if (StringUtils.isEmpty(dto.getAnswer())) {
            throw new ServiceException("答案不能为空");
        }
    }

    private ScQuestionItem buildItem(QuestionItemUpsertDto dto) {
        ScQuestionItem item = new ScQuestionItem();
        item.setCourseId(dto.getCourseId());
        item.setDefaultCatalogId(dto.getCatalogId());
        item.setQuestionType(StringUtils.defaultIfEmpty(dto.getQuestionType(), "single"));
        item.setDifficultyLevel(dto.getDifficultyLevel() == null ? 3 : dto.getDifficultyLevel());
        item.setSourceType(StringUtils.defaultIfEmpty(dto.getSourceType(), "MANUAL"));
        item.setSourceRefId(dto.getSourceRefId());
        item.setSourceBatchNo(dto.getSourceBatchNo());
        item.setCreatorUserId(dto.getCreatorUserId());
        item.setStatus(StringUtils.defaultIfEmpty(dto.getStatus(), "0"));
        item.setRemark(dto.getRemark());
        return item;
    }

    private ScQuestionItemVersion buildVersion(QuestionItemUpsertDto dto) {
        ScQuestionItemVersion version = new ScQuestionItemVersion();
        version.setChapterId(dto.getChapterId());
        version.setKnowledgePointId(dto.getKnowledgePointId());
        version.setStem(dto.getStem());
        version.setAnswer(dto.getAnswer());
        version.setAnalysis(dto.getAnalysis());
        version.setMaterialContent(dto.getMaterialContent());
        version.setQuestionTags(dto.getQuestionTags());
        version.setQualityScore(dto.getQualityScore() == null ? BigDecimal.ZERO : dto.getQualityScore());
        version.setSource(dto.getSource());
        version.setChangeType(StringUtils.defaultIfEmpty(dto.getChangeType(), dto.getItemId() == null ? "CREATE" : "EDIT"));
        version.setIsCurrent("1");
        version.setAiModelId(dto.getAiModelId());
        version.setAiPromptSnapshot(dto.getAiPromptSnapshot());
        version.setRemark(dto.getRemark());
        return version;
    }

    private void saveCatalogRelation(Long itemId, Long catalogId, String operator) {
        if (itemId == null || catalogId == null) {
            return;
        }
        scQuestionCatalogItemRelMapper.deleteScQuestionCatalogItemRelByItemId(itemId);
        ScQuestionCatalogItemRel relation = new ScQuestionCatalogItemRel();
        relation.setCatalogId(catalogId);
        relation.setItemId(itemId);
        relation.setSortNo(0);
        relation.setStatus("0");
        relation.setCreateBy(operator);
        scQuestionCatalogItemRelMapper.insertScQuestionCatalogItemRel(relation);
    }
}
