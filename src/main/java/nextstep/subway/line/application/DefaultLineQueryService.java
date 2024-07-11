package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineQueryService;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.application.dto.LineResponse;

@Service
@Transactional(readOnly = true)
public class DefaultLineQueryService implements LineQueryService {
    public static final String LINE_NOT_FOUND_MESSAGE = "노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;

    public DefaultLineQueryService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Override
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public LineResponse findLineById(Long id) {
        Line line = findLineOrElseThrow(id);
        return LineResponse.from(line);
    }

    private Line findLineOrElseThrow(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException(LINE_NOT_FOUND_MESSAGE));
    }
}