package nextstep.subway.domain.service;

import java.util.Optional;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;

public interface SectionAdditionStrategy {
    int INVALID_SECTION_INDEX = -1;

    int EMPTY_SECTION_INDEX = 0;

    default boolean canApply(Line line, Section newSection) {
        return findExistingSectionForNewAddition(line.getSections(), newSection).isPresent();
    }

    default Optional<Section> findExistingSectionForNewAddition(Sections sections, Section newSection) {
        if (sections.isEmpty()) {
            return Optional.empty();
        }

        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (canAddToExistingSection(currentSection, newSection, sections.size() - 1, i)) {
                return Optional.of(currentSection);
            }
        }

        return Optional.empty();
    }

    default boolean canAddToExistingSection(Section existingSection, Section newSection, int maxIndex, int index) {
        return false;
    }

    void addSection(Line line, Section newSection);

    default boolean hasValidDistance(Section existingSection, Section newSection) {
        return existingSection.getDistance() > newSection.getDistance();
    }

    default Integer calculateNewDistance(Section existingSection, Section newSection) {
        return existingSection.getDistance() - newSection.getDistance();
    }

    String getFailureCaseMessage();
}
