package nextstep.subway.line.domain;

import java.util.List;

import nextstep.subway.line.application.dto.LineResponse;

public interface LineQueryService {
    LineResponse findLineById(Long id);

    List<LineResponse> findAllLines();
}
