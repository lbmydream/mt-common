package com.mt.common.serializer;

import com.github.fge.jsonpatch.JsonPatch;

import java.util.List;

public interface CustomObjectSerializer {
    <T> String serialize(T object);

    byte[] nativeSerialize(Object object);

    <T> T deepCopy(T object);

    <T> T nativeDeepCopy(T object);

    <T> List<T> deepCopy(List<T> object);

    <T> T deserialize(String str);

    Object nativeDeserialize(byte[] bytes);

    <T> T applyJsonPatch(JsonPatch command, T beforePatch, Class<T> clazz);
}