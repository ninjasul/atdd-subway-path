package nextstep.subway.line.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;

public interface JpaLineRepository extends LineRepository, JpaRepository<Line, Long> {
}
