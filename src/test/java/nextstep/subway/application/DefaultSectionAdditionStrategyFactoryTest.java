package nextstep.subway.application;

import static nextstep.subway.domain.model.Sections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.application.strategy.AddSectionAfterLastDownStationStrategy;
import nextstep.subway.application.strategy.AddSectionAfterUpStationStrategy;
import nextstep.subway.application.strategy.AddSectionBeforeDownStationStrategy;
import nextstep.subway.application.strategy.AddSectionBeforeFirstUpStationStrategy;
import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;
import nextstep.subway.domain.service.SectionAdditionStrategy;

class DefaultSectionAdditionStrategyFactoryTest {
    private final SectionAdditionStrategy addSectionAfterUpStationStrategy = new AddSectionAfterUpStationStrategy();
    private final SectionAdditionStrategy addSectionBeforeFirstUpStationStrategy = new AddSectionBeforeFirstUpStationStrategy();
    private final SectionAdditionStrategy addSectionBeforeDownStationStrategy = new AddSectionBeforeDownStationStrategy();
    private final SectionAdditionStrategy addSectionAfterLastDownStationStrategy = new AddSectionAfterLastDownStationStrategy();

    private final DefaultSectionAdditionStrategyFactory factory = new DefaultSectionAdditionStrategyFactory(
        List.of(
            addSectionBeforeFirstUpStationStrategy,
            addSectionAfterUpStationStrategy,
            addSectionBeforeDownStationStrategy,
            addSectionAfterLastDownStationStrategy
        )
    );

    @Test
    @DisplayName("기존 구간과 상행역과 새 구간의 하행역만 같을 때 첫번째 상행역 앞에 구간이 새 구간을 추가하는 전략이 선택된다")
    void whenUpStationIsSameAndDownStationIsDifferent() {
        // given
        Line line = new Line("2호선", "green");
        Station gangnamStation = new Station(1L, "강남역");
        Station yeoksamStation = new Station(2L, "역삼역");
        Station seolleungStation = new Station(3L, "선릉역");

        Section section = new Section(line, yeoksamStation, seolleungStation, 10);
        line.addSection(section);

        Section newSection = new Section(line, gangnamStation, yeoksamStation, 8);

        // when
        SectionAdditionStrategy strategy = factory.getStrategy(line, newSection);

        // then
        assertThat(strategy).isEqualTo(addSectionBeforeFirstUpStationStrategy);
    }

    @Test
    @DisplayName("기존 구간과 새 구간의 상행역만 같을 때 기존 구간의 상행역 뒤에 새 구간을 추가하는 전략이 선택된다")
    void whenUpStationsAreSame() {
        // given
        Line line = new Line("2호선", "green");
        Station gangnamStation = new Station(1L, "강남역");
        Station yeoksamStation = new Station(2L, "역삼역");
        Station seolleungStation = new Station(3L, "선릉역");
        Section section = new Section(line, gangnamStation, seolleungStation, 10);
        line.addSection(section);

        Section newSection = new Section(line, gangnamStation, yeoksamStation, 8);

        // when
        SectionAdditionStrategy strategy = factory.getStrategy(line, newSection);

        // then
        assertThat(strategy).isEqualTo(addSectionAfterUpStationStrategy);
    }

    @Test
    @DisplayName("기존 구간과 새 구간의 하행역만 같을 때 기존 구간 하행역 앞에 새 구간을 추가하는 전략이 선택된다")
    void whenDownStationsAreSame() {
        // given
        Line line = new Line("2호선", "green");
        Station gangnamStation = new Station(1L, "강남역");
        Station yeoksamStation = new Station(2L, "역삼역");
        Station seolleungStation = new Station(3L, "선릉역");
        Section section = new Section(line, gangnamStation, seolleungStation, 10);
        line.addSection(section);

        Section newSection = new Section(line, yeoksamStation, seolleungStation, 8);

        // when
        SectionAdditionStrategy strategy = factory.getStrategy(line, newSection);

        // then
        assertThat(strategy).isEqualTo(addSectionBeforeDownStationStrategy);
    }

    @Test
    @DisplayName("기존 구간과 하행역과 새 구간의 상행역만 같을 때 기존 구간 하행역 뒤에 새 구간을 추가하는 전략이 선택된다")
    void whenDownStationIsSameAndUpStationIsDifferent() {
        // given
        Line line = new Line("2호선", "green");
        Station gangnamStation = new Station(1L, "강남역");
        Station yeoksamStation = new Station(2L, "역삼역");
        Station seolleungStation = new Station(3L, "선릉역");
        Section section = new Section(line, gangnamStation, yeoksamStation, 10);
        line.addSection(section);

        Section newSection = new Section(line, yeoksamStation, seolleungStation, 8);

        // when
        SectionAdditionStrategy strategy = factory.getStrategy(line, newSection);

        // then
        assertThat(strategy).isEqualTo(addSectionAfterLastDownStationStrategy);
    }

    @Test
    @DisplayName("기존 구간과 새 구간의 상하행역이 모두 같을 때 전략을 찾지 못한다")
    void whenAllStationsAreSame() {
        // given
        Line line = new Line("2호선", "green");
        Station gangnamStation = new Station(1L, "강남역");
        Station yeoksamStation = new Station(2L, "역삼역");
        Section section = new Section(line, gangnamStation, yeoksamStation, 10);
        line.addSection(section);

        Section newSection = new Section(line, gangnamStation, yeoksamStation, 10);

        // when & then
        assertThatThrownBy(() -> factory.getStrategy(line, newSection))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
    }

    @Test
    @DisplayName("기존 구간과 새 구간의 상하행역이 모두 다를 때 전략을 찾지 못한다")
    void whenAllStationsAreDifferent() {
        // given
        Line line = new Line("2호선", "green");
        Station gangnamStation = new Station(1L, "강남역");
        Station yeoksamStation = new Station(2L, "역삼역");
        Station seolleungStation = new Station(3L, "선릉역");
        Section section = new Section(line, gangnamStation, yeoksamStation, 10);
        line.addSection(section);

        Section newSection = new Section(line, seolleungStation, new Station(4L, "삼성역"), 8);

        // when & then
        assertThatThrownBy(() -> factory.getStrategy(line, newSection))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
    }

    @Test
    @DisplayName("기존 구간과 상행역과 새 구간의 하행역이 같고 기존 구간의 하행역과 새 구간의 상행역이 같을 때 전략을 찾지 못한다")
    void whenUpAndDownStationsAreSame() {
        // given
        Line line = new Line("2호선", "green");
        Station gangnamStation = new Station(1L, "강남역");
        Station yeoksamStation = new Station(2L, "역삼역");
        Section section = new Section(line, gangnamStation, yeoksamStation, 10);
        line.addSection(section);

        Section newSection = new Section(line, yeoksamStation, gangnamStation, 8);

        // when & then
        assertThatThrownBy(() -> factory.getStrategy(line, newSection))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
    }
}
