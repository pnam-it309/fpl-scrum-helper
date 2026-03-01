package udpm.hn.server.infrastructure.config.global;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.PhaseProject;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Component
public class GlobalVariables {

    private Map<String, Object> globalVariable = new HashMap<>();

    private Map<String, PhaseProject> phaseProjectMap = new HashMap<>();

    public void setGlobalVariable(String key, Object value) {
        globalVariable.put(key, value);
    }

    public Object getGlobalVariable(String key) {
        return globalVariable.get(key);
    }

    public Map<String, PhaseProject> getPhaseProjectMap() {
        return phaseProjectMap;
    }
}
