package org.endeavourhealth.core.fhirStorage;

import org.endeavourhealth.common.cache.ParserPool;
import org.endeavourhealth.core.fhirStorage.exceptions.SerializationException;
import org.hl7.fhir.instance.model.Resource;

public class FhirSerializationHelper {
    private static final ParserPool PARSER_POOL = new ParserPool();
    public static String serializeResource(Resource resource) throws SerializationException {
        try {
            return PARSER_POOL.composeString(resource);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    public static Resource deserializeResource(String resourceAsJsonString) throws SerializationException {
        try {
            return PARSER_POOL.parse(resourceAsJsonString);
        } catch (Exception e) {

            //nasty hack to get around JSON that was saved without escaping the \\ before double quotes that MySQL needs
            //data is being fixed, but in the short-term this will handle the missing \\ chars
            String msg = e.getMessage();
            String prefix = "Unterminated object at line 1 column ";
            int index = msg.indexOf(prefix);
            if (index > -1) {
                String suffix = msg.substring(index + prefix.length());
                index = suffix.indexOf(" ");
                String sub = suffix.substring(0, index);
                int pos = Integer.parseInt(sub);

                String jsonBefore = resourceAsJsonString.substring(0, pos - 3);
                String jsonAfter = resourceAsJsonString.substring(pos - 2);

                int nextPos = jsonAfter.indexOf("\"");
                if (nextPos > -1) {
                    String middle = jsonAfter.substring(0, nextPos);
                    jsonAfter = jsonAfter.substring(nextPos+1);

                    resourceAsJsonString = jsonBefore + "\\\"" + middle + "\\\"" + jsonAfter;
                    return deserializeResource(resourceAsJsonString);
                }
            }

            throw new SerializationException(e);
        }
    }
}
