package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionBeforeDownStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_BEFORE_DOWN_STATION_FAIL_MESSAGE = "하행역 앞에";

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
        Section existingSection = findExistingSectionForNewAddition(sections, newSection);
        existingSection.updateDownStation(newSection.getUpStation(), calculateNewDistance(existingSection, newSection));
        sections.add(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_BEFORE_DOWN_STATION_FAIL_MESSAGE;
    }
}

