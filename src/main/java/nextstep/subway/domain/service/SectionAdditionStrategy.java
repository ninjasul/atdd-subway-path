package nextstep.subway.domain.service;

import java.util.List;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;

public interface SectionAdditionStrategy {
    boolean canApply(Sections sections, Section newSection);

    void addSection(Line line, List<Section> sections, Section newSection);

    default boolean hasValidDistance(Section oldSection, Section newSection) {
        return oldSection.getDistance() > newSection.getDistance();
    }
}
