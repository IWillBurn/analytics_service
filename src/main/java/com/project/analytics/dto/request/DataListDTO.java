package com.project.analytics.dto.request;

import com.project.analytics.dto.postgres.ElementDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DataListDTO {
    private List<DataDTO> data;
    private Long countOfPages;
}
