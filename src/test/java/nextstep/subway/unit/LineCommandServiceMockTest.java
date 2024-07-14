package nextstep.subway.unit;

import static nextstep.subway.application.DefaultLineCommandService.*;
import static nextstep.subway.domain.model.Sections.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.model.Station;
import nextstep.subway.domain.repository.StationRepository;
import nextstep.subway.application.DefaultLineCommandService;
import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.application.dto.SectionResponse;
import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.service.LineCommandService;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;

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
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");

            when(stationRepository.findById(1L)).thenReturn(Optional.of(gangnamStation));
            when(stationRepository.findById(2L)).thenReturn(Optional.of(yeoksamStation));

            String lineName = "2호선";
            String lineColor = "bg-red-600";

            LineRequest lineRequest = new LineRequest(lineName, lineColor, 1L, 2L, 10);
            Line line = new Line(1L, lineRequest.getName(), lineRequest.getColor());
            when(lineRepository.save(any(Line.class))).thenReturn(line);

            // when
            LineResponse response = lineCommandService.saveLine(lineRequest);

            // then
            assertThat(response.getName()).isEqualTo(lineName);
            assertThat(response.getColor()).isEqualTo(lineColor);

            verify(lineRepository, times(1)).save(any(Line.class));
        }

        @Test
        @DisplayName("존재하지 않는 지하철 역으로 노선을 생성하려고 하면 실패한다")
        void saveLineWithNonExistentStation() {
            // given
            Station gangnamStation = new Station(1L, "강남역");

            when(stationRepository.findById(1L)).thenReturn(Optional.of(gangnamStation));
            when(stationRepository.findById(2L)).thenReturn(Optional.empty());

            LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", 1L, 2L, 10);

            // when & then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.saveLine(lineRequest))
                .withMessage(STATION_NOT_FOUND_MESSAGE);
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
                .withMessage(LINE_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("삭제된 지하철 노선을 수정할 때 실패한다")
        void updateDeletedLine() {
            // given
            when(lineRepository.existsById(1L)).thenReturn(true).thenReturn(false);
            doNothing().when(lineRepository).deleteById(1L);

            LineRequest updateRequest = new LineRequest("신분당선", "bg-blue-600", 1L, 2L, 10);

            lineCommandService.deleteLineById(1L);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.updateLine(1L, updateRequest))
                .withMessage(LINE_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("지하철 노선을 정상적으로 수정한다")
        void updateLineSuccessfully() {
            // given
            Line line = new Line(1L, "2호선", "bg-red-600");
            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

            LineRequest updateRequest = new LineRequest("신분당선", "bg-blue-600", 1L, 2L, 10);
            when(lineRepository.save(any(Line.class))).thenReturn(line.getUpdated(updateRequest.getName(), updateRequest.getColor()));

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
                .withMessage(LINE_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("이미 삭제된 지하철 노선을 다시 삭제할 때 실패한다")
        void deleteAlreadyDeletedLine() {
            // given
            when(lineRepository.existsById(1L)).thenReturn(true).thenReturn(false);

            doNothing().when(lineRepository).deleteById(1L);

            // 첫 번째 삭제 호출
            lineCommandService.deleteLineById(1L);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.deleteLineById(1L))
                .withMessage(LINE_NOT_FOUND_MESSAGE);
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
        @DisplayName("존재하지 않는 역으로 구간을 추가하려고 하면 실패한다")
        void addSectionWithNonExistentStation() {
            // given
            Line line = new Line(1L,  "2호선", "bg-red-600");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(999L)).thenReturn(Optional.empty());

            SectionRequest sectionRequest = new SectionRequest(999L, 1L, 5);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.addSection(1L, sectionRequest))
                .withMessage(STATION_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("지하철 노선에 구간을 정상적으로 등록한다")
        void addSectionSuccessfully() {
            // given
            Line line = new Line(1L, "2호선", "bg-red-600");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

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
            verify(lineRepository, times(1)).save(any(Line.class));
        }
    }

    @Nested
    @DisplayName("구간 삭제 기능")
    class DeleteSection {

        @Test
        @DisplayName("구간을 제거할 때 하행 종점역이 아닌 역을 제거하려고 하면 실패한다")
        void removeSectionWithInvalidDownStationFails() {
            // given
            Line line = new Line(1L,  "2호선", "bg-red-600");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(2L)).thenReturn(Optional.of(yeoksamStation));

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.removeSection(1L, 2L))
                .withMessage(CANNOT_REMOVE_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("구간 삭제 시 없는 상행역을 요청하면 구간 삭제가 실패한다")
        void removeSectionWithNonExistentUpStation() {
            // given
            Line line = new Line(1L,  "2호선", "bg-red-600");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(999L)).thenReturn(Optional.empty());

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.removeSection(1L, 999L))
                .withMessage(STATION_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("구간 삭제 시 없는 하행역을 요청하면 구간 삭제가 실패한다")
        void removeSectionWithNonExistentDownStation() {
            // given
            Line line = new Line(1L,  "2호선", "bg-red-600");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
            when(stationRepository.findById(1000L)).thenReturn(Optional.empty());

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> lineCommandService.removeSection(1L, 1000L))
                .withMessage(STATION_NOT_FOUND_MESSAGE);
        }

        @Test
        @DisplayName("지하철 노선에 존재하는 구간을 정상적으로 삭제한다")
        void removeSectionSuccessfully() {
            // given
            Line line = new Line(1L,  "2호선", "bg-red-600");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

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
