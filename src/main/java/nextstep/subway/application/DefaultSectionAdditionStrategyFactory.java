package nextstep.subway.application;

import static nextstep.subway.domain.model.Sections.*;

import java.util.List;

import org.springframework.stereotype.Component;

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
        List<Section> sections = line.getSections();

        return strategies.stream()
            .filter(strategy -> strategy.findAdditionSectionIndex(sections, section) > -1)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(CANNOT_ADD_SECTION_MESSAGE));
    }
}
