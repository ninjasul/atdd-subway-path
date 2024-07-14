package nextstep.subway.domain.model;

import static nextstep.subway.domain.model.Line.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    public static final String ALREADY_EXISTING_SECTION_MESSAGE = "이미 추가된 구간입니다.";
    public static final String CANNOT_ADD_SECTION_MESSAGE = "구간을 추가할 수 없습니다.";
    public static final String INVALID_UP_STATION_MESSAGE = "새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.";
    public static final String DUPLICATE_DOWN_STATION_MESSAGE = "이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.";
    public static final String CANNOT_REMOVE_SECTION_MESSAGE = "지하철 노선에 등록된 하행 종점역만 제거할 수 있습니다.";
    public static final String LAST_SECTION_CANNOT_BE_REMOVED_MESSAGE = "지하철 노선에 상행 종점역과 하행 종점역만 있는 경우 역을 삭제할 수 없습니다.";
    public static final String NOT_EXISTING_SECTION_MESSAGE = "존재하지 않는 구간입니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Line line, Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException(ALREADY_EXISTING_SECTION_MESSAGE);
        }

        if (sections.isEmpty()) {
        }
        else if (canAddSectionByUpStation(section)) {
            Section existingSection = getExistingSectionByUpStation(section);
            updateSectionDistance(existingSection, section);
        } else if (canAddSectionByDownStation(section)) {
            Section existingSection = getExistingSectionByDownStation(section);
            updateSectionDistance(existingSection, section);
        } else if (canAddSectionToLastStation(section)) {
        } else {
            throw new IllegalArgumentException(CANNOT_ADD_SECTION_MESSAGE);
        }

        sections.add(section);
        section.setLine(line);
    }

    private Section getExistingSectionByUpStation(Section newSection) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXISTING_SECTION_MESSAGE));
    }

    private Section getExistingSectionByDownStation(Section newSection) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXISTING_SECTION_MESSAGE));
    }

    private void updateSectionDistance(Section existingSection, Section newSection) {
        int newDistance = existingSection.getDistance() - newSection.getDistance();
        if (newDistance <= 0) {
            throw new IllegalArgumentException(CANNOT_ADD_SECTION_MESSAGE);
        }

        Section updatedSection = new Section(
            existingSection.getLine(),
            newSection.getDownStation(),
            existingSection.getDownStation(),
            newDistance
        );

        sections.remove(existingSection);
        sections.add(updatedSection);
    }

    private boolean canAddSectionByUpStation(Section newSection) {
        return sections.stream()
            .anyMatch(oldSection -> canAddSectionByUpStation(oldSection, newSection));
    }

    private boolean canAddSectionByDownStation(Section newSection) {
        return sections.stream()
            .anyMatch(oldSection -> canAddStationByDownStation(oldSection, newSection));
    }

    private boolean canAddSectionByUpStation(Section oldSection, Section newSection) {
        return oldSection.getUpStation().equals(newSection.getUpStation()) &&
            hasValidDistance(oldSection, newSection);
    }

    private boolean canAddStationByDownStation(Section oldSection, Section newSection) {
        return oldSection.getDownStation().equals(newSection.getDownStation()) &&
            hasValidDistance(oldSection, newSection);
    }

    private static boolean hasValidDistance(Section oldSection, Section newSection) {
        return oldSection.getDistance() > newSection.getDistance();
    }

    private boolean canAddSectionToLastStation(Section newSection) {
        return sections.stream()
            .anyMatch(oldSection -> canAddNewSectionToOldSection(oldSection, newSection));
    }

    private boolean canAddNewSectionToOldSection(Section oldSection, Section newSection) {
        return oldSection.getDownStation().equals(newSection.getUpStation());
    }

    private boolean isValidUpStation(Station upStation) {
        return sections.isEmpty();
    }

    private boolean isStationAlreadyAdded(Station station) {
        return sections.stream()
            .anyMatch(section -> section.getUpStation().equals(station) || section.getDownStation().equals(station));
    }

    private Station getLastDownStation() {
        if (sections.isEmpty()) {
            throw new IllegalStateException(SECTION_NOT_FOUND);
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

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Section> toUnmodifiableList() {
        return List.copyOf(sections);
    }

    public Section getLastSection() {
        return Optional.of(sections)
            .map(it -> it.get(it.size() - 1))
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXISTING_SECTION_MESSAGE));
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
