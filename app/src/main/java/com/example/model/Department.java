package com.example.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "departments",
        indices = {@Index(value = {"code"}, unique = true)}
)
public class Department {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String code; // e.g. "CSE", "ECE"
    @NonNull
    private String name; // e.g. "Computer Science & Engineering"
    @NonNull
    private String years; // "1st Year,2nd Year,3rd Year,4th Year"
    @NonNull
    private String sections; // "Section A,Section B,Section C"

    public Department(@NonNull String id,
                      @NonNull String code,
                      @NonNull String name,
                      @NonNull String years,
                      @NonNull String sections) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.years = years;
        this.sections = sections;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getYears() {
        return years;
    }

    public void setYears(@NonNull String years) {
        this.years = years;
    }

    @NonNull
    public String getSections() {
        return sections;
    }

    public void setSections(@NonNull String sections) {
        this.sections = sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(years, that.years) &&
                Objects.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, years, sections);
    }
}
