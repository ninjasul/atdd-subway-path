package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionAfterUpStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_AFTER_UP_STATION_FAIL_MESSAGE = "상행역 뒤에";

    @Override
    public boolean canAdd(Section existingSection, Section newSection, int maxIndex, int index) {
        return areOnlyUpStationsSame(existingSection, newSection) &&
            hasValidDistance(existingSection, newSection);
    }

    private boolean areOnlyUpStationsSame(Section existingSection, Section newSection) {
        return existingSection.hasSameUpStationWith(newSection.getUpStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getDownStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        Section existingSection = findExistingSectionForNewAddition(sections, newSection);
        existingSection.updateUpStation(newSection.getDownStation(), calculateNewDistance(existingSection, newSection));
        sections.add(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_AFTER_UP_STATION_FAIL_MESSAGE;
    }
}
