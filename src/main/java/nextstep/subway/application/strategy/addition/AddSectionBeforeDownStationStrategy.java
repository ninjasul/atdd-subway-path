package nextstep.subway.application.strategy.addition;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
@Order(4)
public class AddSectionBeforeDownStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_BEFORE_DOWN_STATION_FAIL_MESSAGE = "하행역 앞에";

    @Override
    public boolean canAddToExistingSection(Section existingSection, Section newSection, int maxIndex, int index) {
        return areOnlyDownStationSame(existingSection, newSection) &&
            hasValidDistance(existingSection, newSection);
    }

    private boolean areOnlyDownStationSame(Section existingSection, Section newSection) {
        return existingSection.hasSameDownStationWith(newSection.getDownStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, Section newSection) {
        Section existingSection = findExistingSectionForNewAddition(line.getSections(), newSection).get();
        existingSection.updateDownStation(newSection.getUpStation(), calculateNewDistance(existingSection, newSection));
        line.addSection(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_BEFORE_DOWN_STATION_FAIL_MESSAGE;
    }
}

