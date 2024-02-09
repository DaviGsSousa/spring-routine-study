package com.study.internal.utils;

import com.study.internal.specifications.SpecificationBuilder;
import com.study.internal.specifications.SpecificationHelper;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class TableOptions {

    private Long size;
    private Long page;
    private String sort;
    private String order;
    private String params;

    public Pageable toPageable(){
        if (this.page == null) {
            this.page = 0L;
        }

        if (this.size == null) {
            this.size = 10L;
        }

        if (this.sort == null) {
            this.sort = "id";
        }

        PageRequest request = PageRequest.of(this.page.intValue(), this.size.intValue(), Sort.by(sort));

        if (order == null || order.equalsIgnoreCase("asc")) {
            request = PageRequest.of(this.page.intValue(), this.size.intValue(), Sort.by(sort).ascending());
        } else if (order.equalsIgnoreCase("desc")) {
            request = PageRequest.of(this.page.intValue(), this.size.intValue(), Sort.by(sort).descending());
        }

        return request;
    }

    public Specification toSpecification(){
        SpecificationBuilder builder = new SpecificationBuilder();
        Pattern pattern = Pattern.compile(SpecificationHelper.regex);
        Matcher matcher = pattern.matcher(this.params + ",");

        while(matcher.find()){
            builder.addSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        return builder.build();
    }
}