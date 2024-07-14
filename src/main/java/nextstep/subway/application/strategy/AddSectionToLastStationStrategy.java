package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionToLastStationStrategy implements SectionAdditionStrategy {
    @Override
    public boolean canApply(Sections sections, Section newSection) {
        return sections.isEmpty() ||
            sections.getSections()
            .stream()
            .anyMatch(oldSection -> canAddNewSectionToOldSection(oldSection, newSection));
    }

    private boolean canAddNewSectionToOldSection(Section oldSection, Section newSection) {
        return !oldSection.getUpStation().equals(newSection.getUpStation()) &&
            !oldSection.getDownStation().equals(newSection.getDownStation()) &&
            oldSection.getDownStation().equals(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        sections.add(newSection);
        newSection.setLine(line);
    }
}
