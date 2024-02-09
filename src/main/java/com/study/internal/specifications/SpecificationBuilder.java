package com.study.internal.specifications;

import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecificationBuilder {

    private String specificationName;

    private List<SearchCriteria> searchCriteriaList = new ArrayList<SearchCriteria>();

    /**
     * Set the attribute specificationName
     */
    public SpecificationBuilder() {
        this.specificationName = Thread.currentThread().getStackTrace()[3].getClassName().split("service.")[1].split("Service")[0];
    }

    /**
     * Search for specifications in the package specification and instantiate the necessaries specifications
     *
     * @return
     */
    public Specification build() {

        Specification specification;

        List<Specification> genericSpecifications = searchCriteriaList.stream().map(param -> (Specification) instantiateSpecification(param)).collect(Collectors.toList());

        specification = genericSpecifications.get(0);
        for (int i = 1; i < searchCriteriaList.size(); i++) {
            specification = Specification.where(specification).and(genericSpecifications.get(i));
        }
        return specification;
    }

    /**
     * Instantiate a specification corresponding of this specificationName when this searchCriteria is not null
     *
     * @param searchCriteria
     * @return Object
     */
    public Object instantiateSpecification(SearchCriteria searchCriteria) {
        try {
            Object o = Class.forName("br.com.rybena.customermanager.entity." + specificationName)
                    .getConstructor().newInstance();
            o.getClass().getMethod("setCriteria", SearchCriteria.class).invoke(o,searchCriteria);
            return o;
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SearchCriteria> getSearchCriteriaList() {
        return searchCriteriaList;
    }

    public void addSearchCriteria(String key, String operation, Object value) {
        this.searchCriteriaList.add(new SearchCriteria(key, operation, value));
    }
}