package com.study.internal.plugins;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.internal.service.BeanService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public interface Updatable<T> {

    String repositoryPath = "br.com.rybena.admin.repository.";
    final Set<String> ignoredMethods = Set.of("setId", "setDateCreated", "setDateUpdate", "setUserUpdated");

    /**
     * Método responsável por atualizar apenas os valores recebido do JSON em um objeto persistido
     *
     * @param json Json de valores a serem atualizados
     * @return Objeto persitido e atualizado
     */
    default T update(JsonNode json) {
        List<String> methods = Arrays.stream(this.getClass().getMethods()).map(Method::getName).collect(Collectors.toList());

        Iterator<String> fields = json.fieldNames();
        String key;

        while (fields.hasNext()) {
            key = fields.next();
            String methodName = turnToSetMethod(key);

            // Se existe método que está no Json então setamos o novo valor
            if (ignoredMethods.stream().noneMatch(m -> m.equals(methodName)) && methods.contains(methodName)) {
                setNewValues(key, json, this);
            }
        }
        save(this);
        return (T) this;
    }


    /**
     * Método responsável por buscar o método save no repository do objeto, e executá-lo
     * @param object Objeto persistido
     */
    private static void save(Object object) {
        String className = repositoryPath + object.getClass().getSimpleName() + "Repository";

        try {
            Object repository = BeanService.getBean(Class.forName(className));

            Method m = repository.getClass().getMethod("save", Object.class);
            m.invoke(repository, object);

        } catch (ClassNotFoundException | SecurityException | IllegalArgumentException | NoSuchMethodException |
                 IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * Método responsável por buscar o método de recuperar um objeto persistido e executá-lo
     * @param clazz Classe do objeto a ser recuperado
     * @param id Id do objeto a ser recuperado
     * @return Objeto recuperado
     */
    private static Object getOne(Class clazz, Long id) {
        String className = repositoryPath + clazz.getSimpleName() + "Repository";

        try {
            Object repository = BeanService.getBean(Class.forName(className));

            Method m = repository.getClass().getMethod("findById", Object.class);
            Optional op = (Optional) m.invoke(repository, id);
            return op.get();

        } catch (ClassNotFoundException | SecurityException | IllegalArgumentException | NoSuchMethodException |
                 IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Transforma um nome de atributo em um nome de método Set
     * @param key Nome do atributo
     * @return Nome do método Set
     */
    private static String turnToSetMethod(String key) {
        return "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
    }

    /**
     * Busca o método da classe pelo nome
     * @param name Nome do método
     * @param clazz Classe do método a ser encontrado
     * @return Método encontrado
     */
    private static Method getMethod(String name, Class clazz) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equalsIgnoreCase(name)) {
                return method;
            }
        }

        return null;
    }

    /**
     * Método responsável por buscar o método de modificar um valor específico e executá-lo
     *
     * @param key    Atributo a ser modificado
     * @param value  Novo valor do atributo
     * @param object Objeto a ser modificado
     */
    private static void setNewValues(String key, JsonNode value, Object object) {
        String methodName = (turnToSetMethod(key));

        try {
            Method m = getMethod(methodName, object.getClass());
            Object javaObject = deserializeJsonNode(m, value.get(key), key, object);

            m.invoke(object, javaObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método responsável por transformar um objeto em JSON, em um objeto Java
     * @param m Método que está sendo processado
     * @param j Node no json que está sendo processado
     * @param field Atributo que está sendo processado
     * @param root Objeto que está sendo procesado
     * @return Objeto Java relacionado ao node JSON
     * @throws JsonProcessingException Json inválido
     * @throws ParseException Transform de objeto ou data oválido
     */
    private static Object deserializeJsonNode(Method m, JsonNode j, String field, Object root) throws JsonProcessingException, ParseException {
        Class<?> expectedClass = Arrays.stream(m.getParameterTypes()).iterator().next();

        //Se o node se referir a uma data, é necessário parsear ela corretamente.
        if ("java.util.Date".equals(expectedClass.getName()) && !j.isNull()) {
            return new SimpleDateFormat(j.asText().length() > 10 ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd").parse(j.asText());
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            Object t = objectMapper.treeToValue(j, expectedClass);

            //Se o node se referir a uma relação, é necessário converter cada item da relação
            if ("java.util.Set".equals(expectedClass.getName())) {
                try {
                    Field setField = m.getDeclaringClass().getDeclaredField(field);
                    Class<?> genericSetField = (Class<?>) ((ParameterizedType) setField.getGenericType()).getActualTypeArguments()[0];

                    t = ((HashSet<?>) t).stream().map(map -> mapToObject((HashMap) map, genericSetField, root)).collect(Collectors.toSet());

                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

            return t;
        }
    }


    /**
     * Transforma um Map em um objeto JSON
     * @param map Map de String -> String
     * @param clazz Classe que o map será convertido
     * @param root Objeto root
     * @return Objeto java referente ao Map
     */
    private static Object mapToObject(HashMap map, Class clazz, Object root) {
        try {

            Object obj;
            boolean isNew = map.get("id") == null;

            if (isNew) {
                obj = clazz.getConstructor().newInstance();
            } else {

                Long id = ((Integer) map.get("id")).longValue();
                obj = getOne(clazz, id);
            }

            Set<Field> fields = new HashSet<>(Set.of(obj.getClass().getDeclaredFields()));
            Set<Field> superfields = Set.of(obj.getClass().getSuperclass().getDeclaredFields());
            fields.addAll(superfields);

            for (Field field : fields) {
                String methodName = turnToSetMethod(field.getName());
                if (ignoredMethods.stream().noneMatch(m -> m.equals(methodName)) && map.containsKey(field.getName())) {
                    Method m = getMethod(methodName, obj.getClass());
                    Class expectedClass = Arrays.stream(m.getParameterTypes()).iterator().next();

                    Object convertedObject;

                    if ("java.util.Date".equals(expectedClass.getName())) {
                        convertedObject = new SimpleDateFormat(((String) map.get(field.getName())).length() > 10 ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd").parse((String) map.get(field.getName()));
                    } else {
                        ObjectMapper objectMapper = new ObjectMapper();
                        convertedObject = objectMapper.convertValue(map.get(field.getName()), expectedClass);
                    }


                    m.invoke(obj, convertedObject);
                }
            }

            if (isNew) {
                Method m = getMethod(turnToSetMethod(root.getClass().getSimpleName()), obj.getClass());
                m.invoke(obj, root);
            }


            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}