package com.example.Frl;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "wo_task")
@Data
@NoArgsConstructor
public class WorkOrderTask implements Serializable {

    private static final long serialVersionUID = XXXXXXXXXXXXXXXXXL;

    @Id
    @Column(name = "wotask_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer woTaskId;

    @Column(name = "wo_id")
    private Integer woId;

    @Column(name = "category")
    private String category;

    @Column(name = "operation")
    private String operation;

    @Column(name = "img_update_tag")
    private String imgUpdateTag;

    @Column(name = "completed_flag")
    private String completedFlag;

    @Column(name = "completed_date")
    private Timestamp completedDate;

    @Column(name = "router_seq")
    private Integer routerSeq;

    @Column(name = "video_duration")
    private Integer videoDuration;

    @Column(name = "rating")
    private String rating;

    @Column(name = "inspec_comment")
    private String inspectionComment;

    @Column(name = "image_file")
    private String imageFile;

    @Column(name = "video_file")
    private String videoFile;

    @Column(name = "last_modifier_id")
    private Integer lastModifierId;

    @Column(name = "checklist_task_id")
    private Integer checklistTaskId;

    @Column(name = "checklist_category_id")
    private Integer checklistCategoryId;

    @Column(name = "disclaimer_text")
    private String disclaimerText;

    @Column(name = "checklist_component_id")
    private Integer checklistComponentId;

    @Column(name = "inspec_comment_json")
    private String inspectionCommentJson;

    @Column(name = "features_json")
    private String featuresJson;

    @Column(name = "catalog_json")
    private String catalogJson;

    @Column(name = "is_public_yn")
    @Type(type = "yes_no")
    private Boolean isPublic = true;

    @Column(name = "is_prelim_photo_yn")
    @Type(type = "yes_no")
    private Boolean isPrelimPhoto = false;

    @Column(name = "tags")
    private String tags;

    @Column(name = "photo_order_in_module")
    private Integer photoOrderInModule;

    @Column(name = "orig_image_width_in_pixels")
    private Integer origImageWidthInPixels;

    @Column(name = "orig_image_height_in_pixels")
    private Integer origImageHeightInPixels;

    @Column(name = "orig_file_size_in_bytes")
    private Long origFileSizeInBytes;

    @Column(name = "photo_uploaded_dt")
    private Timestamp photoUploadedDate;

    @Column(name = "primary_yn")
    @Type(type = "yes_no")
    private Boolean isPrimary = false;

    @Column(name = "video_id")
    private String videoId;

    @Column(name = "internal_spin_id")
    private String internalSpinId;

    @Column(name = "external_spin_id")
    private String externalSpinId;

    @Column(name = "hotspot_name")
    private String hotspotName;
    
    public WorkOrderTask(WorkOrderTask workOrderTask) {
        this.category = workOrderTask.getCategory();
        this.operation = workOrderTask.getOperation();
        this.routerSeq = workOrderTask.getRouterSeq();
        this.rating = workOrderTask.getRating();
        this.inspectionComment = workOrderTask.getInspectionComment();
        this.checklistTaskId = workOrderTask.getChecklistTaskId();
        this.checklistCategoryId = workOrderTask.getChecklistCategoryId();
        this.checklistComponentId = workOrderTask.getChecklistComponentId();
        this.inspectionCommentJson = workOrderTask.getInspectionCommentJson();
        this.featuresJson = workOrderTask.getFeaturesJson();
        this.catalogJson = workOrderTask.getCatalogJson();
        this.isPublic = workOrderTask.getIsPublic();
        this.tags = workOrderTask.getTags();
        this.lastModifierId = workOrderTask.getLastModifierId();
        this.imageFile = workOrderTask.getImageFile();
        this.photoOrderInModule = workOrderTask.getPhotoOrderInModule();
        this.isPrelimPhoto = workOrderTask.getIsPrelimPhoto();
        this.origFileSizeInBytes = workOrderTask.getOrigFileSizeInBytes();
        this.origImageHeightInPixels = workOrderTask.getOrigImageHeightInPixels();
        this.origImageWidthInPixels = workOrderTask.getOrigImageWidthInPixels();
        this.photoUploadedDate = workOrderTask.getPhotoUploadedDate();
        this.isPrimary = workOrderTask.getIsPrimary();
        this.videoFile = workOrderTask.getVideoFile();
        this.videoId = workOrderTask.getVideoId();
        this.internalSpinId = workOrderTask.getInternalSpinId();
        this.externalSpinId = workOrderTask.getExternalSpinId();
        this.imgUpdateTag = workOrderTask.getImgUpdateTag();
        this.completedFlag = workOrderTask.getCompletedFlag();
        this.completedDate = workOrderTask.getCompletedDate();
        this.videoDuration = workOrderTask.getVideoDuration();
        this.disclaimerText = workOrderTask.getDisclaimerText();
        this.hotspotName = workOrderTask.getHotspotName();
    }

    public void copyDataFrom(WorkOrderTask workOrderTask) {
        this.rating = workOrderTask.getRating();
        this.inspectionComment = workOrderTask.getInspectionComment();
        this.inspectionCommentJson = workOrderTask.getInspectionCommentJson();
        this.featuresJson = workOrderTask.getFeaturesJson();
        this.catalogJson = workOrderTask.getCatalogJson();
        this.isPublic = workOrderTask.getIsPublic();
        this.tags = workOrderTask.getTags();
        this.lastModifierId = workOrderTask.getLastModifierId();
        this.imageFile = workOrderTask.getImageFile();
        this.photoOrderInModule = workOrderTask.getPhotoOrderInModule();
        this.isPrelimPhoto = workOrderTask.getIsPrelimPhoto();
        this.origFileSizeInBytes = workOrderTask.getOrigFileSizeInBytes();
        this.origImageHeightInPixels = workOrderTask.getOrigImageHeightInPixels();
        this.origImageWidthInPixels = workOrderTask.getOrigImageWidthInPixels();
        this.photoUploadedDate = workOrderTask.getPhotoUploadedDate();
        this.isPrimary = workOrderTask.getIsPrimary();
        this.videoFile = workOrderTask.getVideoFile();
        this.videoId = workOrderTask.getVideoId();
        this.internalSpinId = workOrderTask.getInternalSpinId();
        this.externalSpinId = workOrderTask.getExternalSpinId();
        this.imgUpdateTag = workOrderTask.getImgUpdateTag();
        this.completedFlag = workOrderTask.getCompletedFlag();
        this.completedDate = workOrderTask.getCompletedDate();
        this.videoDuration = workOrderTask.getVideoDuration();
        this.disclaimerText = workOrderTask.getDisclaimerText();
        this.hotspotName = workOrderTask.getHotspotName();
    }

    public void resetMediaData() {
        setImageFile(null);
        setVideoFile(null);
        setVideoDuration(null);
        setVideoId(null);
        setOrigImageHeightInPixels(null);
        setOrigImageWidthInPixels(null);
        setOrigFileSizeInBytes(null);
        setExternalSpinId(null);
        setInternalSpinId(null);
        setImgUpdateTag(null);
        setPhotoUploadedDate(null);
        setIsPrimary(false);
        setPhotoOrderInModule(null);
    }
}
