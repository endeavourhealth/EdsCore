package org.endeavourhealth.core.database.dal.ehr;

import org.endeavourhealth.common.utility.JsonSerializer;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;

import java.util.Iterator;
import java.util.function.Consumer;

public class ResourceMetadataIterator<T extends ResourceMetadata> implements Iterator<T> {


    private final Iterator<String> rowIterator;
    private final Class<T> classOfT;

    public ResourceMetadataIterator(Iterator<String> rowIterator, Class<T> classOfT) {
        this.rowIterator = rowIterator;
        this.classOfT = classOfT;
    }

    @Override
    public boolean hasNext() {
        return rowIterator.hasNext();
    }

    @Override
    public T next() {
        T result = null;

        String metadata = rowIterator.next();
        if (metadata != null) {
            result = JsonSerializer.deserialize(metadata, classOfT);
        }

        return result;
    }

    @Override
    public void remove() {
        rowIterator.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        throw new UnsupportedOperationException();
    }
}
