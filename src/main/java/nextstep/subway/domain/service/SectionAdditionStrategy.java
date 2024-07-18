package nextstep.subway.domain.service;

import java.util.List;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;

public interface SectionAdditionStrategy {
    default Integer findAdditionSectionIndex(List<Section> sections, Section newSection) {
        if (sections.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (canAdd(currentSection, newSection, sections.size() - 1, i)) {
                return i;
            }
        }

        return -1;
    }

    boolean canAdd(Section existingSection, Section newSection, int maxIndex, int index);

    void addSection(Line line, List<Section> sections, Section newSection);

    default boolean hasValidDistance(Section existingSection, Section newSection) {
        return existingSection.getDistance() > newSection.getDistance();
    }

    default Integer calculateNewDistance(Section existingSection, Section newSection) {
        return existingSection.getDistance() - newSection.getDistance();
    }
}
