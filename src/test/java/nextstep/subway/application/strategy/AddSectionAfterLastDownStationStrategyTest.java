package nextstep.subway.application.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;

class AddSectionAfterLastDownStationStrategyTest {
    private final AddSectionAfterLastDownStationStrategy strategy = new AddSectionAfterLastDownStationStrategy();

    @Nested
    @DisplayName("구간 추가 가능 여부 확인")
    class CanAddTests {
        @Test
        @DisplayName("마지막 구간의 하행역과 새 구간의 상행역이 같고 구간이 다를 때 추가 가능하다")
        void canAddTrue() {
            // given
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section existingSection = new Section(null, null, gangnamStation, yeoksamStation, 10);
            Section newSection = new Section(null, null, yeoksamStation, new Station(3L, "선릉역"), 8);

            // when
            boolean result = strategy.canAdd(existingSection, newSection, 0, 0);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("findAdditionSectionIndex() 메소드")
    class FindAdditionSectionIndexTests {

        @Test
        @DisplayName("구간이 비어 있을 때 구간 추가 가능 인덱스가 0이 된다")
        void findAdditionSectionIndexZero() {
            // given
            List<Section> sections = new ArrayList<>();

            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section newSection = new Section(line, gangnamStation, yeoksamStation, 8);

            // when
            int result = strategy.findAdditionSectionIndex(sections, newSection);

            // then
            assertThat(result).isEqualTo(0);
        }
        @Test
        @DisplayName("노선에 구간이 한개 등록되어 있고 마지막 구간 뒤에 새 구간을 추가할 수 있을 때 구간 추가 가능 인덱스가 0이 된다.")
        void findAdditionSectionIndexWithOneSection() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");

            Section firstSecition = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(firstSecition);

            Section newSection = new Section(line, yeoksamStation, new Station(3L, "선릉역"), 8);

            // when
            int result = strategy.findAdditionSectionIndex(line.getSections(), newSection);

            // then
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("노선에 구간이 두 개 이상 등록되어 있고 마지막 구간 뒤에 새 구간을 추가할 수 있을 때 구간 추가 가능 인덱스가 0 보다 크다")
        void findAdditionSectionIndexWithTwoSections() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section firstSecition = new Section(line, gangnamStation, yeoksamStation, 10);
            Section secondSection = new Section(line, yeoksamStation, seolleungStation, 11);
            line.addSection(firstSecition);
            line.addSection(secondSection);

            Section newSection = new Section(line, seolleungStation, new Station(4L, "삼성역"), 8);

            // when
            int result = strategy.findAdditionSectionIndex(line.getSections(), newSection);

            // then
            assertThat(result).isGreaterThan(0);
        }

        @Test
        @DisplayName("새 구간을 추가할 수 없을 때 구간 추가 가능 인덱스가 -1이 된다")
        void findAdditionSectionIndexMinusOne() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");

            Section existingSection = new Section(line, gangnamStation, yeoksamStation, 10);
            List<Section> sections = new ArrayList<>();
            sections.add(existingSection);

            Section newSection = new Section(line, new Station(3L, "선릉역"), new Station(4L, "삼성역"), 8);

            // when
            int result = strategy.findAdditionSectionIndex(sections, newSection);

            // then
            assertThat(result).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("구간 추가")
    class AddSectionTests {

        @Test
        @DisplayName("구간이 비어 있을 때 새 구간을 추가하면 구간이 추가된다")
        void addSectionSuccess() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section newSection = new Section(line, gangnamStation, yeoksamStation, 10);
            List<Section> sections = new ArrayList<>();

            // when
            strategy.addSection(line, sections, newSection);

            // then
            assertThat(sections).contains(newSection);
        }
    }
}