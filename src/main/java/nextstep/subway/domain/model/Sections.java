package nextstep.subway.domain.model;

import static nextstep.subway.domain.model.Line.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.domain.service.SectionAdditionStrategy;

@Embeddable
public class Sections {
    public static final String ALREADY_EXISTING_SECTION_MESSAGE = "이미 추가된 구간입니다.";
    public static final String CANNOT_ADD_SECTION_MESSAGE = "구간을 추가할 수 없습니다.";
    public static final String CANNOT_REMOVE_SECTION_MESSAGE = "지하철 노선에 등록된 하행 종점역만 제거할 수 있습니다.";
    public static final String LAST_SECTION_CANNOT_BE_REMOVED_MESSAGE = "지하철 노선에 상행 종점역과 하행 종점역만 있는 경우 역을 삭제할 수 없습니다.";
    public static final String NOT_EXISTING_SECTION_MESSAGE = "존재하지 않는 구간입니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Line line, SectionAdditionStrategy sectionAdditionStrategy, Section newSection) {
        sectionAdditionStrategy.addSection(line, sections, newSection);
    }

    public Section getExistingSectionByUpStation(Section newSection) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXISTING_SECTION_MESSAGE));
    }

    public Section getExistingSectionByDownStation(Section newSection) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXISTING_SECTION_MESSAGE));
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

    public List<Section> getSections() {
        return sections;
    }
}
