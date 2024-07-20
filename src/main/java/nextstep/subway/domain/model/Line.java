package nextstep.subway.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.util.Pair;

import nextstep.subway.domain.service.SectionAdditionStrategy;

@Entity
public class Line {
    public static final String  FIRST_SECTION_NOT_FOUND_MESSAGE = "첫번째 구간을 찾을 수 없습니다.";

    public static final String  LAST_SECTION_NOT_FOUND_MESSAGE = "마지막 구간을 찾을 수 없습니다.";

    public static final String SECTION_NOT_FOUND_MESSAGE = "구간을 찾을 수 없습니다.";

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

    public void addSection(SectionAdditionStrategy sectionAdditionStrategy, Section section) {
        sections.addSection(this, sectionAdditionStrategy, section);
    }

    public void removeSection(Station station) {
        if (sections.isEmpty()) {
            throw new IllegalStateException(SECTION_NOT_FOUND_MESSAGE);
        }

        sections.removeSection(station);
    }

    private Section getFirstSection() {
        return sections.getSections().stream()
            .filter(section -> sections.getSections().stream()
                .noneMatch(s -> s.getDownStation().equals(section.getUpStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(FIRST_SECTION_NOT_FOUND_MESSAGE));
    }

    public Section getLastSection() {
        return sections.getSections().stream()
            .filter(section -> sections.getSections().stream()
                .noneMatch(s -> s.getUpStation().equals(section.getDownStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(LAST_SECTION_NOT_FOUND_MESSAGE));
    }

    public List<Station> getOrderedUnmodifiableStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();
        Section firstSection = getFirstSection(stationToSectionMaps.getSecond());
        List<Station> orderedStations = getOrderedStations(firstSection, stationToSectionMaps.getFirst());
        return Collections.unmodifiableList(orderedStations);
    }

    private Pair<Map<Station, Section>, Map<Station, Section>> getStationToSectionMaps() {
        Map<Station, Section> upStationToSectionMap = new HashMap<>();
        Map<Station, Section> downStationToSectionMap = new HashMap<>();

        for (Section section : sections.getSections()) {
            upStationToSectionMap.put(section.getUpStation(), section);
            downStationToSectionMap.put(section.getDownStation(), section);
        }

        return Pair.of(upStationToSectionMap, downStationToSectionMap);
    }

    private List<Station> getOrderedStations(Section firstSection, Map<Station, Section> upStationToSectionMap) {
        List<Station> orderedStations = new ArrayList<>();
        Section currentSection = firstSection;

        while (currentSection != null) {
            orderedStations.add(currentSection.getUpStation());
            Section nextSection = upStationToSectionMap.get(currentSection.getDownStation());

            if (nextSection == null) {
                orderedStations.add(currentSection.getDownStation());
            }

            currentSection = nextSection;
        }

        return orderedStations;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Section> getUnmodifiableSections() {
        return sections.toUnmodifiableList();
    }

    public List<Section> getOrderedUnmodifiableSections() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();
        Section firstSection = getFirstSection(stationToSectionMaps.getSecond());
        List<Section> orderedSections = getOrderedSections(firstSection, stationToSectionMaps);
        return Collections.unmodifiableList(orderedSections);
    }

    private List<Section> getOrderedSections(
        Section firstSection,
        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps
    ) {
        List<Section> orderedSections = new ArrayList<>();
        Section currentSection = firstSection;

        while (currentSection != null) {
            orderedSections.add(currentSection);
            currentSection = stationToSectionMaps.getFirst().get(currentSection.getDownStation());
        }
        return orderedSections;
    }

    private Section getFirstSection(Map<Station, Section> downStationToSectionMap) {
        return sections.getSections()
            .stream()
            .filter(section -> !downStationToSectionMap.containsKey(section.getUpStation()))
            .findFirst()
            .orElse(null);
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