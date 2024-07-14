package nextstep.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.Station;
import nextstep.subway.StationRepository;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineCommandService;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.application.dto.LineRequest;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.application.dto.SectionResponse;

@Service
@Transactional
public class DefaultLineCommandService implements LineCommandService {
    public static final String LINE_NOT_FOUND_MESSAGE = "노선을 찾을 수 없습니다.";
    public static final String STATION_NOT_FOUND_MESSAGE = "역을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public DefaultLineCommandService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public LineResponse saveLine(LineRequest lineRequest) {
        final Station upStation = findStationOrElseThrow(lineRequest.getUpStationId());
        final Station downStation = findStationOrElseThrow(lineRequest.getDownStationId());
        final Section section = new Section.SectionBuilder()
            .upStation(upStation)
            .downStation(downStation)
            .distance(lineRequest.getDistance())
            .build();

        Line line = new Line(
            lineRequest.getName(),
            lineRequest.getColor()
        );

        line.addSection(section);

        Line savedLine = lineRepository.save(line);
        return LineResponse.from(savedLine);
    }

    @Override
    public void updateLine(Long id, LineRequest lineRequest) {
        lineRepository.save(findLineOrElseThrow(id).getUpdated(lineRequest.getName(), lineRequest.getColor()));
    }

    @Override
    public void deleteLineById(Long id) {
        if (!lineRepository.existsById(id)) {
            throw new IllegalArgumentException(LINE_NOT_FOUND_MESSAGE);
        }
        lineRepository.deleteById(id);
    }

    @Override
    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineOrElseThrow(lineId);
        Station upStation = findStationOrElseThrow(sectionRequest.getUpStationId());
        Station downStation = findStationOrElseThrow(sectionRequest.getDownStationId());

        Section section = new Section.SectionBuilder()
            .line(line)
            .upStation(upStation)
            .downStation(downStation)
            .distance(sectionRequest.getDistance())
            .build();

        line.addSection(section);

        lineRepository.save(line);
        return SectionResponse.from(section);
    }

    @Override
    public void removeSection(Long lineId, Long stationId) {
        Line line = findLineOrElseThrow(lineId);
        Station station = findStationOrElseThrow(stationId);

        line.removeSection(station);
        lineRepository.save(line);
    }

    private Line findLineOrElseThrow(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException(LINE_NOT_FOUND_MESSAGE));
    }

    private Station findStationOrElseThrow(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException(STATION_NOT_FOUND_MESSAGE));
    }
}
