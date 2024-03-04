package fr.uge.revue.model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class TestsReview {
    @Id
    @GeneratedValue
    private long id;
    private long succeededCount;
    private long totalCount;
    @ElementCollection
    @CollectionTable
    private List<String> errors;

    public TestsReview() {
    }

    public TestsReview(long succeededCount, long totalCount) {
        this.succeededCount = succeededCount;
        this.totalCount = totalCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSucceededCount() {
        return succeededCount;
    }

    public void setSucceededCount(long succeededCount) {
        this.succeededCount = succeededCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = Objects.requireNonNull(errors);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TestsReview t && t.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TestsReview{" +
                "id=" + id +
                ", succeededCount=" + succeededCount +
                ", totalCount=" + totalCount +
                '}';
    }
}
