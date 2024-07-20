package nextstep.subway.domain.model;

import static nextstep.subway.domain.model.Line.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.springframework.data.util.Pair;

import nextstep.subway.domain.service.SectionAdditionStrategy;

@Embeddable
public class Sections {
    public static final String ALREADY_EXISTING_SECTION_MESSAGE = "이미 추가된 구간입니다.";
    public static final String CANNOT_ADD_SAME_STATIONS_MESSAGE = "상행역과 하행역이 같습니다.";
    public static final String CANNOT_ADD_SECTION_MESSAGE = "구간을 추가할 수 없습니다.";
    public static final String CANNOT_REMOVE_SECTION_MESSAGE = "지하철 노선에 등록된 하행 종점역만 제거할 수 있습니다.";
    public static final String LAST_SECTION_CANNOT_BE_REMOVED_MESSAGE = "지하철 노선에 상행 종점역과 하행 종점역만 있는 경우 역을 삭제할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
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

    public List<Section> getOrderedUnmodifiableSections() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Pair<Map<Station, Section>, Map<Station, Section>> stationToSectionMaps = getStationToSectionMaps();
        Section firstSection = getFirstSection(stationToSectionMaps.getSecond());
        List<Section> orderedSections = getOrderedSections(firstSection, stationToSectionMaps);
        return Collections.unmodifiableList(orderedSections);
    }

    private Pair<Map<Station, Section>, Map<Station, Section>> getStationToSectionMaps() {
        Map<Station, Section> upStationToSectionMap = new HashMap<>();
        Map<Station, Section> downStationToSectionMap = new HashMap<>();

        for (Section section : sections) {
            upStationToSectionMap.put(section.getUpStation(), section);
            downStationToSectionMap.put(section.getDownStation(), section);
        }

        return Pair.of(upStationToSectionMap, downStationToSectionMap);
    }

    private Section getFirstSection(Map<Station, Section> downStationToSectionMap) {
        return sections
            .stream()
            .filter(section -> !downStationToSectionMap.containsKey(section.getUpStation()))
            .findFirst()
            .orElse(null);
    }

    public Optional<Section> getLastSection() {
        if (sections.isEmpty()) {
            return Optional.empty();
        }

        return getLastSection(getStationToSectionMaps().getFirst());
    }

    private Optional<Section> getLastSection(Map<Station, Section> upStationToSectionMap) {
        return sections
            .stream()
            .filter(section -> !upStationToSectionMap.containsKey(section.getDownStation()))
            .findFirst();
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

    public void addSection(Line line, Section newSection) {
        validateSectionAddition(newSection);
        newSection.updateLine(line);
        sections.add(newSection);
    }

    public void addSection(Line line, SectionAdditionStrategy sectionAdditionStrategy, Section newSection) {
        validateSectionAddition(newSection);
        newSection.updateLine(line);
        sectionAdditionStrategy.addSection(line, sections, newSection);
    }

    private void validateSectionAddition(Section newSection) {
        if (sections.contains(newSection)) {
            throw new IllegalArgumentException(ALREADY_EXISTING_SECTION_MESSAGE);
        }

        if (newSection.areBothStationsSame()) {
            throw new IllegalArgumentException(Sections.CANNOT_ADD_SAME_STATIONS_MESSAGE);
        }
    }

    private Station getLastDownStation() {
        if (sections.isEmpty()) {
            throw new IllegalStateException(SECTION_NOT_FOUND_MESSAGE);
        }

        return sections.get(sections.size() - 1).getDownStation();
    }

    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException(LAST_SECTION_CANNOT_BE_REMOVED_MESSAGE);
        }

        if (!getLastDownStation().equals(station)) {
            throw new IllegalArgumentException(CANNOT_REMOVE_SECTION_MESSAGE);
        }

        Section sectionToRemove = sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(CANNOT_REMOVE_SECTION_MESSAGE));

        sections.remove(sectionToRemove);
    }

    public List<Section> toUnmodifiableList() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(sections);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Section> getSections() {
        return sections;
    }
}
