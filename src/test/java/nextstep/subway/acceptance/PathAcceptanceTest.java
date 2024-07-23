package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.TestFixture.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineRequest;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 강남역;
    private Long 역삼역;
    private Long 교대역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 신논현역;
    private Long 잠실역;
    private Long 이호선;
    private Long 삼호선;
    private Long 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        신논현역 = createStationAndGetId("신논현역");

        교대역 = createStationAndGetId("교대역");
        강남역 = createStationAndGetId("강남역");
        역삼역 = createStationAndGetId("역삼역");

        남부터미널역 = createStationAndGetId("남부터미널역");
        양재역 = createStationAndGetId("양재역");

        잠실역 = createStationAndGetId("논현역");

        신분당선 = createLineAndGetId(new LineRequest("신분당선", "red", 신논현역, 강남역, 11));
        이호선 = createLineAndGetId(new LineRequest("2호선", "green", 교대역, 강남역, 7));
        삼호선 = createLineAndGetId(new LineRequest("3호선", "orange", 교대역, 남부터미널역, 3));

        addSection(이호선, 강남역, 역삼역, 13);
        addSection(삼호선, 남부터미널역, 양재역, 5);
        addSection(신분당선, 강남역, 양재역, 10);
    }

    @Test
    @DisplayName("같은 노선에 존재하는 두 역을 조회하는 경우 경로가 정상적으로 조회된다")
    void findPathInSameLine() {
        // when
        ExtractableResponse<Response> response = getPaths(교대역, 역삼역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(교대역, 강남역, 역삼역);
        assertThat(getResponseDistance(response)).isEqualTo(20);
    }

    @Test
    @DisplayName("출발역에서 한번 환승을 해야 도착역에 다다를 수 있는 경우 경로가 정상적으로 조회된다")
    void findPathWithOneTransfer() {
        // when
        ExtractableResponse<Response> response = getPaths(교대역, 신논현역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(교대역, 강남역, 신논현역);
        assertThat(getResponseDistance(response)).isEqualTo(18);
    }

    @Test
    @DisplayName("출발역에서 두번 환승을 해야 도착역에 다다를 수 있는 경우 경로가 정상적으로 조회된다")
    void findPathWithTwoTransfers() {
        // given
        Long source = 신논현역;
        Long target = 남부터미널역;

        // when
        ExtractableResponse<Response> response = getPaths(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(신논현역, 강남역, 교대역, 남부터미널역);
        assertThat(getResponseDistance(response)).isEqualTo(21);
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경우 역이 하나만 조회된다.")
    void findPathWhenSourceAndTargetAreSame() {
        // given & when
        ExtractableResponse<Response> response = getPaths(강남역, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(강남역);
        assertThat(getResponseDistance(response)).isEqualTo(0);
    }

    @Test
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외가 발생한다")
    void findPathWhenSourceAndTargetAreNotConnected() {
        // given & when
        ExtractableResponse<Response> response = getPaths(강남역, 잠실역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {999L})
    @DisplayName("존재하지 않은 출발역을 조회하는 경우 예외가 발생한다")
    void findPathWhenSourceStationNotFound(Long source) {
        // given & when
        ExtractableResponse<Response> response = getPaths(source, 강남역);

        // then
        int statusCode = response.statusCode();
        assertThat(statusCode).isBetween(400, 599);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {999L})
    @DisplayName("존재하지 않는 도착역을 조회하는 경우 예외가 발생한다")
    void findPathWhenTargetStationNotFound(Long target) {
        // given & when
        ExtractableResponse<Response> response = getPaths(강남역, target);

        // then
        int statusCode = response.statusCode();
        assertThat(statusCode).isBetween(400, 599);
    }

    private List<Long> getResponseStationIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    private int getResponseDistance(ExtractableResponse<Response> response) {
        return response.jsonPath().getInt("distance");
    }

    private static String getResponseMessage(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("message");
    }
}
