package com.smart.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScTextbook extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long textbookId;
    private String textbookCode;
    private String textbookName;
    private String isbn;
    private String author;
    private String publisher;
    private String edition;
    private String publishDate;
    private BigDecimal price;
    private String coverUrl;
    private String category;
    private String subjectType;
    private String status;

    public Long getTextbookId() { return textbookId; }
    public void setTextbookId(Long textbookId) { this.textbookId = textbookId; }
    public String getTextbookCode() { return textbookCode; }
    public void setTextbookCode(String textbookCode) { this.textbookCode = textbookCode; }
    public String getTextbookName() { return textbookName; }
    public void setTextbookName(String textbookName) { this.textbookName = textbookName; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("textbookId", getTextbookId())
                .append("textbookName", getTextbookName())
                .append("isbn", getIsbn())
                .append("status", getStatus())
                .toString();
    }
}
