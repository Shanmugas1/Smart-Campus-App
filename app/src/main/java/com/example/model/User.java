package com.example.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String registrationNumber;
    @NonNull
    private Role role;
    @NonNull
    private String department;
    @NonNull
    private String year;
    @NonNull
    private String section;
    @NonNull
    private String profileImage;
    private boolean active;
    private long createdAt;

    public User(@NonNull String id,
                @NonNull String name,
                @NonNull String email,
                @NonNull String password,
                @NonNull String registrationNumber,
                @NonNull Role role,
                @NonNull String department,
                @NonNull String year,
                @NonNull String section,
                @NonNull String profileImage,
                boolean active,
                long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.registrationNumber = registrationNumber;
        this.role = role;
        this.department = department;
        this.year = year;
        this.section = section;
        this.profileImage = profileImage;
        this.active = active;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @NonNull
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(@NonNull String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    @NonNull
    public Role getRole() {
        return role;
    }

    public void setRole(@NonNull Role role) {
        this.role = role;
    }

    @NonNull
    public String getDepartment() {
        return department;
    }

    public void setDepartment(@NonNull String department) {
        this.department = department;
    }

    @NonNull
    public String getYear() {
        return year;
    }

    public void setYear(@NonNull String year) {
        this.year = year;
    }

    @NonNull
    public String getSection() {
        return section;
    }

    public void setSection(@NonNull String section) {
        this.section = section;
    }

    @NonNull
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(@NonNull String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isActive() {
        return active;
    }

    @androidx.room.Ignore
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return active == user.active &&
                createdAt == user.createdAt &&
                Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(registrationNumber, user.registrationNumber) &&
                role == user.role &&
                Objects.equals(department, user.department) &&
                Objects.equals(year, user.year) &&
                Objects.equals(section, user.section) &&
                Objects.equals(profileImage, user.profileImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, registrationNumber, role, department, year, section, profileImage, active, createdAt);
    }
}
