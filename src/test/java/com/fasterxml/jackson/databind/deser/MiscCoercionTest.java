package com.fasterxml.jackson.databind.deser;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;

public class MiscCoercionTest extends BaseMapTest
{
    private final String JSON_EMPTY = quote("");

    private final ObjectMapper VANILLA_MAPPER = sharedMapper();

    private final ObjectMapper COERCING_MAPPER = jsonMapperBuilder()
            .withCoercionConfigDefaults(cfg -> 
                cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();

    /*
    /********************************************************
    /* Tests for collections
    /********************************************************
     */

    public void testScalarCollections() throws Exception
    {
        final JavaType listType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<List<Double>>() { });
        _verifyNoCoercion(listType);
        List<Double> result = _readWithCoercion(listType);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    public void testStringCollections() throws Exception
    {
        final JavaType listType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<List<String>>() { });
        _verifyNoCoercion(listType);
        List<String> result = _readWithCoercion(listType);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /*
    /********************************************************
    /* Tests for Maps
    /********************************************************
     */

    public void testScalarMap() throws Exception
    {
        final JavaType mapType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<Map<Long, Boolean>>() { });
        _verifyNoCoercion(mapType);
        Map<?,?> result = _readWithCoercion(mapType);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    public void testEnumMap() throws Exception
    {
        final JavaType mapType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<EnumMap<ABC, Boolean>>() { });
        _verifyNoCoercion(mapType);
        Map<?,?> result = _readWithCoercion(mapType);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /*
    /********************************************************
    /* Tests for arrays
    /********************************************************
     */

    public void testObjectArray() throws Exception
    {
        final JavaType arrayType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<Object[]>() { });
        _verifyNoCoercion(arrayType);
        Object[] result = _readWithCoercion(arrayType);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    public void testStringArray() throws Exception
    {
        final JavaType arrayType = VANILLA_MAPPER.getTypeFactory()
                .constructType(new TypeReference<String[]>() { });
        _verifyNoCoercion(arrayType);
        String[] result = _readWithCoercion(arrayType);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    public void testBooleanArray() throws Exception
    {
        _verifyNoCoercion(boolean[].class);
        boolean[] result = _readWithCoercion(boolean[].class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    public void testIntArray() throws Exception
    {
        _verifyNoCoercion(int[].class);
        int[] result = _readWithCoercion(int[].class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    public void testLongArray() throws Exception
    {
        _verifyNoCoercion(long[].class);
        long[] result = _readWithCoercion(long[].class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    public void testFloatArray() throws Exception
    {
        _verifyNoCoercion(float[].class);
        float[] result = _readWithCoercion(float[].class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    public void testDoubleArray() throws Exception
    {
        _verifyNoCoercion(double[].class);
        double[] result = _readWithCoercion(double[].class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /*
    /********************************************************
    /* Helper methods
    /********************************************************
     */

    private void _verifyNoCoercion(Class<?> targetType) throws Exception {
        _verifyNoCoercion(VANILLA_MAPPER.constructType(targetType));
    }

    private void _verifyNoCoercion(JavaType targetType) throws Exception {
        try {
            VANILLA_MAPPER.readerFor(targetType).readValue(JSON_EMPTY);
            fail("Should not pass");
        } catch (Exception e) {
            verifyException(e, "Cannot deserialize value of type");
            verifyException(e, "from empty String");
        }
    }

    private <T> T _readWithCoercion(Class<?> targetType) throws Exception {
        return COERCING_MAPPER.readerFor(targetType).readValue(JSON_EMPTY);
    }

    private <T> T _readWithCoercion(JavaType targetType) throws Exception {
        return COERCING_MAPPER.readerFor(targetType).readValue(JSON_EMPTY);
    }
}
