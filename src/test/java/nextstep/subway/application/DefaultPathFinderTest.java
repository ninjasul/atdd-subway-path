package nextstep.subway.application;

import static nextstep.subway.application.DefaultPathFinder.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Path;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;

public class DefaultPathFinderTest {
    private DefaultPathFinder pathFinder;

    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;
    private Station 남부터미널역;
    private Station 신논현역;

    @BeforeEach
    public void setUp() {
        Line 이호선 = new Line("2호선", "green");
        Line 삼호선 = new Line("3호선", "orange");

        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");
        신논현역 = new Station("신논현역");

        이호선.addSection(new Section(이호선, 교대역, 강남역, 7));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 5));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 3));

        List<Line> lines = Arrays.asList(이호선, 삼호선);
        pathFinder = new DefaultPathFinder(lines);
    }

    @Test
    @DisplayName("경로 조회 결과가 정상적인 케이스")
    void findPathSuccessful() {
        // given & when
        Path path = pathFinder.findPath(교대역, 역삼역);

        // then
        assertThat(path.getStations()).containsExactly(교대역, 강남역, 역삼역);
        assertThat(path.getDistance()).isEqualTo(12);
    }

    @Test
    @DisplayName("경로 조회가 불가능한 케이스")
    void findPathThrowsException() {
        // given & when & then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 신논현역))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage(PATH_NOT_FOUND_ERROR_MESSAGE);
    }
}
