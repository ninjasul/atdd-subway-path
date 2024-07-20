package nextstep.subway.application.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;

class AddSectionAfterUpStationStrategyTest {
    private final AddSectionAfterUpStationStrategy strategy = new AddSectionAfterUpStationStrategy();

    @Nested
    @DisplayName("구간 추가 가능 여부 확인")
    class CanAddTests {

        @Test
        @DisplayName("기존 구간과 새 구간의 상행역만 같으면 추가 가능하다")
        void hasSameUpStationOnly() {
            // given
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section existingSection = new Section(null, null, gangnamStation, yeoksamStation, 10);
            Section newSection = new Section(null, null, gangnamStation, seolleungStation, 8);

            // when
            boolean result = strategy.canAdd(existingSection, newSection, 0, 0);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("구간 추가")
    class AddSectionTests {
        @Test
        @DisplayName("새 구간이 기존 구간 상행역 뒤에 추가되면 구간이 기존 구간 앞에 추가된다")
        void addSectionAfterExistingSection() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section existingSection = new Section(line, gangnamStation, seolleungStation, 10);
            line.addSection(existingSection);

            List<Section> sections = line.getSections();

            Section newSection = new Section(line, gangnamStation, yeoksamStation, 5);

            // when
            strategy.addSection(line, sections, newSection);

            List<Section> orderedSections = line.getOrderedUnmodifiableSections();

            // then
            assertThat(orderedSections).contains(newSection);
            assertThat(orderedSections).hasSize(2);
            assertThat(orderedSections).containsExactly(newSection, existingSection);
        }

        @Test
        @DisplayName("구간이 두 개 등록되어 있는 상태에서 새 구간이 첫번째 구간 상행역 뒤에 추가되면 첫번째 구간 이전에 새 구간이 추가된다")
        void addSectionBeforeFirstSection() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");
            Station samsungStation = new Station(4L, "삼성역");

            Section firstSection = new Section(line, gangnamStation, seolleungStation, 10);
            Section secondSection = new Section(line, seolleungStation, samsungStation, 9);
            line.addSection(firstSection);
            line.addSection(secondSection);

            List<Section> sections = line.getSections();

            Section newSection = new Section(line, gangnamStation, yeoksamStation, 8);

            // when
            strategy.addSection(line, sections, newSection);

            List<Section> orderedSections = line.getOrderedUnmodifiableSections();

            // then
            assertThat(orderedSections).contains(newSection);
            assertThat(orderedSections).hasSize(3);
            assertThat(orderedSections).containsExactly(newSection, firstSection, secondSection);
        }

        @Test
        @DisplayName("sections에 구간이 두 개 등록되어 있는 상태에서 새 구간이 두번째 구간 상행역 전에 추가되면 두번째 구간 이전에 구간이 추가된다")
        void addSectionBeforeSecondSection() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");
            Station samsungStation = new Station(4L, "삼성역");

            Section firstSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section secondSection = new Section(line, yeoksamStation, samsungStation, 9);
            line.addSection(firstSection);
            line.addSection(secondSection);

            List<Section> sections = line.getSections();

            Section newSection = new Section(line, yeoksamStation, seolleungStation, 5);

            // when
            strategy.addSection(line, sections, newSection);

            List<Section> orderedSections = line.getOrderedUnmodifiableSections();

            // then
            assertThat(orderedSections).contains(newSection);
            assertThat(orderedSections).hasSize(3);
            assertThat(orderedSections).containsExactly(firstSection, newSection, secondSection);
        }
    }
}
