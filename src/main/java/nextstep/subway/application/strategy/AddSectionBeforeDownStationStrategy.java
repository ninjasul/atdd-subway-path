package nextstep.subway.application.strategy;

import static nextstep.subway.domain.model.Sections.*;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionByDownStationStrategy implements SectionAdditionStrategy {
    @Override
    public boolean canAdd(Section existingSection, Section newSection) {
        return areOnlyDownStationsMatching(existingSection, newSection) &&
            hasValidDistance(existingSection, newSection);
    }

    private boolean areOnlyDownStationsMatching(Section existingSection, Section newSection) {
        return existingSection.hasSameDownStationWith(newSection.getDownStation()) &&
            !existingSection.hasSameUpStationWith(newSection.getUpStation()) &&
            !existingSection.hasSameDownStationWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        int index = findAdditionSectionIndex(sections, newSection);
        Section existingSection = sections.get(index);
        existingSection.updateDistance(calculateNewDistance(existingSection, newSection));
        sections.add(newSection);
        newSection.updateLine(line);
    }
}

