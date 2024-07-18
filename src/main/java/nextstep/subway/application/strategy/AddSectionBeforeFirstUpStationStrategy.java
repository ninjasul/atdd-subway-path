package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionBeforeUpStationStrategy implements SectionAdditionStrategy {

    @Override
    public Integer findAdditionSectionIndex(List<Section> sections, Section newSection) {
        if (sections.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (canAdd(currentSection, newSection)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean canAdd(Section existingSection, Section newSection) {
        return existingSection.hasSameUpStationWith(newSection.getDownStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        int index = findAdditionSectionIndex(sections, newSection);
        int actualAdditionIndex = Math.max(0, index - 1);
        sections.add(actualAdditionIndex, newSection);
    }
}


