package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.common.utils.SecurityUtils;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScResourceAsset;
import com.smart.system.domain.ScResourceComment;
import com.smart.system.domain.ScResourceNode;
import com.smart.system.service.IScResourceService;

@RestController
@RequestMapping("/campus/resource")
public class ScResourceController extends BaseController {
    @Autowired
    private IScResourceService scResourceService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(ScResource scResource) {
        startPage();
        List<ScResource> list = scResourceService.selectScResourceList(scResource);
        return getDataTable(list);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{resourceId}")
    public AjaxResult getInfo(@PathVariable Long resourceId) {
        return success(scResourceService.selectScResourceByResourceId(resourceId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/operation/overview")
    public AjaxResult operationOverview(@RequestParam(required = false) Long courseId,
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        return success(scResourceService.getResourceOperationOverview(courseId, limit));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/tree")
    public AjaxResult courseTree(@RequestParam Long courseId) {
        return success(scResourceService.getCourseResourceTree(courseId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/tree/init/{courseId}")
    public AjaxResult initTree(@PathVariable Long courseId) {
        return toAjax(scResourceService.initCourseResourceRoot(courseId));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:add')")
    @Log(title = "课程资源目录", businessType = BusinessType.INSERT)
    @PostMapping("/node")
    public AjaxResult addNode(@Validated @RequestBody ScResourceNode scResourceNode) {
        scResourceNode.setCreateBy(getUsername());
        return toAjax(scResourceService.insertScResourceNode(scResourceNode));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:edit')")
    @Log(title = "课程资源目录", businessType = BusinessType.UPDATE)
    @PutMapping("/node")
    public AjaxResult editNode(@Validated @RequestBody ScResourceNode scResourceNode) {
        scResourceNode.setUpdateBy(getUsername());
        return toAjax(scResourceService.updateScResourceNode(scResourceNode));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:remove')")
    @Log(title = "课程资源目录", businessType = BusinessType.DELETE)
    @DeleteMapping("/node/{nodeId}")
    public AjaxResult removeNode(@PathVariable Long nodeId) {
        int rows = scResourceService.deleteScResourceNodeByNodeId(nodeId);
        return rows > 0 ? success() : error("节点下仍有关联文章或子节点，暂不能删除");
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:add')")
    @Log(title = "文章课时", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScResource scResource) {
        scResource.setCreateBy(getUsername());
        return toAjax(scResourceService.insertScResource(scResource));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:edit')")
    @Log(title = "文章课时", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScResource scResource) {
        scResource.setUpdateBy(getUsername());
        return toAjax(scResourceService.updateScResource(scResource));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:remove')")
    @Log(title = "文章课时", businessType = BusinessType.DELETE)
    @DeleteMapping("/{resourceIds}")
    public AjaxResult remove(@PathVariable Long[] resourceIds) {
        return toAjax(scResourceService.deleteScResourceByResourceIds(resourceIds));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/asset/list")
    public AjaxResult assetList(ScResourceAsset scResourceAsset) {
        return success(scResourceService.selectScResourceAssetList(scResourceAsset));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:add')")
    @Log(title = "资源资产", businessType = BusinessType.INSERT)
    @PostMapping("/asset")
    public AjaxResult addAsset(@Validated @RequestBody ScResourceAsset scResourceAsset) {
        scResourceAsset.setCreateBy(getUsername());
        return toAjax(scResourceService.insertScResourceAsset(scResourceAsset));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:edit')")
    @Log(title = "资源资产", businessType = BusinessType.UPDATE)
    @PutMapping("/asset")
    public AjaxResult editAsset(@Validated @RequestBody ScResourceAsset scResourceAsset) {
        scResourceAsset.setUpdateBy(getUsername());
        return toAjax(scResourceService.updateScResourceAsset(scResourceAsset));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:remove')")
    @Log(title = "资源资产", businessType = BusinessType.DELETE)
    @DeleteMapping("/asset/{assetId}")
    public AjaxResult removeAsset(@PathVariable Long assetId) {
        return toAjax(scResourceService.deleteScResourceAssetByAssetId(assetId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/list")
    public AjaxResult commentList(@RequestParam Long resourceId) {
        return success(scResourceService.selectScResourceCommentList(resourceId, SecurityUtils.getUserId()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment")
    public AjaxResult addComment(@RequestBody ScResourceComment scResourceComment) {
        scResourceComment.setUserId(SecurityUtils.getUserId());
        scResourceComment.setCreateBy(getUsername());
        return toAjax(scResourceService.addResourceComment(scResourceComment));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{resourceId}/favorite")
    public AjaxResult favorite(@PathVariable Long resourceId) {
        return toAjax(scResourceService.favoriteResource(resourceId, SecurityUtils.getUserId(), getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{resourceId}/rate")
    public AjaxResult rate(@PathVariable Long resourceId, @RequestParam Integer ratingScore) {
        return toAjax(scResourceService.rateResource(resourceId, SecurityUtils.getUserId(), ratingScore));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{resourceId}/share")
    public AjaxResult share(@PathVariable Long resourceId) {
        return toAjax(scResourceService.shareResource(resourceId));
    }
}
