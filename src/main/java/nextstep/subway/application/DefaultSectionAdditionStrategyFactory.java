package nextstep.subway.application;

import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.application.strategy.AddSectionByDownStationStrategy;
import nextstep.subway.application.strategy.AddSectionByUpStationStrategy;
import nextstep.subway.application.strategy.AddSectionToLastStationStrategy;
import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.service.SectionAdditionStrategy;
import nextstep.subway.domain.service.SectionAdditionStrategyFactory;

@Component
public class DefaultSectionAdditionStrategyFactory implements SectionAdditionStrategyFactory {
    private final List<SectionAdditionStrategy> strategies;

    public DefaultSectionAdditionStrategyFactory(List<SectionAdditionStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public SectionAdditionStrategy getStrategy(Line line, Section section) {
        Sections sections = line.getSections();

        if (sections.contains(section)) {
            throw new IllegalArgumentException(Sections.ALREADY_EXISTING_SECTION_MESSAGE);
        }

        return strategies.stream()
            .filter(strategy -> strategy.canApply(sections, section))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(Sections.CANNOT_ADD_SECTION_MESSAGE));
    }
}
