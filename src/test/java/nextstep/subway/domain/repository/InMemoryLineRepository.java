package nextstep.subway.domain.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import nextstep.subway.domain.model.Line;

public class InMemoryLineRepository implements LineRepository {
    private Map<Long, Line> lines = new HashMap<>();
    private AtomicLong idGenerator = new AtomicLong();

    @Override
    public Optional<Line> findById(Long id) {
        return Optional.ofNullable(lines.get(id));
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    @Override
    public Line save(Line line) {
        if (line.getId() == null) {
            line.generateId(idGenerator.incrementAndGet());
        }

        lines.put(line.getId(), line);
        return line;
    }

    @Override
    public void deleteById(Long id) {
        lines.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return lines.containsKey(id);
    }
}
