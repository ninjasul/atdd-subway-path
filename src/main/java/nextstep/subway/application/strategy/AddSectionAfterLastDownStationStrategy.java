package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionAfterLastDownStationStrategy implements SectionAdditionStrategy {

    @Override
    public Integer findAdditionSectionIndex(List<Section> sections, Section newSection) {
        if (sections.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (canAdd(currentSection, newSection, sections.size() - 1, i)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean canAdd(Section existingSection, Section newSection, int maxIndex, int index) {
        return index == maxIndex &&
            existingSection.hasSameDownStationWith(newSection.getUpStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getDownStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        sections.add(newSection);
    }
}


