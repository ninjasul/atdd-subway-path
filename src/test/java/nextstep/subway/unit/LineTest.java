package nextstep.subway.unit;

import static nextstep.subway.domain.model.LineColor.*;
import static nextstep.subway.domain.model.LineName.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import nextstep.subway.domain.model.Station;
import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;

public class LineTest {

    @Nested
    @DisplayName("구간 추가 기능")
    class AddSection {

        @Test
        @DisplayName("이미 존재하는 구간을 추가하려고 하면 실패한다")
        void addSectionWithDuplicateSection() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Section section = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(section);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(section))
                .withMessage(Sections.ALREADY_EXISTING_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("새로운 구간을 추가할 때 상행역이 노선의 하행 종점역이 아니면 구간 추가가 실패한다")
        void addSectionWithInvalidUpStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            // when // then
            Section newSection = new Section(line, gangnamStation, seolleungStation, 10);
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(newSection))
                .withMessage(Sections.INVALID_UP_STATION_MESSAGE);
        }

        @Test
        @DisplayName("새로운 구간을 추가할 때 하행역이 이미 노선에 존재하면 구간 추가가 실패한다")
        void addSectionWithDuplicateDownSection() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            // when // then
            Section newSection = new Section(line, yeoksamStation, gangnamStation, 10);

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(newSection))
                .withMessage(Sections.DUPLICATE_DOWN_STATION_MESSAGE);
        }

        @Test
        @DisplayName("새로운 구간을 추가할 때 해당 구간이 올바르면 구간이 추가된다")
        void addSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Section section = new Section(line, gangnamStation, yeoksamStation, 10);

            // when
            line.addSection(section);

            // then
            assertThat(line.getUnmodifiableSections()).contains(section);
        }
    }

    @Nested
    @DisplayName("구간 삭제 기능")
    class RemoveSection {

        @Test
        @DisplayName("구간을 제거할 때 마지막 구간이면 구간 제거가 실패한다")
        void removeLastSection() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.removeSection(yeoksamStation))
                .withMessage(Sections.LAST_SECTION_CANNOT_BE_REMOVED_MESSAGE);
        }

        @Test
        @DisplayName("존재하지 않는 구간을 제거하려고 하면 실패한다")
        void removeNonExistentSectionFails() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section section = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(section);

            Section newSection = new Section(line, yeoksamStation, seolleungStation, 8);
            line.addSection(newSection);

            // when // then
            Station nonExistentStation = new Station("없는역");
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.removeSection(nonExistentStation))
                .withMessage(Sections.CANNOT_REMOVE_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("구간을 제거할 때 하행 종점역이 아닌 역을 제거하려고 하면 실패한다")
        void removeSectionWithInvalidDownStationFails() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additonalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additonalSection);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.removeSection(yeoksamStation))
                .withMessage(Sections.CANNOT_REMOVE_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("구간을 제거할 때 해당 구간이 존재하면 구간이 제거된다")
        void removeSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section section = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additonalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(section);
            line.addSection(additonalSection);

            // when
            line.removeSection(seolleungStation);

            // then
            assertThat(line.getUnmodifiableSections()).contains(section);
        }
    }

    @Nested
    @DisplayName("구간 조회 기능")
    class GetSection {

        @Test
        @DisplayName("노선의 마지막 구간을 조회할 때 마지막 구간이 반환된다")
        void getLastSection() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Section section = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(section);

            // when
            Section lastSection = line.getLastSection();

            // then
            assertThat(lastSection).isEqualTo(section);
        }


        @Test
        @DisplayName("구간 리스트를 조회할 때 변경 불가능한 리스트가 반환된다")
        void getUnmodifiableSections() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");

            Section section = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(section);

            // when
            List<Section> sections = line.getUnmodifiableSections();

            // then
            assertThat(sections).contains(section);

            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> sections.add(
                    new Section(
                        line,
                        new Station("선릉역"),
                        new Station("잠실역"),
                        5
                    )
                )
            );
        }
    }


    @Nested
    @DisplayName("노선 정보 업데이트 기능")
    class UpdateLine {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("노선 정보를 업데이트 할 때 빈 노선 이름을 입력하면 오류가 발생한다")
        void updateLineWithEmptyName(String name) {
            // given
            Line line = new Line("2호선", "bg-red-600");

            // when & then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.getUpdated(name, "bg-orange-100"))
                .withMessage(EMPTY_NAME_ERROR_MESSAGE);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("노선 정보를 업데이트 할 때 빈 노선 색상을 입력하면 오류가 발생한다")
        void updateLineWithEmptyColor(String color) {
            // given
            Line line = new Line("2호선", "bg-red-600");

            // when & then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.getUpdated("3호선", color))
                .withMessage(EMPTY_COLOR_ERROR_MESSAGE);
        }

        @Test
        @DisplayName("노선 정보를 업데이트 할 때 올바른 이름과 색상을 주면 업데이트가 성공한다")
        void updateLineSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            // when
            Line updatedLine = line.getUpdated("3호선", "bg-blue-600");

            // then
            assertThat(updatedLine.getName()).isEqualTo("3호선");
            assertThat(updatedLine.getColor()).isEqualTo("bg-blue-600");
        }

    }
}
