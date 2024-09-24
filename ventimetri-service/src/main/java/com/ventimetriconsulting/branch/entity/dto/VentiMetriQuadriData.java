package com.ventimetriconsulting.branch.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@ToString
public class VentiMetriQuadriData {

    List<BranchResponseEntity> branches;
}
