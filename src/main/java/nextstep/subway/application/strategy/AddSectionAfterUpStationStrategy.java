package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionByUpStationStrategy implements SectionAdditionStrategy {

    @Override
    public boolean canAdd(Section existingSection, Section newSection) {
        return areOnlyUpStationsMatching(existingSection, newSection) &&
            hasValidDistance(existingSection, newSection);
    }

    private boolean areOnlyUpStationsMatching(Section existingSection, Section newSection) {
        return existingSection.hasSameUpStationWith(newSection.getUpStation()) &&
            !existingSection.hasSameDownStationWith(newSection.getDownStation()) &&
            !existingSection.hasSameDownStationWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        int index = findAdditionSectionIndex(sections, newSection);
        Section existingSection = sections.get(index);
        existingSection.updateUpStation(newSection.getDownStation(), calculateNewDistance(existingSection, newSection));
        newSection.updateLine(line);
        sections.add(index, newSection);
    }
}
