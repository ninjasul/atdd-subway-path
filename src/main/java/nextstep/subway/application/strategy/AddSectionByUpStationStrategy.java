package nextstep.subway.application.strategy;

import static nextstep.subway.domain.model.Sections.*;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionByUpStationStrategy implements SectionAdditionStrategy {
    @Override
    public boolean canApply(Sections sections, Section newSection) {
        return sections.getSections()
            .stream()
            .anyMatch(oldSection -> canAddStationByUpStation(oldSection, newSection));
    }

    private boolean canAddStationByUpStation(Section oldSection, Section newSection) {
        return oldSection.getUpStation().equals(newSection.getUpStation()) &&
            !oldSection.getDownStation().equals(newSection.getDownStation()) &&
            !oldSection.getDownStation().equals(newSection.getUpStation()) &&
            hasValidDistance(oldSection, newSection);
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        Section existingSection = getExistingSectionByUpStation(sections, newSection);
        int newDistance = existingSection.getDistance() - newSection.getDistance();
        existingSection.updateDistance(newDistance);
        sections.add(newSection);
        newSection.setLine(line);
    }

    private Section getExistingSectionByUpStation(List<Section> sections, Section newSection) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(NOT_EXISTING_SECTION_MESSAGE));
    }
}
