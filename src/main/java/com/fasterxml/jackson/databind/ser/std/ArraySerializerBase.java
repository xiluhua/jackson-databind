package com.fasterxml.jackson.databind.ser.std;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.*;

/**
 * Intermediate base class for serializers used for various Java arrays.
 * 
 * @param <T> Type of arrays serializer handles
 */
public abstract class ArraySerializerBase<T>
    extends ContainerSerializer<T>
{
    /**
     * Setting for specific local override for "unwrap single element arrays":
     * true for enable unwrapping, false for preventing it, `null` for using
     * global configuration.
     */
    protected final Boolean _unwrapSingle;

    protected ArraySerializerBase(Class<T> cls)
    {
        super(cls);
        _unwrapSingle = null;
    }

    protected ArraySerializerBase(ArraySerializerBase<?> src) {
        super(src);
        _unwrapSingle = src._unwrapSingle;
    }

    protected ArraySerializerBase(ArraySerializerBase<?> src, BeanProperty property,
            Boolean unwrapSingle)
    {
        super(src, property);
        _unwrapSingle = unwrapSingle;
    }

    public abstract JsonSerializer<?> _withResolved(BeanProperty prop,
            Boolean unwrapSingle);

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializers,
            BeanProperty property)
    {
        Boolean unwrapSingle = null;

        // First: if we have a property, may have property-annotation overrides
        if (property != null) {
            JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
            if (format != null) {
                unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
                if (!Objects.equals(unwrapSingle, _unwrapSingle)) {
                    return _withResolved(property, unwrapSingle);
                }
            }
        }
        return this;
    }

    @Override
    public final void serializeWithType(T value, JsonGenerator g, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonToken.START_ARRAY));
        // [databind#631]: Assign current value, to be accessible by custom serializers
        g.assignCurrentValue(value);
        serializeContents(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    protected abstract void serializeContents(T value, JsonGenerator jgen, SerializerProvider provider)
        throws JacksonException;

    protected final boolean _shouldUnwrapSingle(SerializerProvider provider) {
        if (_unwrapSingle == null) {
            return provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        }
        return _unwrapSingle.booleanValue();
    }
}
