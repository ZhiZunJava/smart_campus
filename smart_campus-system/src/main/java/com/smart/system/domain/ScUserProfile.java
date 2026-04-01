package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 智慧校园用户扩展档案对象 sc_user_profile
 *
 * @author can
 */
public class ScUserProfile extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long profileId;

    private Long userId;

    private String userType;

    private String studentNo;

    private String teacherNo;

    private String realName;

    /** 曾用名 */
    private String formerName;

    private String gender;

    /** 民族 */
    private String nation;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /** 政治面貌 */
    private String politicalStatus;

    /** 籍贯 */
    private String nativePlace;

    /** 证件类型 */
    private String idType;

    /** 证件号码 */
    private String idNumber;

    private Long gradeId;

    private Long classId;

    private Long advisorUserId;

    private String major;

    /** 培养类型 */
    private String cultivationType;

    /** 学历层次 */
    private String educationLevel;

    /** 学制(年) */
    private String schoolingLength;

    private Integer admissionYear;

    /** 录取方式 */
    private String admissionType;

    /** 考生号 */
    private String examNumber;

    /** 准考证号 */
    private String admissionTicketNo;

    /** 录取分数 */
    private BigDecimal admissionScore;

    /** 录取批次 */
    private String admissionBatch;

    /** 生源地 */
    private String sourceRegion;

    /** 毕业中学 */
    private String graduateSchool;

    private String learningGoal;

    private String interestTags;

    private String learningStyle;

    private String avatarUrl;

    /** 预计毕业时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedGraduationDate;

    /** 实际毕业时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualGraduationDate;

    /** 毕业证书编号 */
    private String diplomaNo;

    /** 结业/肄业标识 */
    private String completionType;

    /** 毕业去向 */
    private String graduationDestination;

    /** 毕业备注 */
    private String graduationRemark;

    /** 学位类别 */
    private String degreeType;

    /** 学位授予日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date degreeAwardDate;

    /** 学位证书编号 */
    private String degreeCertNo;

    /** 学位论文题目 */
    private String thesisTitle;

    /** 学位备注 */
    private String degreeRemark;

    /** 通讯地址 */
    private String contactAddress;

    /** 家庭住址 */
    private String homeAddress;

    /** 紧急联系人 */
    private String emergencyContact;

    /** 紧急联系电话 */
    private String emergencyPhone;

    /** 户口所在地 */
    private String householdAddress;

    /** 银行卡号 */
    private String bankCardNo;

    private String status;

    private String studentStatusCode;

    private String studentStatusName;

    private Date studentStatusUpdateTime;

    /** 班级名称（非持久化，由后端填充） */
    private String className;

    // ===================== Getters & Setters =====================

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getTeacherNo() { return teacherNo; }
    public void setTeacherNo(String teacherNo) { this.teacherNo = teacherNo; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getFormerName() { return formerName; }
    public void setFormerName(String formerName) { this.formerName = formerName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getNation() { return nation; }
    public void setNation(String nation) { this.nation = nation; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getPoliticalStatus() { return politicalStatus; }
    public void setPoliticalStatus(String politicalStatus) { this.politicalStatus = politicalStatus; }

    public String getNativePlace() { return nativePlace; }
    public void setNativePlace(String nativePlace) { this.nativePlace = nativePlace; }

    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getAdvisorUserId() { return advisorUserId; }
    public void setAdvisorUserId(Long advisorUserId) { this.advisorUserId = advisorUserId; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getCultivationType() { return cultivationType; }
    public void setCultivationType(String cultivationType) { this.cultivationType = cultivationType; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getSchoolingLength() { return schoolingLength; }
    public void setSchoolingLength(String schoolingLength) { this.schoolingLength = schoolingLength; }

    public Integer getAdmissionYear() { return admissionYear; }
    public void setAdmissionYear(Integer admissionYear) { this.admissionYear = admissionYear; }

    public String getAdmissionType() { return admissionType; }
    public void setAdmissionType(String admissionType) { this.admissionType = admissionType; }

    public String getExamNumber() { return examNumber; }
    public void setExamNumber(String examNumber) { this.examNumber = examNumber; }

    public String getAdmissionTicketNo() { return admissionTicketNo; }
    public void setAdmissionTicketNo(String admissionTicketNo) { this.admissionTicketNo = admissionTicketNo; }

    public BigDecimal getAdmissionScore() { return admissionScore; }
    public void setAdmissionScore(BigDecimal admissionScore) { this.admissionScore = admissionScore; }

    public String getAdmissionBatch() { return admissionBatch; }
    public void setAdmissionBatch(String admissionBatch) { this.admissionBatch = admissionBatch; }

    public String getSourceRegion() { return sourceRegion; }
    public void setSourceRegion(String sourceRegion) { this.sourceRegion = sourceRegion; }

    public String getGraduateSchool() { return graduateSchool; }
    public void setGraduateSchool(String graduateSchool) { this.graduateSchool = graduateSchool; }

    public String getLearningGoal() { return learningGoal; }
    public void setLearningGoal(String learningGoal) { this.learningGoal = learningGoal; }

    public String getInterestTags() { return interestTags; }
    public void setInterestTags(String interestTags) { this.interestTags = interestTags; }

    public String getLearningStyle() { return learningStyle; }
    public void setLearningStyle(String learningStyle) { this.learningStyle = learningStyle; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Date getExpectedGraduationDate() { return expectedGraduationDate; }
    public void setExpectedGraduationDate(Date expectedGraduationDate) { this.expectedGraduationDate = expectedGraduationDate; }

    public Date getActualGraduationDate() { return actualGraduationDate; }
    public void setActualGraduationDate(Date actualGraduationDate) { this.actualGraduationDate = actualGraduationDate; }

    public String getDiplomaNo() { return diplomaNo; }
    public void setDiplomaNo(String diplomaNo) { this.diplomaNo = diplomaNo; }

    public String getCompletionType() { return completionType; }
    public void setCompletionType(String completionType) { this.completionType = completionType; }

    public String getGraduationDestination() { return graduationDestination; }
    public void setGraduationDestination(String graduationDestination) { this.graduationDestination = graduationDestination; }

    public String getGraduationRemark() { return graduationRemark; }
    public void setGraduationRemark(String graduationRemark) { this.graduationRemark = graduationRemark; }

    public String getDegreeType() { return degreeType; }
    public void setDegreeType(String degreeType) { this.degreeType = degreeType; }

    public Date getDegreeAwardDate() { return degreeAwardDate; }
    public void setDegreeAwardDate(Date degreeAwardDate) { this.degreeAwardDate = degreeAwardDate; }

    public String getDegreeCertNo() { return degreeCertNo; }
    public void setDegreeCertNo(String degreeCertNo) { this.degreeCertNo = degreeCertNo; }

    public String getThesisTitle() { return thesisTitle; }
    public void setThesisTitle(String thesisTitle) { this.thesisTitle = thesisTitle; }

    public String getDegreeRemark() { return degreeRemark; }
    public void setDegreeRemark(String degreeRemark) { this.degreeRemark = degreeRemark; }

    public String getContactAddress() { return contactAddress; }
    public void setContactAddress(String contactAddress) { this.contactAddress = contactAddress; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public String getHouseholdAddress() { return householdAddress; }
    public void setHouseholdAddress(String householdAddress) { this.householdAddress = householdAddress; }

    public String getBankCardNo() { return bankCardNo; }
    public void setBankCardNo(String bankCardNo) { this.bankCardNo = bankCardNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentStatusCode() { return studentStatusCode; }
    public void setStudentStatusCode(String studentStatusCode) { this.studentStatusCode = studentStatusCode; }

    public String getStudentStatusName() { return studentStatusName; }
    public void setStudentStatusName(String studentStatusName) { this.studentStatusName = studentStatusName; }

    public Date getStudentStatusUpdateTime() { return studentStatusUpdateTime; }
    public void setStudentStatusUpdateTime(Date studentStatusUpdateTime) { this.studentStatusUpdateTime = studentStatusUpdateTime; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("profileId", getProfileId())
                .append("userId", getUserId())
                .append("userType", getUserType())
                .append("studentNo", getStudentNo())
                .append("teacherNo", getTeacherNo())
                .append("realName", getRealName())
                .append("formerName", getFormerName())
                .append("gender", getGender())
                .append("nation", getNation())
                .append("birthDate", getBirthDate())
                .append("politicalStatus", getPoliticalStatus())
                .append("nativePlace", getNativePlace())
                .append("idType", getIdType())
                .append("idNumber", getIdNumber())
                .append("gradeId", getGradeId())
                .append("classId", getClassId())
                .append("advisorUserId", getAdvisorUserId())
                .append("major", getMajor())
                .append("cultivationType", getCultivationType())
                .append("educationLevel", getEducationLevel())
                .append("schoolingLength", getSchoolingLength())
                .append("admissionYear", getAdmissionYear())
                .append("admissionType", getAdmissionType())
                .append("examNumber", getExamNumber())
                .append("admissionTicketNo", getAdmissionTicketNo())
                .append("admissionScore", getAdmissionScore())
                .append("admissionBatch", getAdmissionBatch())
                .append("sourceRegion", getSourceRegion())
                .append("graduateSchool", getGraduateSchool())
                .append("learningGoal", getLearningGoal())
                .append("interestTags", getInterestTags())
                .append("learningStyle", getLearningStyle())
                .append("avatarUrl", getAvatarUrl())
                .append("expectedGraduationDate", getExpectedGraduationDate())
                .append("actualGraduationDate", getActualGraduationDate())
                .append("diplomaNo", getDiplomaNo())
                .append("completionType", getCompletionType())
                .append("graduationDestination", getGraduationDestination())
                .append("graduationRemark", getGraduationRemark())
                .append("degreeType", getDegreeType())
                .append("degreeAwardDate", getDegreeAwardDate())
                .append("degreeCertNo", getDegreeCertNo())
                .append("thesisTitle", getThesisTitle())
                .append("degreeRemark", getDegreeRemark())
                .append("contactAddress", getContactAddress())
                .append("homeAddress", getHomeAddress())
                .append("emergencyContact", getEmergencyContact())
                .append("emergencyPhone", getEmergencyPhone())
                .append("householdAddress", getHouseholdAddress())
                .append("bankCardNo", getBankCardNo())
                .append("status", getStatus())
                .append("studentStatusCode", getStudentStatusCode())
                .append("studentStatusName", getStudentStatusName())
                .append("studentStatusUpdateTime", getStudentStatusUpdateTime())
                .append("className", getClassName())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
