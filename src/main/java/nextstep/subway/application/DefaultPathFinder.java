package nextstep.subway.application;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Path;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;
import nextstep.subway.domain.service.PathFinder;

public class DefaultPathFinder implements PathFinder {
    public static final String PATH_NOT_FOUND_ERROR_MESSAGE = "경로를 찾을 수 없습니다.";

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public DefaultPathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initializeGraph(lines);
    }

    private void initializeGraph(List<Line> lines) {
        for (Line line : lines) {
            for (Section section : line.getOrderedUnmodifiableSections()) {
                graph.addVertex(section.getUpStation());
                graph.addVertex(section.getDownStation());
                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                graph.setEdgeWeight(edge, section.getDistance());
            }
        }
    }

    public Path findPath(Station source, Station target) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        GraphPath<Station, DefaultWeightedEdge> path;
        try {
            path = dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(PATH_NOT_FOUND_ERROR_MESSAGE);
        }

        if (path == null) {
            throw new IllegalStateException(PATH_NOT_FOUND_ERROR_MESSAGE);
        }

        List<Station> stations = path.getVertexList();
        int distance = (int)path.getWeight();

        return new Path(stations, distance);
    }
}
