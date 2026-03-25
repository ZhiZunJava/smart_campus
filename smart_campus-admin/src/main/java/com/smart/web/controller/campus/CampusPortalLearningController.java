package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScResourceAsset;
import com.smart.system.domain.ScResourceComment;
import com.smart.system.service.IScResourceService;

/**
 * 门户端学习资源接口
 */
@RestController
@RequestMapping("/campus/portal/learning")
public class CampusPortalLearningController extends BaseController {
    @Autowired
    private IScResourceService scResourceService;

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/list")
    public TableDataInfo resourceList(ScResource scResource) {
        startPage();
        List<ScResource> list = scResourceService.selectScResourceList(scResource);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/{resourceId}")
    public AjaxResult resourceDetail(@PathVariable Long resourceId) {
        return success(scResourceService.selectScResourceByResourceId(resourceId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/tree")
    public AjaxResult resourceTree(Long courseId) {
        return success(scResourceService.getCourseResourceTree(courseId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/asset/list")
    public AjaxResult resourceAssetList(ScResourceAsset scResourceAsset) {
        return success(scResourceService.selectScResourceAssetList(scResourceAsset));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/comment/list")
    public AjaxResult resourceCommentList(Long resourceId) {
        return success(scResourceService.selectScResourceCommentList(resourceId, getUserId()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PostMapping("/resource/comment")
    public AjaxResult addResourceComment(@RequestBody ScResourceComment scResourceComment) {
        scResourceComment.setUserId(getUserId());
        scResourceComment.setCreateBy(getUsername());
        return toAjax(scResourceService.addResourceComment(scResourceComment));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/{resourceId}/favorite")
    public AjaxResult favoriteStatus(@PathVariable Long resourceId) {
        return success(scResourceService.isFavoriteResource(resourceId, getUserId()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PostMapping("/resource/{resourceId}/favorite")
    public AjaxResult favoriteResource(@PathVariable Long resourceId) {
        if (scResourceService.isFavoriteResource(resourceId, getUserId())) {
            AjaxResult ajax = AjaxResult.success("已收藏");
            ajax.put("favorited", true);
            return ajax;
        }
        return toAjax(scResourceService.favoriteResource(resourceId, getUserId(), getUsername()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PostMapping("/resource/{resourceId}/unfavorite")
    public AjaxResult unfavoriteResource(@PathVariable Long resourceId) {
        return toAjax(scResourceService.unfavoriteResource(resourceId, getUserId()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/favorite/list")
    public TableDataInfo favoriteList() {
        startPage();
        List<ScResource> list = scResourceService.listFavoriteResources(getUserId());
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PostMapping("/resource/{resourceId}/rate")
    public AjaxResult rateResource(@PathVariable Long resourceId, Integer ratingScore) {
        return toAjax(scResourceService.rateResource(resourceId, getUserId(), ratingScore));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PostMapping("/resource/{resourceId}/share")
    public AjaxResult shareResource(@PathVariable Long resourceId) {
        return toAjax(scResourceService.shareResource(resourceId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PostMapping("/resource/comment/{commentId}/like")
    public AjaxResult likeComment(@PathVariable Long commentId) {
        return toAjax(scResourceService.toggleCommentLike(commentId, getUserId(), getUsername()));
    }
}
