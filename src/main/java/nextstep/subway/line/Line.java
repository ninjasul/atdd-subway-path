package nextstep.subway.line;

import nextstep.subway.line.section.AddType;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String color;
    @Embedded
    private Sections sections;
    @Column(nullable = false)
    private Long distance;

    protected Line() {
    }

    public Line(Long id,
                String name,
                String color,
                Sections sections,
                Long distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.distance = distance;
    }

    public Line(String name,
                String color,
                Station upStation,
                Station downStation,
                Long distance) {
        this.name = name;
        this.color = color;
        List<Section> list = new ArrayList<>();
        list.add(new Section(upStation, downStation, distance));
        this.sections = Sections.from(list);
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public Long getDistance() {
        return distance;
    }

    public void update(String name,
                       String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        AddType addType = this.sections.add(section);
        this.distance = section.calculateAddDistance(addType, this.distance);
    }

    public void deleteSection(Station station) {
        Section removedSection = this.sections.delete(station);
        this.distance = removedSection.calculateSubDistance(this.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections) && Objects.equals(distance, line.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections, distance);
    }

}
