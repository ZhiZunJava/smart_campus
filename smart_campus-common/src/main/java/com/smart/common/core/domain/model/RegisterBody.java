package com.smart.common.core.domain.model;

/**
 * 用户注册对象
 * 
 * @author ruoyi
 */
public class RegisterBody extends LoginBody
{
    private String userType;
    private String realName;
    private String phonenumber;
    private String email;
    private String studentNo;
    private String teacherNo;
    private Integer admissionYear;
    private String learningGoal;
    private String major;
    private String sex;

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo;
    }

    public String getTeacherNo()
    {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo)
    {
        this.teacherNo = teacherNo;
    }

    public Integer getAdmissionYear()
    {
        return admissionYear;
    }

    public void setAdmissionYear(Integer admissionYear)
    {
        this.admissionYear = admissionYear;
    }

    public String getLearningGoal()
    {
        return learningGoal;
    }

    public void setLearningGoal(String learningGoal)
    {
        this.learningGoal = learningGoal;
    }

    public String getMajor()
    {
        return major;
    }

    public void setMajor(String major)
    {
        this.major = major;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }
}
