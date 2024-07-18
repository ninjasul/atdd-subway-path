package nextstep.subway.application;

import static nextstep.subway.domain.model.Sections.*;
import static nextstep.subway.domain.service.SectionAdditionStrategy.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;
import nextstep.subway.domain.service.SectionAdditionStrategyFactory;

@Component
public class DefaultSectionAdditionStrategyFactory implements SectionAdditionStrategyFactory {
    private static final String FAILURE_CASE_MESSAGE_DELIMITER = ", ";

    private final List<SectionAdditionStrategy> strategies;

    public DefaultSectionAdditionStrategyFactory(List<SectionAdditionStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public SectionAdditionStrategy getStrategy(Line line, Section section) {
        List<Section> sections = line.getSections();

        List<String> failureCaseMessages = new ArrayList<>();
        for (SectionAdditionStrategy strategy : strategies) {
            int index = strategy.findAdditionSectionIndex(sections, section);
            if (index > INVALID_SECTION_INDEX) {
                return strategy;
            } else {
                failureCaseMessages.add(strategy.getFailureCaseMessage());
            }
        }

        throw new IllegalArgumentException(getConcatenatedFailureMessage(failureCaseMessages));
    }

    private static String getConcatenatedFailureMessage(List<String> failureCaseMessages) {
        return String.format("%s %s", String.join(FAILURE_CASE_MESSAGE_DELIMITER, failureCaseMessages), CANNOT_ADD_SECTION_MESSAGE);
    }
}
