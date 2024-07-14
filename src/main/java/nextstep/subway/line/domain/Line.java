package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.Station;

@Entity
public class Line {
    public static final String SECTION_NOT_FOUND = "섹션이 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = new LineName(name);
        this.color = new LineColor(color);
        this.sections = new Sections();
    }

    public Line(String name, String color) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
        this.sections = new Sections();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color.getValue();
    }

    public Line getUpdated(String name, String color) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
        return this;
    }

    public void addSection(Section section) {
        sections.addSection(this, section);
    }

    public void removeSection(Station station) {
        if (sections.isEmpty()) {
            throw new IllegalStateException(SECTION_NOT_FOUND);
        }

        sections.removeSection(station);
    }

    public List<Section> getUnmodifiableSections() {
        return sections.toUnmodifiableList();
    }

    public Section getLastSection() {
        return sections.getLastSection();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Line line = (Line)object;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}