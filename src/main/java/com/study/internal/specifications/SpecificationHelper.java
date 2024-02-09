package com.study.internal.specifications;

import com.study.internal.service.BeanService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class SpecificationHelper {

    /**
     * Do the predicate of search with the operations ':', '>' or '<'
     *
     * @param root
     * @param searchCriteria
     * @param builder
     * @param args
     * @return
     */

    public static final String regex = "(\\w+?)(:|<|>|\\^)((?:[\\wç]*[^\\w,]*)+),";
    static EntityManager manager = BeanService.getBean(EntityManager.class);

    public static Predicate customPredicate(Root<?> root, SearchCriteria searchCriteria, CriteriaBuilder builder,
                                            Object... args) {
        if (searchCriteria.getOperation().equalsIgnoreCase(">")) {

            return builder.greaterThanOrEqualTo(root.<String>get(searchCriteria.getKey()), searchCriteria.getValue().toString());

        } else if (searchCriteria.getOperation().equalsIgnoreCase("<")) {

            return builder.lessThanOrEqualTo(root.<String>get(searchCriteria.getKey()), searchCriteria.getValue().toString());

        } else if (searchCriteria.getOperation().equalsIgnoreCase("^")) {

            return builder.or(Pattern.compile("OR")
                    .splitAsStream(searchCriteria.getKey())
                    .map(name -> builder.like(builder.lower(root.<String>get(name).as(String.class)), searchCriteria.getValue().toString().toLowerCase() + "%"))
                    .toArray(Predicate[]::new));
        } else if (searchCriteria.getOperation().equalsIgnoreCase(":")) {

            if (searchCriteria.getValue().equals("null")) {
                return builder.isNull(root.<String>get(searchCriteria.getKey()));
            }

            if (searchCriteria.getValue().equals("full")) {
                return builder.isNotNull(root.<String>get(searchCriteria.getKey()));
            }

            if (searchCriteria.getValue().equals("true") && searchCriteria.getValue() != null) {
                searchCriteria.setValue(true);
            }

            if (searchCriteria.getValue().equals("false")) {
                searchCriteria.setValue(false);
            }

            if (root.get(searchCriteria.getKey()).getJavaType() == Date.class) {
                return dateCriteria(root, searchCriteria, builder);
            }

            if (manager.getMetamodel().getEntities().stream().anyMatch(m -> m.getName().equals(root.get(searchCriteria.getKey()).getJavaType().getSimpleName()))) {
                try {
                    String relationName = root.get(searchCriteria.getKey()).getJavaType().getSimpleName();
                    System.out.println("\n CRITERIA VALUE: " + searchCriteria.getValue() + " -> CLASS: " + relationName);
                    Long id = Long.valueOf(searchCriteria.getValue().toString());

                    String repositoryName = "br.com.rybena.customermanager.repository." + relationName + "Repository";

                    Object repository = BeanService.getBean(Class.forName(repositoryName));
                    System.out.println("BEAN CLAIMED: " + repositoryName);

                    searchCriteria.setValue(findSearchMethod(repository, id));

                } catch (NumberFormatException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return builder.equal(root.<String>get(searchCriteria.getKey()), searchCriteria.getValue());
        }

        return null;
    }

    /**
     * Get the method "findById" of repository
     *
     * @param repositoryObject
     * @param id
     * @return findById method
     */
    public static Object findSearchMethod(Object repositoryObject, Long id) {
        for (Method method : repositoryObject.getClass().getMethods()) {
            if (method.getName().equals("findById")) {
                try {
                    Object searchMethod = method.invoke(repositoryObject, id);

                    return searchMethod.getClass().getMethod("get").invoke(searchMethod);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                         | NoSuchElementException | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Do the predicate of search with date
     *
     * @param root
     * @param searchCriteria
     * @param builder
     * @return
     */
    public static Predicate dateCriteria(Root<?> root, SearchCriteria searchCriteria, CriteriaBuilder builder) {

        if (searchCriteria.getValue().toString().contains("/")) {
            String[] dates = searchCriteria.getValue().toString().split("/");
            Date since;

            try {
                String format = Character.isDigit(searchCriteria.getValue().toString().charAt(2)) ? "yyyy-MM-dd" : "dd-MM-yyyy";
                since = new SimpleDateFormat(format).parse(dates[0]);
                Date to = new SimpleDateFormat(format).parse(dates[1]);

                return builder.between(root.<Date>get(searchCriteria.getKey()), since, to);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            Date date;

            try {
                String format = Character.isDigit(searchCriteria.getValue().toString().charAt(2)) ? "yyyy-MM-dd" : "dd-MM-yyyy";
                date = new SimpleDateFormat(format).parse(searchCriteria.getValue().toString());
                searchCriteria.setValue(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return builder.equal(root.<String>get(searchCriteria.getKey()), searchCriteria.getValue());
        }
        return null;
    }
}