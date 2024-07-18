package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionBeforeDownStationStrategy implements SectionAdditionStrategy {
    @Override
    public boolean canAdd(Section existingSection, Section newSection, int maxIndex, int index) {
        return areOnlyDownStationSame(existingSection, newSection) &&
            hasValidDistance(existingSection, newSection);
    }

    private boolean areOnlyDownStationSame(Section existingSection, Section newSection) {
        return existingSection.hasSameDownStationWith(newSection.getDownStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        int index = findAdditionSectionIndex(sections, newSection);
        Section existingSection = sections.get(index);
        existingSection.updateDownStation(newSection.getUpStation(), calculateNewDistance(existingSection, newSection));
        addNewSection(sections, index, newSection);
    }

    private void addNewSection(List<Section> sections, int index, Section newSection) {
        if (index == sections.size() - 1) {
            sections.add(newSection);
            return;
        }

        int actualAdditionIndex = Math.min(index + 1, sections.size() - 1);
        sections.add(actualAdditionIndex, newSection);
    }
}

