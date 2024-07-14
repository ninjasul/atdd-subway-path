package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.Station;
import nextstep.subway.StationRepository;
import nextstep.subway.line.application.DefaultLineCommandService;
import nextstep.subway.line.application.dto.LineRequest;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.application.dto.SectionResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineCommandService;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;

public class LineCommandServiceMockTest {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private LineCommandService lineCommandService;

    @BeforeEach
    void setUp() {
        lineRepository = mock(LineRepository.class);
        stationRepository = mock(StationRepository.class);
        lineCommandService = new DefaultLineCommandService(lineRepository, stationRepository);
    }

    @Nested
    @DisplayName("노선 추가 기능")
    class AddLine {

        @Test
        @DisplayName("지하철 노선을 생성한다")
        void saveLine() {
            // given
            Station upStation = new Station("강남역");
            Station downStation = new Station("역삼역");
            when(stationRepository.findById(1L)).thenReturn(Optional.of(upStation));
            when(stationRepository.findById(2L)).thenReturn(Optional.of(downStation));

            LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", 1L, 2L, 10);
            Line line = new Line(lineRequest.getName(), lineRequest.getColor());
            when(lineRepository.save(any(Line.class))).thenReturn(line);

            // when
            LineResponse response = lineCommandService.saveLine(lineRequest);

            // then
            assertThat(response.getName()).isEqualTo("2호선");
            verify(lineRepository, times(1)).save(any(Line.class));
        }

    }

    @Nested
    @DisplayName("노선 수정 기능")
    class UpdateLine {

        @Test
        @DisplayName("존재하지 않는 지하철 노선을 수정할 때 실패한다")
        void updateNonExistentLine() {
            // given
            when(lineRepository.findById(999L)).thenReturn(Optional.empty());

            LineRequest updateRequest = new LineRequest("신분당선", "bg-blue-600", 1L, 2L, 10);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.updateLine(999L, updateRequest))
                .withMessage(DefaultLineCommandService.LINE_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("삭제된 지하철 노선을 수정할 때 실패한다")
        void updateDeletedLine() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

            doNothing().when(lineRepository).deleteById(1L);
            LineRequest updateRequest = new LineRequest("신분당선", "bg-blue-600", 1L, 2L, 10);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.updateLine(1L, updateRequest))
                .withMessage(DefaultLineCommandService.LINE_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("지하철 노선을 정상적으로 수정한다")
        void updateLineSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

            LineRequest updateRequest = new LineRequest("신분당선", "bg-blue-600", 1L, 2L, 10);
            when(lineRepository.save(any(Line.class))).thenReturn(line.getUpdated("신분당선", "bg-blue-600"));

            // when
            lineCommandService.updateLine(1L, updateRequest);

            // then
            verify(lineRepository, times(1)).save(any(Line.class));
        }
    }

    @Nested
    @DisplayName("노선 삭제 기능")
    class DeleteLine {

        @Test
        @DisplayName("존재하지 않는 지하철 노선을 삭제할 때 실패한다")
        void deleteNonExistentLine() {
            // given
            when(lineRepository.findById(999L)).thenReturn(Optional.empty());

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.deleteLineById(999L))
                .withMessage(DefaultLineCommandService.LINE_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("이미 삭제된 지하철 노선을 다시 삭제할 때 실패한다")
        void deleteAlreadyDeletedLine() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            when(lineRepository.findById(1L)).thenReturn(Optional.of(line)).thenReturn(Optional.empty());
            doNothing().when(lineRepository).deleteById(1L);

            // 첫 번째 삭제 호출
            lineCommandService.deleteLineById(1L);

            // when // then
            assertThatThrownBy(() -> lineCommandService.deleteLineById(1L))
                .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("지하철 노선을 정상적으로 삭제한다")
        void deleteLineSuccessfully() {
            // given
            when(lineRepository.existsById(1L)).thenReturn(true);
            doNothing().when(lineRepository).deleteById(1L);

            // when
            lineCommandService.deleteLineById(1L);

            // then
            verify(lineRepository, times(1)).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("구간 추가 기능")
    class AddSection {

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
            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(1L)).thenReturn(Optional.of(gangnamStation));
            when(stationRepository.findById(3L)).thenReturn(Optional.of(seolleungStation));

            SectionRequest sectionRequest = new SectionRequest(1L, 3L, 8);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.addSection(1L, sectionRequest))
                .withMessage(Sections.INVALID_UP_STATION_MESSAGE);
        }

        @Test
        @DisplayName("새로운 구간을 추가할 때 하행역이 이미 노선에 존재하면 구간 추가가 실패한다")
        void addSectionWithDuplicateDownStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);
            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(1L)).thenReturn(Optional.of(gangnamStation));
            when(stationRepository.findById(2L)).thenReturn(Optional.of(yeoksamStation));

            SectionRequest sectionRequest = new SectionRequest(2L, 1L, 5);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.addSection(1L, sectionRequest))
                .withMessage(Sections.DUPLICATE_DOWN_STATION_MESSAGE);
        }

        @Test
        @DisplayName("존재하지 않는 역으로 구간을 추가하려고 하면 실패한다")
        void addSectionWithNonExistentStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(999L)).thenReturn(Optional.empty());

            SectionRequest sectionRequest = new SectionRequest(999L, 1L, 5);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.addSection(1L, sectionRequest))
                .withMessage(DefaultLineCommandService.STATION_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("지하철 노선에 구간을 정상적으로 등록한다")
        void addSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(2L)).thenReturn(Optional.of(yeoksamStation));
            when(stationRepository.findById(3L)).thenReturn(Optional.of(seolleungStation));
            when(lineRepository.save(any(Line.class))).thenReturn(line);

            SectionRequest sectionRequest = new SectionRequest(2L, 3L, 8);

            // when
            SectionResponse response = lineCommandService.addSection(1L, sectionRequest);

            // then
            assertThat(response.getUpStationId()).isEqualTo(2L);
            assertThat(response.getDownStationId()).isEqualTo(3L);
        }
    }

    @Nested
    @DisplayName("구간 삭제 기능")
    class DeleteSection {

        @Test
        @DisplayName("구간을 제거할 때 하행 종점역이 아닌 역을 제거하려고 하면 실패한다")
        void removeSectionWithInvalidDownStationFails() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(2L)).thenReturn(Optional.of(yeoksamStation));

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.removeSection(1L, 2L))
                .withMessage(Sections.CANNOT_REMOVE_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("없는 역을 상행역을 요청하여 구간 삭제 실패")
        void removeSectionWithNonExistentUpStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(999L)).thenReturn(Optional.empty());

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.removeSection(1L, 999L))
                .withMessage(DefaultLineCommandService.STATION_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("없는 역을 하행역을 요청하여 구간 삭제 실패")
        void removeSectionWithNonExistentDownStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(1000L)).thenReturn(Optional.empty());

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.removeSection(1L, 1000L))
                .withMessage(DefaultLineCommandService.STATION_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("지하철 노선에 구간을 정상적으로 삭제한다")
        void removeSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(3L)).thenReturn(Optional.of(seolleungStation));

            // when
            lineCommandService.removeSection(1L, 3L);

            // then
            verify(lineRepository, times(1)).save(any(Line.class));
        }
    }
}
