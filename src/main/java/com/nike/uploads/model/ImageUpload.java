package com.nike.uploads.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fez on 7/28/2014.
 * Desc: Simple Model POJO class to represent Data
 */
public class ImageUpload implements Parcelable{

    long id;
    String title;
    String link;
    String mediaUrl;
    String mediaName;
    String dateTaken;
    String description;
    String published;
    String author;
    String authorId;
    String tags;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String media) {
        this.mediaUrl = media;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String date_taken) {
        this.dateTaken = date_taken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(mediaUrl);
        parcel.writeString(mediaName);
        parcel.writeString(dateTaken);
        parcel.writeString(description);
        parcel.writeString(published);
        parcel.writeString(author);
        parcel.writeString(authorId);
        parcel.writeString(tags);
    }

    public static final Parcelable.Creator<ImageUpload> CREATOR = new Creator<ImageUpload>() {
        public ImageUpload createFromParcel(Parcel source) {
            ImageUpload image= new ImageUpload();
            image.id = source.readLong();
            image.title = source.readString();
            image.link = source.readString();
            image.mediaUrl = source.readString();
            image.mediaName = source.readString();
            image.dateTaken = source.readString();
            image.description = source.readString();
            image.published = source.readString();
            image.author = source.readString();
            image.authorId = source.readString();
            image.tags = source.readString();
            return image;
        }
        public ImageUpload[] newArray(int size) {
            return new ImageUpload[size];
        }
    };

    public int describeContents() {
        return 0;
    }
}
