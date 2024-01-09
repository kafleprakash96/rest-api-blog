package org.prkguides.blog.miscellaneous;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.prkguides.blog.dto.PostDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse {

    private List<PostDto> content;

    private int pageNo;

    private int pageSize;

    private Long totalElements;

    private int totalPages;

    private boolean last;  //if the page is last

}
