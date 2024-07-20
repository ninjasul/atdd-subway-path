package nextstep.subway.application.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
public class AddSectionAfterLastDownStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_AFTER_LAST_DOWN_STATION_FAIL_MESSAGE = "마지막 하행역 뒤에";

    @Override
    public boolean canAdd(Section existingSection, Section newSection, int maxIndex, int index) {
        return index == maxIndex &&
            index > INVALID_SECTION_INDEX &&
            existingSection.hasSameDownStationWith(newSection.getUpStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getDownStation());
    }

    @Override
    public void addSection(Line line, List<Section> sections, Section newSection) {
        sections.add(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_AFTER_LAST_DOWN_STATION_FAIL_MESSAGE;
    }
}


