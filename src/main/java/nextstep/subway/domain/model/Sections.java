package nextstep.subway.domain.model;

import static nextstep.subway.domain.model.Line.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

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
