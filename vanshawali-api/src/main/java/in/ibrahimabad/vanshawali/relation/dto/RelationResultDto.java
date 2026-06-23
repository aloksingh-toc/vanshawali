package in.ibrahimabad.vanshawali.relation.dto;

import java.util.List;

public record RelationResultDto(
        Long fromId,
        String fromName,
        Long toId,
        String toName,
        Long commonAncestorId,
        String commonAncestorName,
        int generationGap,
        int depthFrom,
        int depthTo,
        String relationLabel,
        List<String> pathFrom,
        List<String> pathTo) {
}
