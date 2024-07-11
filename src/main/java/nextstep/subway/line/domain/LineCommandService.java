package nextstep.subway.line.domain;

import nextstep.subway.line.application.dto.LineRequest;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.application.dto.SectionResponse;

public interface LineCommandService {
    LineResponse saveLine(LineRequest lineRequest);

    void updateLine(Long id, LineRequest lineRequest);

    void deleteLineById(Long id);

    SectionResponse addSection(Long lineId, SectionRequest sectionRequest);

    void removeSection(Long lineId, Long stationId);
}
