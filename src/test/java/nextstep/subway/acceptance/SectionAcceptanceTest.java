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


        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * When 지하철 노선에 새로운 구간을 등록하면
         * Then 새로운 구간이 등록된다
         */
        @DisplayName("지하철 노선에 구간을 등록한다")
        @Test
        void testAddSection() {
            // given
            createStation("강남역");
            createStation("역삼역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            createStation("선릉역");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 2L, 3L, 8);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 없는 역을 상행역으로 요청하여
         * When 지하철 노선에 구간을 등록하려 하면
         * Then 구간 등록이 실패한다
         */
        @DisplayName("없는 역을 상행역으로 요청하여 구간 등록 실패")
        @Test
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

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 없는 역을 하행역으로 요청하여
         * When 지하철 노선에 구간을 등록하려 하면
         * Then 구간 등록이 실패한다
         */
        @DisplayName("없는 역을 하행역으로 요청하여 구간 등록 실패")
        @Test
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

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 새로운 구간을 등록하고
         * When 지하철 노선에 기존 구간과 중복된 구간을 등록하면
         * Then 구간 등록이 실패한다
         */
        @DisplayName("지하철 노선에 중복된 구간을 등록할 때 실패한다")
        @Test
        void testAddDuplicateSection() {
            // given
            createStation("강남역");
            createStation("역삼역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 1L, 2L, 10);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 새로운 구간을 등록하고
         * When 지하철 노선에 상행역은 이미 존재하고 하행역은 존재하지 않는 경우 새 구간을 등록하면
         * Then 기존 상행역과 하행역 사이에 구간을 추가한다
         */
        @DisplayName("지하철 노선에 상행역 기준으로 새 구간을 등록할 때 성공한다")
        @Test
        void testAddNewSectionByUpStation() {
            // given
            createStation("강남역");
            createStation("판교역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            createStation("양재역");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 1L, 3L, 2);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        /**
         * Given 지하철 노선을 생성하고
         * And 두 지하철역을 생성하고
         * And 새로운 구간을 등록하고
         * When 지하철 노선에 하행역은 이미 존재하고 상행역은 존재하지 않는 경우 새 구간을 등록하면
         * Then 기존 상행역과 하행역 사이에 구간을 추가한다
         */
        @DisplayName("지하철 노선에 하행역 기준으로 새 구간을 등록할 때 성공한다")
        @Test
        void testAddNewSectionByDownStation() {
            // given
            createStation("강남역");
            createStation("판교역");
            ExtractableResponse<Response> createLineResponse = createLine(new LineRequest("2호선", "bg-red-600", 1L, 2L, 10));
            Long lineId = createLineResponse.jsonPath().getLong("id");

            createStation("양재역");

            // when
            ExtractableResponse<Response> response = addSection(lineId, 2L, 3L, 8);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
