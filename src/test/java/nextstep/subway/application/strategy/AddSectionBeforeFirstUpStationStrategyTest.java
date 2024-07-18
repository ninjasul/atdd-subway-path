package nextstep.subway.application.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;

class AddSectionBeforeFirstUpStationStrategyTest {
    private final AddSectionBeforeFirstUpStationStrategy strategy = new AddSectionBeforeFirstUpStationStrategy();

    @Nested
    @DisplayName("구간 추가 가능 여부 확인")
    class CanAddTests {

        @Test
        @DisplayName("기존 구간의 상행역과 새 구간의 하행역이 같고 기존 구간의 역들과 새 구간의 역들이 다를 때 추가 가능하다")
        void canAddTrue() {
            // given
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section existingSection = new Section(null, null, yeoksamStation, seolleungStation, 10);
            Section newSection = new Section(null, null, gangnamStation, yeoksamStation, 8);

            // when
            boolean result = strategy.canAdd(existingSection, newSection, 0, 0);

            // then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @DisplayName("추가할 기존 구간의 index가 -1 또는 1인 경우 추가 불가능하다")
        @ValueSource(ints = {-1, 1})
        void indexNotZero(int index) {
            // given
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section existingSection = new Section(null, null, gangnamStation, yeoksamStation, 10);
            Section newSection = new Section(null, null, yeoksamStation, gangnamStation, 8);

            // when
            boolean result = strategy.canAdd(existingSection, newSection, 0, index);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("기존 구간의 상행역과 새 구간의 하행역이 다를 때 추가 불가능하다")
        void hasSameUpStationWithNewSectionDownStationFalse() {
            // given
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section existingSection = new Section(null, null, gangnamStation, seolleungStation, 10);
            Section newSection = new Section(null, null, yeoksamStation, seolleungStation, 8);

            // when
            boolean result = strategy.canAdd(existingSection, newSection, 0, 0);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("기존 구간의 역들과 새 구간의 상행역이 다를 때 추가 불가능하다")
        void hasDifferentBothStationsWithNewSectionUpStationFalse() {
            // given
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section existingSection = new Section(null, null, gangnamStation, seolleungStation, 10);
            Section newSection = new Section(null, null, yeoksamStation, seolleungStation, 8);

            // when
            boolean result = strategy.canAdd(existingSection, newSection, 0, 0);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("구간 추가")
    class AddSectionTests {

        @Test
        @DisplayName("구간이 하나도 없는 상태에서 새 구간을 추가하면 새 구간이 추가된다")
        void addSectionWhenEmpty() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section newSection = new Section(line, gangnamStation, yeoksamStation, 10);

            List<Section> sections = new ArrayList<>();

            // when
            strategy.addSection(line, sections, newSection);

            // then
            assertThat(sections).containsExactly(newSection);
        }

        @Test
        @DisplayName("구간이 하나 추가되어 있는 상태에서 새 구간을 추가하면 새 구간이 기존 구간 앞에 추가된다")
        void addSectionWhenOneExisting() {
            // given
            Line line = new Line("2호선", "green");

            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section existingSection = new Section(line, yeoksamStation, seolleungStation, 10);
            line.addSection(existingSection);

            List<Section> sections = line.getSections();
            Section newSection = new Section(line, gangnamStation, yeoksamStation, 8);

            // when
            strategy.addSection(line, sections, newSection);

            // then
            assertThat(sections).containsExactly(newSection, existingSection);
        }

        @Test
        @DisplayName("구간이 두 개 추가되어 있는 상태에서 새 구간을 추가하면 새 구간이 기존 구간보다 가장 앞에 추가된다")
        void addSectionWhenTwoExisting() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");
            Station samsungStation = new Station(4L, "삼성역");

            Section firstSection = new Section(line, yeoksamStation, seolleungStation, 10);
            Section secondSection = new Section(line, seolleungStation, samsungStation, 10);
            line.addSection(firstSection);
            line.addSection(secondSection);

            List<Section> sections = line.getSections();

            Section newSection = new Section(line, gangnamStation, yeoksamStation, 8);

            // when
            strategy.addSection(line, sections, newSection);

            // then
            assertThat(sections).containsExactly(newSection, firstSection, secondSection);
        }
    }
}
