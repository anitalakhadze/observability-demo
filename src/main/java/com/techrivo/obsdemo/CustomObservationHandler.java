package com.techrivo.obsdemo;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomObservationHandler implements ObservationHandler<Observation.Context> {

    @Override
    public void onStart(Observation.Context context) {
        context.addLowCardinalityKeyValue(KeyValue.of("Observability", "Test"));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

}
