package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionBeforeFirstUpStationStrategy implements SectionAdditionStrategy {

    @Override
    public boolean canAdd(Section existingSection, Section newSection, int maxIndex, int index) {
        return index == 0 &&
            existingSection.hasSameUpStationWith(newSection.getDownStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        sections.add(0, newSection);
    }
}


