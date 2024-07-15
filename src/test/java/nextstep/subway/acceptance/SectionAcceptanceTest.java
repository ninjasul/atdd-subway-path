package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.*;
import static nextstep.subway.acceptance.TestFixture.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineRequest;

@DisplayName("지하철 구간 관련 기능 인수테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SectionAcceptanceTest {

    @Nested
    @DisplayName("구간 추가")
    class AddSection {
        @Test
        @DisplayName("지하철 노선에 구간을 정상적으로 등록한다")
        void testAddSectionSuccessfully() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 2L, 3L, 8);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        @Test
        @DisplayName("상행역 기준으로 신규 구간을 추가한다")
        void testAddSectionByUpStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 3L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 1L, 2L, 5);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        @Test
        @DisplayName("존재하지 않는 상행역으로 구간을 추가하려고 하면 실패한다")
        void testAddSectionWithNonExistentUpStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 999L, 3L, 8);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        @Test
        @DisplayName("상행역 기준으로 구간 추가 시 기존 구간 거리보다 큰 거리값을 요청하면 실패한다")
        void testAddSectionWithShorterDistanceThanExistingUpStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 3L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 1L, 2L, 15);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        @Test
        @DisplayName("하행역 기준으로 신규 구간을 추가한다")
        void testAddSectionByDownStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 3L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 2L, 3L, 8);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        @Test
        @DisplayName("존재하지 않는 하행역으로 구간을 추가하려고 하면 실패한다")
        void testAddSectionWithNonExistentDownStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 2L, 999L, 8);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        @Test
        @DisplayName("하행역 기준으로 구간 추가 시 기존 구간 거리보다 큰 거리값을 요청하면 실패한다")
        void testAddSectionWithShorterDistanceThanExistingDownStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 3L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 2L, 3L, 15);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        @Test
        @DisplayName("구간 추가 후 마지막 구간에 다시 구간을 추가한다")
        void testAddSectionToEndOfLine() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            createStation("삼성역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");
            addSection(lineId, 2L, 3L, 8);

            // when
            ExtractableResponse<Response> response = addSection(lineId, 3L, 4L, 7);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        @Test
        @DisplayName("상행역과 하행역이 모두 기존 구간에 존재하면 추가를 실패한다")
        void testAddSectionWithBothExistingStations() {
            // given
            createStation("강남역");
            createStation("선릉역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 1L, 2L, 5);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        @Test
        @DisplayName("구간이 이미 존재하는 노선에 구간을 추가하려고 할 때 상행역과 하행역이 모두 기존 구간과 다른 경우 추가를 실패한다")
        void testAddSectionWithBothNonExistingStations() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");
            createStation("삼성역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 4L, 3L, 5);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @Nested
    @DisplayName("구간 제거")
    class RemoveSection {

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 새로운 구간을 등록하고
         * When 지하철 노선의 마지막 역을 제거하면
         * Then 역이 제거된다
         */
        @DisplayName("지하철 노선의 구간을 제거한다")
        @Test
        void testRemoveSection() {
            // given
            createStation("강남역");
            createStation("역삼역");
            createStation("선릉역");

            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");
            addSection(lineId, 2L, 3L, 8);

            // when
            ExtractableResponse<Response> response = removeSection(lineId, 3L);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * When 지하철 노선의 구간이 하나만 존재할 때 구간을 제거하면
         * Then 구간 제거가 실패한다
         */
        @DisplayName("구간이 하나만 존재할 때 구간 제거가 실패한다")
        @Test
        void testRemoveSectionWhenOnlyOneExists() {
            // given
            createStation("강남역");
            createStation("역삼역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = removeSection(lineId, 2L);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 새로운 구간을 등록하고
         * When 없는 상행역을 요청하여 구간을 삭제하면
         * Then 구간 삭제가 실패한다
         */
        @DisplayName("없는 역을 상행역을 요청하여 구간 삭제 실패")
        @Test
        void testRemoveSectionWithNonExistentUpStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = removeSection(lineId, 999L);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 새로운 구간을 등록하고
         * When 없는 하행역을 요청하여 구간을 삭제하면
         * Then 구간 삭제가 실패한다
         */
        @DisplayName("없는 역을 하행역을 요청하여 구간 삭제 실패")
        @Test
        void testRemoveSectionWithNonExistentDownStation() {
            // given
            createStation("강남역");
            createStation("역삼역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = removeSection(lineId, 1000L);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
